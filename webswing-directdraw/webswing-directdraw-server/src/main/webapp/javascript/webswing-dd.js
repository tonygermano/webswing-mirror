(function(root, factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define([ 'ProtoBuf','text!directdraw.proto' ], factory);
	} else {
		root.WebswingDirectDraw = factory(dcodeIO.ProtoBuf);
	}
}(this, function(ProtoBuf, webswingProto) {
	return function(c) {
		"use strict";
		c = c || {};
		var config = {
			onErrorMessage : c.onErrorMessage || function(message) {
				console.log(message);
			}
		};
		var proto = webswingProto!=null? ProtoBuf.loadProto(webswingProto,"directdraw.proto"):ProtoBuf.loadProtoFile("/directdraw.proto");
		var WebImageProto = proto.build("org.webswing.directdraw.proto.WebImageProto");
		var InstructionProto = proto.build("org.webswing.directdraw.proto.DrawInstructionProto.InstructionProto");
		var SegmentTypeProto = proto.build("org.webswing.directdraw.proto.PathProto.SegmentTypeProto");
		var ArcTypeProto = proto.build("org.webswing.directdraw.proto.ArcProto.ArcTypeProto");
		var CyclicMethodProto = proto.build("org.webswing.directdraw.proto.CyclicMethodProto");
		var StrokeJoinProto = proto.build("org.webswing.directdraw.proto.StrokeProto.StrokeJoinProto");
		var StrokeCapProto = proto.build("org.webswing.directdraw.proto.StrokeProto.StrokeCapProto");
		var StyleProto = proto.build("org.webswing.directdraw.proto.FontProto.StyleProto");
		var CompositeTypeProto = proto.build("org.webswing.directdraw.proto.CompositeProto.CompositeTypeProto");
		var MAX_GRADIENT_CYCLE_REPEAT_COUNT = 20;
		var constantPoolCache = c.constantPoolCache || {};
		var imagePoolCache = c.imagePoolCache || {};

		function draw64(data, targetCanvas) {
			var image = WebImageProto.decode64(data);
			return drawChunks(image, targetCanvas).then(function(result) {
				return drawWebImage(image, result);
			}, function(error) {
				throw error;
			});
		}

		function drawBin(data, targetCanvas) {
			var offset = data.offset;
			var image = WebImageProto.decode(data);
			data.offset = offset;
			return drawChunks(image, targetCanvas).then(function(result) {
				return drawWebImage(image, result);
			}, function(error) {
				throw error;
			});
		}

		function drawProto(data, targetCanvas) {
			var image = data;
			return drawChunks(image, targetCanvas).then(function(result) {
				return drawWebImage(image, result);
			}, function(error) {
				throw error;
			});
		}

		function drawChunks(image, targetCanvas) {
			if (image.chunks != null) {
				return image.chunks.reduce(function(seq, current) {
					return seq.then(function(result) {
						return drawWebImage(current, result);
					}, function(error) {
						throw error;
					});
				}, Promise.resolve(targetCanvas));
			} else {
				return Promise.resolve(targetCanvas);
			}
		}

		function drawWebImage(image, targetCanvas) {
			return new Promise(function(resolve, reject) {
				try {
					var newCanvas;
					if (targetCanvas != null) {
						newCanvas = targetCanvas;
					} else {
						newCanvas = document.createElement("canvas");
					}
					if (newCanvas.width != image.width || newCanvas.height != image.height) {
						newCanvas.width = image.width;
						newCanvas.height = image.height;
					}

					var imageContext = {
						canvas : newCanvas,
						graphicsStates : {},
						currentStateId : null
					};

					var imagesToPrepare;
					imagesToPrepare = [];
					populateConstantsPool(image, imagesToPrepare);

					prepareImages(imagesToPrepare).then(function(preloadedImageConstants) {
						var ctx = imageContext.canvas.getContext("2d");
						if (image.instructions != null) {
							ctx.save();
							image.instructions.reduce(function(seq, instruction) {
								return seq.then(function(resolved) {
									return interpretInstruction(ctx, instruction, imageContext);
								});
							}, Promise.resolve()).then(function() {
								ctx.restore();
								resolve(imageContext.canvas);
							}, function(error) {
								ctx.restore();
								reject(error);
								config.onErrorMessage(error);
							});
						}
					}, function(error) {
						config.onErrorMessage(error);
					});
				} catch (e) {
					config.onErrorMessage(e);
					reject(e);
				}
			});
		}

		function populateConstantsPool(image, imagesToPrepare) {
			image.constants.forEach(function(constant, index) {
				constantPoolCache[constant.id] = constant;
				if (constant.image != null) {
					imagesToPrepare.push(constant.image);
				}
			});
			if (image.images != null) {
				image.images.forEach(function(imgConst) {
					imagePoolCache[imgConst.id] = imgConst;
					imagesToPrepare.push(imgConst.image);
				});
			}
		}

		function interpretInstruction(ctx, instruction, imageContext) {
			var instCode = instruction.inst;
			var args = resolveArgs(instruction.args, constantPoolCache);
			switch (instCode) {
			case InstructionProto.GRAPHICS_CREATE:
				iprtGraphicsCreate(ctx, instruction.args[0], args, imageContext);
				break;
			case InstructionProto.GRAPHICS_SWITCH:
				iprtGraphicsSwitch(ctx, instruction.args[0], imageContext);
				break;
			case InstructionProto.GRAPHICS_DISPOSE:
				delete imageContext.graphicsStates[instruction.args[0]];
				break;
			case InstructionProto.DRAW:
				iprtDraw(ctx, args);
				break;
			case InstructionProto.FILL:
				iprtFill(ctx, args);
				break;
			case InstructionProto.DRAW_IMAGE:
				iprtDrawImage(ctx, args);
				break;
			case InstructionProto.DRAW_WEBIMAGE:
				return iprtDrawWebImage(ctx, args, instruction.webImage, imageContext);
				break;
			case InstructionProto.DRAW_STRING:
				iprtDrawString(ctx, args);
				break;
			case InstructionProto.COPY_AREA:
				iprtCopyArea(ctx, args, imageContext);
				break;
			case InstructionProto.SET_STROKE:
				iprtSetStroke(ctx, args);
				imageContext.graphicsStates[imageContext.currentStateId].strokeArgs = args;
				break;
			case InstructionProto.SET_PAINT:
				iprtSetPaint(ctx, args, imageContext);
				imageContext.graphicsStates[imageContext.currentStateId].paintArgs = args;
				break;
			case InstructionProto.SET_COMPOSITE:
				iprtSetComposite(ctx, args);
				imageContext.graphicsStates[imageContext.currentStateId].composite = args;
				break;
			case InstructionProto.TRANSFORM:
				var tx = iprtTransform(ctx, args);
				imageContext.graphicsStates[imageContext.currentStateId].transformArgs = concatTransform(
						imageContext.graphicsStates[imageContext.currentStateId].transformArgs, tx);
				break;
			default:
				console.log("instCode:" + instCode + " not recognized");
			}
			return Promise.resolve();
		}

		function iprtGraphicsSwitch(ctx, id, imageContext) {
			var graphicsStates = imageContext.graphicsStates;
			if (graphicsStates[id] != null) {
				if (graphicsStates[id].strokeArgs != null) {
					iprtSetStroke(ctx, graphicsStates[id].strokeArgs);
				}
				if (graphicsStates[id].paintArgs != null) {
					iprtSetPaint(ctx, graphicsStates[id].paintArgs);
				}
				if (graphicsStates[id].composite != null) {
					iprtSetComposite(ctx, graphicsStates[id].composite);
				}
				if (graphicsStates[id].transformArgs != null) {
					var m = graphicsStates[id].transformArgs;
					ctx.setTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
				}
			} else {
				console.log("Graphics with id " + id + " not initialized!");
			}
			imageContext.currentStateId = id;
		}

		function iprtGraphicsCreate(ctx, thisId, args, imageContext) {
			var graphicsStates = imageContext.graphicsStates;
			if (graphicsStates[thisId] == null) {
				graphicsStates[thisId] = {};
				imageContext.currentStateId = thisId;
				args.shift();
				var tx = iprtTransform(ctx, args, true);
				imageContext.graphicsStates[thisId].transformArgs = tx;
				args.shift();
				iprtSetStroke(ctx, args);
				imageContext.graphicsStates[thisId].strokeArgs = args.slice(0, 1);
				args.shift();
				iprtSetComposite(ctx, args);
				imageContext.graphicsStates[thisId].composite = args.slice(0, 1);
				args.shift();
				iprtSetPaint(ctx, args, imageContext);
				imageContext.graphicsStates[thisId].paintArgs = args;
			} else {
				console.log("Graphics with id " + thisId + " already exist!");
			}

		}

		function iprtDraw(ctx, args) {
			ctx.save();
			if (path(ctx, args[1])) {
				ctx.clip(fillRule(args[1]));
			}
			path(ctx, args[0], true);
			ctx.stroke();
			ctx.restore();
		}

		function iprtFill(ctx, args) {
			ctx.save();
			if (path(ctx, args[1])) {
				ctx.clip(fillRule(args[1]));
			}
			path(ctx, args[0]);
			ctx.fill(fillRule(args[0]));
			ctx.restore();
		}

		function iprtDrawImage(ctx, args) {
			var clip = null, image = null, p = null;
			if (args[1] != null) {
				p = args[1].points.points;
				image = imagePoolCache[p[0]].image.data;
			}
			if (args[0] != null) {
				clip = args[0];
			}

			ctx.save();
			ctx.setTransform(1, 0, 0, 1, 0, 0);
			if (path(ctx, clip)) {
				ctx.clip(fillRule(clip));
			}
			ctx.translate(p[1], p[2]);
			ctx.drawImage(image, 0, 0, image.width, image.height);
			ctx.restore();
		}

		function iprtDrawWebImage(ctx, args, imagedata, imageContext) {
			var transform = null, crop = null, clip = null;

			if (args[0].transform != null) {
				transform = args[0];
			}
			if (args[1].rectangle != null) {
				crop = args[1].rectangle;
			}
			if (args[3] != null) {
				clip = args[3];
			}
			return drawBin(imagedata).then(function(imageCanvas) {
				ctx.save();
				if (path(ctx, clip)) {
					ctx.clip(fillRule(clip));
				}
				if (transform != null) {
					iprtTransform(ctx, [ transform ]);
				}
				ctx.drawImage(imageCanvas, crop.x, crop.y, crop.w, crop.h, 0, 0, crop.w, crop.h);
				ctx.restore();
			});
		}

		function iprtDrawString(ctx, args) {
			var string, font, transform, clip;
			string = args[0].string;
			font = args[1].font;
			transform = args[2];
			clip = args[3];
			ctx.save();
			if (path(ctx, clip)) {
				ctx.clip(fillRule(clip));
			}
			iprtTransform(ctx, [ transform ]);
			var style = '';
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
			case StyleProto.BOLDANDITALIC:
				style = 'bold italic';
				break;
			}
			ctx.font = style + " " + font.size + "px " + font.family;
			ctx.fillText(string, 0, 0);

			ctx.restore();
		}

		function iprtCopyArea(ctx, args, imageContext) {
			var p = args[0].points.points;
			var clip = args[1];
			ctx.save();

			if (path(ctx, clip)) {
				ctx.clip(fillRule(clip));
			}
			ctx.beginPath();
			ctx.setTransform(1, 0, 0, 1, 0, 0);
			ctx.rect(p[0], p[1], p[2], p[3]);
			ctx.clip();
			ctx.translate(p[4], p[5]);
			ctx.drawImage(imageContext.canvas, 0, 0);
			ctx.restore();
		}

		function iprtTransform(ctx, args, reset) {
			var t = args[0].transform;
			if (reset) {
				ctx.setTransform(t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
			} else {
				ctx.transform(t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
			}
			return [ t.m00, t.m10, t.m01, t.m11, t.m02, t.m12 ];
		}

		function iprtSetStroke(ctx, args) {
			var stroke = args[0].stroke;
			ctx.lineWidth = stroke.width;
			ctx.miterLimit = stroke.miterLimit;
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
			if (stroke.dash != null) {
				ctx.setLineDash(stroke.dash);
				ctx.lineDashOffset = stroke.dashOffset;
			}
		}

		function iprtSetPaint(ctx, args, imageContext) {
			var constant = args[0];
			if (constant.color != null) {
				var color = parseColor(constant.color.rgba);
				ctx.fillStyle = color;
				ctx.strokeStyle = color;
			}
			if (constant.image != null) {
				var anchor = args[1].rectangle;
				var preloadedImage = constant.image.data;
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
				var bCanvasMin, bCanvasMax, bStartPoint, bIncrement;
				if (g.repeat != null && g.repeat != CyclicMethodProto.NO_CYCLE) {
					var c = imageContext.canvas;
					if ((g.yStart - g.yEnd) != 0) {
						var a = -(g.xStart - g.xEnd) / (g.yStart - g.yEnd);
						bCanvasMin = Math.min(0, -a * c.width, c.height, c.height - a * c.width);
						bCanvasMax = Math.max(0, -a * c.width, c.height, c.height - a * c.width);
						bStartPoint = (g.yStart - a * g.xStart);
						bIncrement = (g.yEnd - a * g.xEnd) - bStartPoint;
					} else {
						bCanvasMin = 0;
						bCanvasMax = c.width;
						bStartPoint = g.xStart;
						bIncrement = xIncrement;
					}
					// repeat by increasing
					var maxCount = (bCanvasMax + (bStartPoint % bIncrement) - (bCanvasMax % bIncrement) - bStartPoint) / bIncrement;
					// repeat decreasing
					var minCount = -(bCanvasMin + (bStartPoint % bIncrement) - (bCanvasMin % bIncrement) - bStartPoint) / bIncrement + 1;
					increaseCount = (maxCount > 0 ? maxCount : 0) + (minCount < 0 ? -minCount : 0);
					decreaseCount = (minCount > 0 ? minCount : 0) + (maxCount < 0 ? -maxCount : 0);
					repeatcount = increaseCount + 1 + decreaseCount;
				}
				var gradient = ctx.createLinearGradient(g.xStart - xIncrement * decreaseCount, g.yStart - yIncrement * decreaseCount, g.xEnd
						+ xIncrement * increaseCount, g.yEnd + yIncrement * increaseCount);
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
					var gradientsize;
					if (fX == rg.xCenter && fY == rg.yCenter) {
						gradientsize = rg.radius;
					} else {
						var distanceFromCenterToFocus = Math.sqrt(Math.pow(rg.xFocus - rg.xCenter, 2) + Math.pow(rg.yFocus - rg.yCenter, 2));
						gradientsize = Math.min(distanceFromCenterToFocus, rg.radius - distanceFromCenterToFocus);
					}
					repeatcount = Math.ceil(Math.sqrt(Math.pow(imageContext.canvas.height, 2) + Math.pow(imageContext.canvas.width, 2))
							/ gradientsize);
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

		function iprtSetComposite(ctx, args) {
			var composite = args[0].composite;
			if (composite != null) {
			    ctx.globalAlpha = composite.alpha;
				switch (composite.type) {
				case CompositeTypeProto.CLEAR:
					ctx.globalCompositeOperation = "destination-out";
					break;
				case CompositeTypeProto.SRC:
					ctx.globalCompositeOperation = "source-over";
					break;
				case CompositeTypeProto.DST:
					ctx.globalCompositeOperation = "destination-in";
					break;
				case CompositeTypeProto.SRC_OVER:
					ctx.globalCompositeOperation = "source-over";
					break;
				case CompositeTypeProto.DST_OVER:
					ctx.globalCompositeOperation = "destination-over";
					break;
				case CompositeTypeProto.SRC_IN:
					ctx.globalCompositeOperation = "source-in";
					break;
				case CompositeTypeProto.DST_IN:
					ctx.globalCompositeOperation = "destination-in";
					break;
				case CompositeTypeProto.SRC_OUT:
					ctx.globalCompositeOperation = "source-out";
					break;
				case CompositeTypeProto.DST_OUT:
					ctx.globalCompositeOperation = "destination-out";
					break;
				case CompositeTypeProto.SRC_ATOP:
					ctx.globalCompositeOperation = "source-atop";
					break;
				case CompositeTypeProto.XOR:
					ctx.globalCompositeOperation = "xor";
					break;
				}
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
			var binary = '';
			var bytes = new Uint8Array(image.data.buffer, image.data.offset, image.data.limit - image.data.offset);

			for ( var i = 0, l = bytes.byteLength; i < l; i++) {
				binary += String.fromCharCode(bytes[i]);
			}

			return "data:image/png;base64," + window.btoa(binary);
			// return 'data:image/png;base64,' + image.data.toBase64();
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
			return 'rgba(' + ((rgba >>> 24) & mask) + ',' + ((rgba >>> 16) & mask) + ',' + ((rgba) >>> 8 & mask) + ',' + ((rgba & mask) / 255) + ')';
		}

		function prepareImages(imageConstants) {
			return new Promise(function(resolve, reject) {
				try {
					var preloadedImageConstants;
					preloadedImageConstants = [];
					if (imageConstants.length > 0) {
						var loadPromisesArray = imageConstants.map(function(constant) {
							return new Promise(function(resolve, reject) {
								var img = new Image();
								img.onload = function() {
									constant.data = img;
									preloadedImageConstants.push(img);
									resolve();
								};
								img.src = getImageData(constant);
							});
						});
						Promise.all(loadPromisesArray).then(function(preloadedImageConstants) {
							resolve(preloadedImageConstants);
						});
					} else {
						resolve(preloadedImageConstants);
					}
				} catch (e) {
					config.onErrorMessage(error);
					reject(e);
				}
			});
		}

		function fillRule(constant) {
			if (constant.path != null) {
				return constant.path.windingOdd ? 'evenodd' : 'nonzero';
			}
			return 'nonzero';
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
			draw64 : function(data, targetCanvas) {
				return draw64(data, targetCanvas);
			},
			drawBin : function(data, targetCanvas) {
				return drawBin(data, targetCanvas);
			},
			drawProto : function(data, targetCanvas) {
				return drawProto(data, targetCanvas);
			},
			getConstantPoolCache : function() {
				return constantPoolCache;
			},
			getImagePoolCache : function() {
				return imagePoolCache;
			}
		};
	};
}));
