function WebswingDirectDraw(c) {
	"use strict";

	if (c.canvas == null) {
		return null;
	}

	var config = {
		onErrorMessage : c.onErrorMessage || function(message) {
			console.log(message);
		},
		canvas : c.canvas,
		proto : c.proto,
		constantPool : c.constantPool != null ? c.constantPool : [],
		imagePool : c.imagePool != null ? c.imagePool : []
	};

	var WebImageProto = c.proto.build("org.webswing.directdraw.proto.WebImageProto");
	var InstructionProto = c.proto.build("org.webswing.directdraw.proto.DrawInstructionProto.InstructionProto");
	var SegmentTypeProto = c.proto.build("org.webswing.directdraw.proto.PathProto.SegmentTypeProto");
	var ArcTypeProto = c.proto.build("org.webswing.directdraw.proto.ArcProto.ArcTypeProto");
	var CyclicMethodProto = c.proto.build("org.webswing.directdraw.proto.CyclicMethodProto");
	var StrokeJoinProto = c.proto.build("org.webswing.directdraw.proto.StrokeProto.StrokeJoinProto");
	var StrokeCapProto = c.proto.build("org.webswing.directdraw.proto.StrokeProto.StrokeCapProto");
	var StyleProto = c.proto.build("org.webswing.directdraw.proto.FontProto.StyleProto");
	var MAX_GRADIENT_CYCLE_REPEAT_COUNT = 30;
	var ctx;
	var constantPoolCache = config.constantPool;
	var imagePoolCache = config.imagePool;
	var graphicsStates = {};
	var currentStateId;

	function draw64(data) {
		var image = WebImageProto.decode64(data);
		return drawWebImage(image);
	}

	function drawBin(data) {
		var image = WebImageProto.decode(data);
		return drawWebImage(image);
	}

	function drawWebImage(image) {
		return new Promise(function(resolve, reject) {
			try {
				var imagesToPrepare = [];
				populateConstantsPool(image, imagesToPrepare);

				prepareImages(imagesToPrepare).then(function(preloadedImages) {
					ctx = config.canvas.getContext("2d");
					config.canvas.width = config.canvas.width;
					if (image.instructions != null) {
						image.instructions.forEach(function(instruction) {
							interpretInstruction(ctx, instruction, preloadedImages);
						});
					}
					cleanupImages(preloadedImages);
					cleanupCanvas(ctx);
					resolve(config.canvas);
				}, function(error) {
					config.onErrorMessage(error);
				});
			} catch (e) {
				config.onErrorMessage(error);
				reject(e);
			}
		});
	}

	function populateConstantsPool(image, imagesToPrepare) {
		image.constants.forEach(function(constant, index) {
			constantPoolCache[constant.id] = constant;
		});
		image.imageConstants.forEach(function(constant, index) {
			imagePoolCache[constant.id] = constant;
		});
		if (image.instructions != null) {
			image.instructions.forEach(function(instruction, index) {
				if (instruction.inst == InstructionProto.DRAW_IMAGE) {
					if (imagePoolCache[instruction.args[0]].image != null) {
						imagesToPrepare.push(imagePoolCache[instruction.args[0]].image);
					}
				}
				if (instruction.inst == InstructionProto.SET_PAINT) {
					if (constantPoolCache[instruction.args[0]].image != null) {
						imagesToPrepare.push(constantPoolCache[instruction.args[0]].image);
					}
				}
				if (instruction.inst == InstructionProto.DRAW_WEBIMAGE) {
					instruction.webImage = WebImageProto.decode(instruction.webImage);
					populateConstantsPool(instruction.webImage, imagesToPrepare);
				}
			});
		}
	}

	function interpretInstruction(ctx, instruction, preloadedImages) {
		var instCode = instruction.inst;
		var args = resolveArgs(instruction.args, constantPoolCache);
		switch (instCode) {
		case InstructionProto.GRAPHICS_CREATE:
			iprtGraphicsCreate(ctx, instruction.args[0], instruction.args[1]);
			break;
		case InstructionProto.GRAPHICS_SWITCH:
			iprtGraphicsSwitch(ctx, instruction.args[0], preloadedImages);
			break;
		case InstructionProto.GRAPHICS_DISPOSE:
			delete graphicsStates[instruction.args[0]];
			break;
		case InstructionProto.DRAW:
			iprtDraw(ctx, args);
			break;
		case InstructionProto.FILL:
			iprtFill(ctx, args);
			break;
		case InstructionProto.DRAW_IMAGE:
			iprtDrawImage(ctx, args, imagePoolCache[instruction.args[0]], preloadedImages);
			break;
		case InstructionProto.DRAW_WEBIMAGE:
			iprtDrawWebImage(ctx, args, instruction.webImage, preloadedImages);
			break;
		case InstructionProto.DRAW_STRING:
			iprtDrawString(ctx, args);
			break;
		case InstructionProto.COPY_AREA:
			iprtCopyArea(ctx, args);
			break;
		case InstructionProto.SET_STROKE:
			iprtSetStroke(ctx, args);
			graphicsStates[currentStateId].strokeArgs = args;
			break;
		case InstructionProto.SET_PAINT:
			iprtSetPaint(ctx, args, preloadedImages);
			graphicsStates[currentStateId].paintArgs = args;
			break;
		case InstructionProto.TRANSFORM:
			var tx = iprtTransform(ctx, args);
			graphicsStates[currentStateId].transformArgs = concatTransform(graphicsStates[currentStateId].transformArgs, tx);
			break;
		default:
			console.log("instCode:" + instCode + " not recognized");
		}
	}

	function iprtGraphicsSwitch(ctx, id, preloadedImages) {
		if (graphicsStates[id] != null) {
			if (graphicsStates[id].strokeArgs != null) {
				iprtSetStroke(ctx, graphicsStates[id].strokeArgs);
			}
			if (graphicsStates[id].paintArgs != null) {
				iprtSetPaint(ctx, graphicsStates[id].paintArgs, preloadedImages);
			}
			if (graphicsStates[id].transformArgs != null) {
				var m = graphicsStates[id].transformArgs;
				ctx.setTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
			}
		} else {
			console.log("Graphics with id " + id + " not initialized!");
		}
		currentStateId = id;
	}

	function iprtGraphicsCreate(ctx, thisId, parentId) {
		if (graphicsStates[thisId] == null) {
			graphicsStates[thisId] = {};
			if (parentId == null) {
				graphicsStates[thisId].strokeArgs = [ {
					stroke : {
						widthX10 : 10,
						miterLimitX10 : 100,
						cap : StrokeCapProto.CAP_SQUARE,
						join : StrokeJoinProto.JOIN_MITER
					}
				} ];
				graphicsStates[thisId].paintArgs = [ {
					color : {
						rgba : 0x000000FF
					}
				} ];
				graphicsStates[thisId].transformArgs = [ 1, 0, 0, 1, 0, 0 ];
			} else {
				graphicsStates[thisId].strokeArgs = graphicsStates[parentId].strokeArgs;
				graphicsStates[thisId].paintArgs = graphicsStates[parentId].paintArgs;
				graphicsStates[thisId].transformArgs = graphicsStates[parentId].transformArgs;
			}
		} else {
			console.log("Graphics with id " + thisId + " already exist!");
		}

	}

	function iprtDraw(ctx, args) {
		ctx.save();
		if (path(ctx, args[1])) {
			ctx.clip();
		}
		path(ctx, args[0], true);
		ctx.stroke();
		ctx.restore();
	}

	function iprtFill(ctx, args) {
		ctx.save();
		if (path(ctx, args[1])) {
			ctx.clip();
		}
		path(ctx, args[0]);
		ctx.fill();
		ctx.restore();
	}

	function iprtDrawImage(ctx, args, imageConst, preloadedImages) {
		var image, hash, transform, bg, crop, clip;
		if (imageConst.image != null) {
			hash = imageConst.image.hash;
			image = preloadedImages[hash];
		}
		if (args[1].transform != null) {
			transform = args[1];
		}
		if (args[2].rectangle != null) {
			crop = args[2].rectangle;
		}
		if (args[3].color != null) {
			bg = args[3].color;
		}
		if (args[4] != null) {
			clip = args[4];
		}
		ctx.save();
		if (path(ctx, clip)) {
			ctx.clip();
		}
		if (transform != null) {
			iprtTransform(ctx, [ transform ]);
		}
		if (crop != null) {
			ctx.drawImage(image, crop.x, crop.y, crop.w, crop.h, 0, 0, crop.w, crop.h);
		} else {
			ctx.drawImage(image, 0, 0, image.width, image.height);
		}
		ctx.restore();
	}

	function iprtDrawWebImage(ctx, args, image, preloadedImages) {
		var transform, bg, crop, clip;

		if (args[0].transform != null) {
			transform = args[0];
		}
		if (args[1].rectangle != null) {
			crop = args[1].rectangle;
		}
		if (args[2].color != null) {
			bg = args[2].color;
		}
		if (args[3] != null) {
			clip = args[3];
		}

		var imageCanvas = document.createElement("canvas");
		imageCanvas.width = crop.w;
		imageCanvas.height = crop.h;
		var icCtx = imageCanvas.getContext("2d");
		icCtx.translate(crop.x, crop.y);

		var originalGraphicsStates = graphicsStates;
		var originalCurrentStateId = currentStateId;
		graphicsStates = {};
		currentStateId = null;
		if (image.instructions != null) {
			image.instructions.forEach(function(instruction) {
				interpretInstruction(icCtx, instruction);
			}, preloadedImages);
		}
		graphicsStates = originalGraphicsStates;
		currentStateId = currentStateId;

		ctx.save();
		if (path(ctx, clip)) {
			ctx.clip();
		}
		if (transform != null) {
			iprtTransform(ctx, [ transform ]);
		}
		ctx.drawImage(imageCanvas, 0, 0, imageCanvas.width, imageCanvas.height);
		ctx.restore();

	}

	function iprtDrawString(ctx, args) {
		var string, font, transform, clip;
		string = args[0].string;
		font = args[1].font;
		transform = args[2];
		clip = args[3];
		ctx.save();
		if (path(ctx, clip)) {
			ctx.clip();
		}
		iprtTransform(ctx, [ transform ]);
		var style;
		switch (font.style) {
		case StyleProto.NORMAL:
			style = '';
			break;
		case StyleProto.OBLIQUE:
			style = 'bold';
			break;
		case StyleProto.OBLIQUE:
			style = 'italic';
			break;
		}
		ctx.font = style + " " + font.size + "px " + font.family;
		ctx.fillText(string, 0, 0);

		ctx.restore();
	}

	function iprtCopyArea(ctx, args) {
		var p = args[0].points.points;
		var clip = args[1];
		ctx.save();

		if (path(ctx, clip)) {
			ctx.clip();
		}
		var tmpCanvas = document.createElement("canvas");
		tmpCanvas.width = p[2];
		tmpCanvas.height = p[3];
		var copiedArea = ctx.getImageData(p[0], p[1], p[2], p[3]);
		tmpCanvas.getContext("2d").putImageData(copiedArea, 0, 0);
		ctx.drawImage(tmpCanvas, p[4], p[5]);
		ctx.restore();

	}

	function iprtTransform(ctx, args) {
		var t = args[0].transform;
		var a, b, c, d, e, f;
		a = t.m00 != null ? t.m00 : 1;
		b = t.m10 != null ? t.m10 : 0;
		c = t.m01 != null ? t.m01 : 0;
		d = t.m11 != null ? t.m11 : 1;
		e = t.m02X2 != null ? t.m02X2 / 2 : 0;
		f = t.m12X2 != null ? t.m12X2 / 2 : 0;
		var m = [ a, b, c, d, e, f ];
		ctx.transform(a, b, c, d, e, f);
		return m;
	}

	function iprtSetStroke(ctx, args) {
		var stroke = args[0].stroke;
		ctx.lineWidth = stroke.widthX10 / 10;
		ctx.miterLimit = stroke.miterLimitX10 / 10;
		switch (stroke.cap) {
		case StrokeCapProto.CAP_BUTT:
			ctx.lineCap = "butt";
			break;
		case StrokeCapProto.CAP_ROUND:
			ctx.lineCap = "round";
			break;
		case StrokeCapProto.CAP_SQUARE:
			ctx.lineCap = "square";
			break;
		}
		switch (stroke.join) {
		case StrokeJoinProto.JOIN_MITER:
			ctx.lineJoin = "miter";
			break;
		case StrokeJoinProto.JOIN_ROUND:
			ctx.lineJoin = "round";
			break;
		case StrokeJoinProto.JOIN_BEVEL:
			ctx.lineJoin = "bevel";
			break;
		}
		if (stroke.dashX10 != null) {
			var dash = [];
			for ( var i = 0; i < stroke.dashX10.length; i++) {
				dash[i] = stroke.dashX10[i] / 10;
			}
			ctx.setLineDash(dash);
			ctx.lineDashOffset = stroke.dashOffset;
		}
	}

	function iprtSetPaint(ctx, args, preloadedImages) {
		var constant = args[0];
		if (constant.color != null) {
			var color = parseColor(constant.color.rgba);
			ctx.fillStyle = color;
			ctx.strokeStyle = color;
		}
		if (constant.image != null) {
			var anchor = args[1].rectangle;
			var preloadedImage = preloadedImages[constant.image.hash];
			var ptrn;
			if (anchor.x == 0 && anchor.y == 0 && anchor.w == preloadedImage.width && anchor == preloadedImage.height) {
				ptrn = ctx.createPattern(preloadedImage, 'repeat');
			} else {
				var ptrnCanvas = document.createElement('canvas');
				var ax = anchor.x < 0 ? ((anchor.x % anchor.w) + anchor.w) : (anchor.x % anchor.w);
				var ay = anchor.y < 0 ? ((anchor.y % anchor.h) + anchor.h) : (anchor.y % anchor.h);
				ptrnCanvas.width = anchor.w;
				ptrnCanvas.height = anchor.h;
				var ptrnContext = ptrnCanvas.getContext("2d");
				ptrnContext.fillRect(0, 0, anchor.w, anchor.h);
				ptrnContext.fillStyle = ptrnContext.createPattern(preloadedImage, 'repeat');
				ptrnContext.setTransform(anchor.w / preloadedImage.width, 0, 0, anchor.h / preloadedImage.height, ax, ay);
				ptrnContext.fillRect(-ax * preloadedImage.width / anchor.w, -ay * preloadedImage.height / anchor.h, preloadedImage.width,
						preloadedImage.height);
				ptrn = ctx.createPattern(ptrnCanvas, 'repeat');
			}
			ctx.fillStyle = ptrn;
			ctx.strokeStyle = ptrn;
		}
		if (constant.linearGrad != null) {
			var g = constant.linearGrad;
			// in case of cyclic gradient calculate repeat counts
			var repeatcount = 1;
			var xIncrement = g.xEnd - g.xStart;
			var yIncrement = g.yEnd - g.yStart;
			var increaseCount = 0, decreaseCount = 0;
			if (g.repeat != null && g.repeat != CyclicMethodProto.NO_CYCLE) {
				var c = config.canvas;
				if ((g.yStart - g.yEnd) != 0) {
					var a = -(g.xStart - g.xEnd) / (g.yStart - g.yEnd);
					var bCanvasMin = Math.min(0, -a * c.width, c.height, c.height - a * c.width);
					var bCanvasMax = Math.max(0, -a * c.width, c.height, c.height - a * c.width);
					var bStartPoint = (g.yStart - a * g.xStart);
					var bIncrement = (g.yEnd - a * g.xEnd) - bStartPoint;
				} else {
					var bCanvasMin = 0;
					var bCanvasMax = c.width;
					var bStartPoint = g.xStart;
					var bIncrement = xIncrement;
				}
				// repeat by increasing
				var maxCount = (bCanvasMax + (bStartPoint % bIncrement) - (bCanvasMax % bIncrement) - bStartPoint) / bIncrement;
				// repeat decreasing
				var minCount = -(bCanvasMin + (bStartPoint % bIncrement) - (bCanvasMin % bIncrement) - bStartPoint) / bIncrement + 1;
				var increaseCount = (maxCount > 0 ? maxCount : 0) + (minCount < 0 ? -minCount : 0);
				var decreaseCount = (minCount > 0 ? minCount : 0) + (maxCount < 0 ? -maxCount : 0);
				repeatcount = increaseCount + 1 + decreaseCount;
			}
			var gradient = ctx.createLinearGradient(g.xStart - xIncrement * decreaseCount, g.yStart - yIncrement * decreaseCount, g.xEnd + xIncrement
					* increaseCount, g.yEnd + yIncrement * increaseCount);
			for ( var rep = -decreaseCount; rep < repeatcount - decreaseCount; rep++) {

				if (rep % 2 == 0) {
					for ( var i = 0; i < g.colors.length; i++) {
						gradient.addColorStop(g.fractions[i] / repeatcount + (rep + decreaseCount) / repeatcount, parseColor(g.colors[i]));
					}
				} else {
					for ( var i = g.colors.length - 1; i >= 0; i--) {
						gradient.addColorStop(Math.abs(1 - (g.fractions[i])) / repeatcount + (rep + decreaseCount) / repeatcount,
								parseColor(g.colors[i]));
					}
				}
			}
			ctx.fillStyle = gradient;
			ctx.strokeStyle = gradient;
		}
		if (constant.radialGrad != null) {
			var rg = constant.radialGrad;
			// fix focus if outside radius
			var phi, maxFocusX, maxFocusY, fX, fY, directionX, directionY;
			phi = 1 / Math.tan((rg.xFocus - rg.xCenter) / (rg.yFocus - rg.yCenter));
			directionX = rg.xFocus - rg.xCenter < 0 ? -1 : 1;
			directionY = rg.yFocus - rg.yCenter < 0 ? -1 : 1;
			maxFocusX = rg.xCenter + (directionX * rg.radius * Math.cos(phi));
			maxFocusY = rg.yCenter + (directionY * rg.radius * Math.sin(phi));
			var useMaxFocus = Math.sqrt(Math.pow(maxFocusX - rg.xCenter, 2) + Math.pow(maxFocusY - rg.yCenter, 2)) < Math.sqrt(Math.pow(rg.xFocus
					- rg.xCenter, 2)
					+ Math.pow(rg.yFocus - rg.yCenter, 2));
			fX = useMaxFocus ? (directionX == 1 ? Math.floor(maxFocusX) : Math.ceil(maxFocusX)) : rg.xFocus;
			fY = useMaxFocus ? (directionY == 1 ? Math.floor(maxFocusY) : Math.ceil(maxFocusY)) : rg.yFocus;

			// in case of cyclic gradient calculate repeat counts
			var repeatcount = 1;
			if (rg.repeat != null && rg.repeat != CyclicMethodProto.NO_CYCLE) {
				var gradientsize, repeatcount;
				if (fX == rg.xCenter && fY == rg.yCenter) {
					gradientsize = rg.radius;
				} else {
					var distanceFromCenterToFocus = Math.sqrt(Math.pow(rg.xFocus - rg.xCenter, 2) + Math.pow(rg.yFocus - rg.yCenter, 2));
					gradientsize = Math.min(distanceFromCenterToFocus, rg.radius - distanceFromCenterToFocus);
				}
				repeatcount = Math.ceil(Math.sqrt(Math.pow(config.canvas.height, 2) + Math.pow(config.canvas.width, 2)) / gradientsize);
				repeatcount = Math.min(MAX_GRADIENT_CYCLE_REPEAT_COUNT, repeatcount);
			}

			// create gradient
			var xCentershift = (fX - rg.xCenter) * (repeatcount - 1);
			var yCentershift = (fY - rg.yCenter) * (repeatcount - 1);
			var grd = ctx.createRadialGradient(fX, fY, 0, rg.xCenter - xCentershift, rg.yCenter - yCentershift, rg.radius * repeatcount);
			for ( var rep = 0; rep < repeatcount; rep++) {
				if (rg.repeat == CyclicMethodProto.REFLECT) {
					if (rep % 2 == 0) {
						for ( var i = 0; i < rg.colors.length; i++) {
							grd.addColorStop(rg.fractions[i] / repeatcount + rep / repeatcount, parseColor(rg.colors[i]));
						}
					} else {
						for ( var i = rg.colors.length - 1; i >= 0; i--) {
							grd.addColorStop(Math.abs(1 - (rg.fractions[i])) / repeatcount + rep / repeatcount, parseColor(rg.colors[i]));
						}
					}
				} else {
					for ( var i = 0; i < rg.colors.length; i++) {
						grd.addColorStop(rg.fractions[i] / repeatcount + rep / repeatcount, parseColor(rg.colors[i]));
					}
				}
			}
			ctx.fillStyle = grd;
			ctx.strokeStyle = grd;
		}
	}

	function path(ctx, dConst, biased) {
		var success = false;
		ctx.beginPath();
		// rectangle
		if (dConst.rectangle != null) {
			var rect = dConst.rectangle;
			pathRectangle(ctx, rect, biased);
			success = true;
		}
		// roundRectangle
		if (dConst.roundRectangle != null) {
			var rr = dConst.roundRectangle;
			pathRoundRectangle(ctx, rr, biased);
			success = true;
		}

		// ellipse
		if (dConst.ellipse != null) {
			var e = dConst.ellipse;
			pathEllipse(ctx, e.x, e.y, e.w, e.h, biased);
			success = true;
		}

		// arc
		if (dConst.arc != null) {
			pathArc(ctx, dConst.arc, biased);
			success = true;
		}

		// generig path
		if (dConst.path != null) {
			var path = dConst.path;
			var bias = biased ? 0.5 : 0;
			var off = 0;
			path.type.forEach(function(type, index) {
				switch (type) {
				case SegmentTypeProto.MOVE:
					ctx.moveTo(path.points[off + 0] + bias, path.points[off + 1] + bias);
					off += 2;
					break;
				case SegmentTypeProto.LINE:
					ctx.lineTo(path.points[off + 0] + bias, path.points[off + 1] + bias);
					off += 2;
					break;
				case SegmentTypeProto.QUAD:
					ctx.quadraticCurveTo(path.points[off + 0] + bias, path.points[off + 1] + bias, path.points[off + 2], path.points[off + 3]);
					off += 4;
					break;
				case SegmentTypeProto.CUBIC:
					ctx.bezierCurveTo(path.points[off + 0] + bias, path.points[off + 1] + bias, path.points[off + 2], path.points[off + 3],
							path.points[off + 4], path.points[off + 5]);
					off += 6;
					break;
				case SegmentTypeProto.CLOSE:
					ctx.closePath();
					break;
				default:
					console.log("segment.type:" + segment.type + " not recognized");
				}
			});
			success = true;
		}
		return success;
	}

	function pathRectangle(ctx, rect, biased) {
		var bias = biased ? 0.5 : 0;
		var x = rect.x + bias;
		var y = rect.y + bias;
		ctx.moveTo(x, y);
		ctx.lineTo(x + rect.w, y);
		ctx.lineTo(x + rect.w, y + rect.h);
		ctx.lineTo(x, y + rect.h);
		ctx.lineTo(x, y);
	}

	function pathEllipse(ctx, x, y, w, h, biased) {
		var bias = biased ? 0.5 : 0;
		x += bias;
		y += bias;
		var kappa = .5522848;
		var ox = (w / 2) * kappa; // control point offset horizontal
		var oy = (h / 2) * kappa; // control point offset vertical
		var xe = x + w; // x-end
		var ye = y + h; // y-end
		var xm = x + w / 2; // x-middle
		var ym = y + h / 2; // y-middle

		ctx.moveTo(x, ym);
		ctx.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		ctx.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
		ctx.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		ctx.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
		ctx.closePath();
	}

	function pathRoundRectangle(ctx, rr, biased) {
		var bias = biased ? 0.5 : 0;
		rr.x += bias;
		rr.y += bias;
		var acv = 0.22385762508460333;
		var pts = getRRCoords([ 0, 0, 0, 0.5 ], rr);
		ctx.moveTo(pts[0], pts[1]);

		pts = getRRCoords([ 0, 0, 1, -0.5 ], rr);
		ctx.lineTo(pts[0], pts[1]);
		pts = getRRCoords([ 0, 0, 1, -acv, 0, acv, 1, 0, 0, 0.5, 1, 0 ], rr);
		ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

		pts = getRRCoords([ 1, -0.5, 1, 0 ], rr);
		ctx.lineTo(pts[0], pts[1]);
		pts = getRRCoords([ 1, -acv, 1, 0, 1, 0, 1, -acv, 1, 0, 1, -0.5 ], rr);
		ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

		pts = getRRCoords([ 1, 0, 0, 0.5 ], rr);
		ctx.lineTo(pts[0], pts[1]);
		pts = getRRCoords([ 1, 0, 0, acv, 1, -acv, 0, 0, 1, -0.5, 0, 0 ], rr);
		ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

		pts = getRRCoords([ 0, 0.5, 0, 0 ], rr);
		ctx.lineTo(pts[0], pts[1]);
		pts = getRRCoords([ 0, acv, 0, 0, 0, 0, 0, acv, 0, 0, 0, 0.5 ], rr);
		ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

		ctx.closePath();
	}

	function pathArc(ctx, arc, biased) {
		var bias = biased ? 0.5 : 0;
		arc.x += bias;
		arc.y += bias;
		var w = arc.w / 2, h = arc.h / 2, x = arc.x + w, y = arc.y + h;
		var angStRad = -(arc.start * Math.PI / 180);
		var ext = -arc.extent;
		var arcSegs = 4;
		var increment = ext < 0 ? Math.PI / 2 : -Math.PI / 2;
		var cv = ext < 0 ? 0.5522847498307933 : -0.5522847498307933;
		if (ext > -360 && ext < 360) {
			arcSegs = Math.ceil(Math.abs(ext) / 90);
			increment = (ext / arcSegs) * Math.PI / 180;
			cv = 4.0 / 3.0 * Math.sin(increment / 2) / (1.0 + Math.cos(increment / 2));
			arcSegs = cv == 0 ? 0 : arcSegs;
		}
		ctx.moveTo(x + Math.cos(angStRad) * w, y + Math.sin(angStRad) * h);
		for ( var j = 1; j <= arcSegs; j++) {
			var angle = angStRad + increment * (j - 1);
			var relx = Math.cos(angle);
			var rely = Math.sin(angle);
			var pts = [];
			pts[0] = (x + (relx - cv * rely) * w);
			pts[1] = (y + (rely + cv * relx) * h);
			angle += increment;
			relx = Math.cos(angle);
			rely = Math.sin(angle);
			pts[2] = (x + (relx + cv * rely) * w);
			pts[3] = (y + (rely - cv * relx) * h);
			pts[4] = (x + relx * w);
			pts[5] = (y + rely * h);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
		}
		switch (arc.type) {
		case ArcTypeProto.OPEN:
			break;
		case ArcTypeProto.CHORD:
			ctx.closePath();
			break;
		case ArcTypeProto.PIE:
			ctx.lineTo(x, y);
			ctx.closePath();
			break;
		}
	}

	function getImageData(image) {
		return 'data:image/png;base64,' + image.data.toBase64();
	}

	function getRRCoords(pts, rr) {
		var coords = [];
		var nc = 0;
		for ( var i = 0; i < pts.length; i += 4) {
			coords[nc++] = rr.x + pts[i + 0] * rr.w + pts[i + 1] * Math.abs(rr.arcW);
			coords[nc++] = rr.y + pts[i + 2] * rr.h + pts[i + 3] * Math.abs(rr.arcH);
		}
		return coords;
	}

	function parseColor(rgba) {
		var mask = 0x000000FF;
		return 'rgba(' + ((rgba >>> 24) & mask) + ',' + ((rgba >>> 16) & mask) + ',' + ((rgba) >>> 8 & mask) + ',' + (rgba & mask) + ')';
	}

	function prepareImages(imageConstants) {
		return new Promise(function(resolve, reject) {
			try {
				var preloadedImages = {};
				if (imageConstants.length > 0) {
					var loadPromisesArray = imageConstants.map(function(constant) {
						return new Promise(function(resolve, reject) {
							preloadedImages[constant.hash] = new Image();
							preloadedImages[constant.hash].onload = function() {
								resolve(preloadedImages);
							};
							preloadedImages[constant.hash].src = getImageData(constant);
						});
					});
					Promise.all(loadPromisesArray).then(resolve(preloadedImages));
				} else {
					resolve(preloadedImages);
				}
			} catch (e) {
				config.onErrorMessage(error);
				reject(e);
			}
		});
	}

	function cleanupImages(preloadedImages) {
		for ( var hash in preloadedImages) {
			if (preloadedImages.hasOwnProperty(hash)) {
				preloadedImages[hash].src = '';
				preloadedImages[hash].onload = null;
				delete preloadedImages[hash];
			}
		}
	}

	function cleanupCanvas() {
		ctx.setTransform(1, 0, 0, 1, 0, 0);
	}

	function resolveArgs(args, cache) {
		var result = [];
		for ( var i = 0; i < args.length; i++) {
			result[i] = constantPoolCache[args[i]];
		}
		return result;
	}

	function concatTransform(m, t) {
		var r = [];
		if (m == null) {
			return t;
		} else {
			r[0] = m[0] * t[0] + m[2] * t[1];
			r[1] = m[1] * t[0] + m[3] * t[1];
			r[2] = m[0] * t[2] + m[2] * t[3];
			r[3] = m[1] * t[2] + m[3] * t[3];
			r[4] = m[0] * t[4] + m[2] * t[5] + m[4];
			r[5] = m[1] * t[4] + m[3] * t[5] + m[5];
		}
		return r;
	}

	return {
		draw64 : function(data) {
			return draw64(data);
		},
		drawBin : function(data) {
			return drawBin(data);
		}
	};
};