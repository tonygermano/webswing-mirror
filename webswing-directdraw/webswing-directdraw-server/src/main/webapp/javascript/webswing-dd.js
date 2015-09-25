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
				} else if (constant.texture != null) {
					imagesToPrepare.push(constant.texture.image);
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
			var args = resolveArgs(instruction.args, constantPoolCache);
			var graphicsState = imageContext.graphicsStates[imageContext.currentStateId];
			switch (instruction.inst) {
                case InstructionProto.GRAPHICS_CREATE:
                    iprtGraphicsCreate(ctx, instruction.args[0], args, imageContext);
                    break;
                case InstructionProto.GRAPHICS_SWITCH:
                    iprtGraphicsSwitch(ctx, instruction.args[0], imageContext);
                    break;
                case InstructionProto.GRAPHICS_DISPOSE:
                    iprtGraphicsDispose(instruction.args[0], imageContext);
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
                    return iprtDrawWebImage(ctx, args, instruction.webImage);
                    break;
                case InstructionProto.DRAW_STRING:
                    iprtDrawString(ctx, args, graphicsState.fontTransform);
                    break;
                case InstructionProto.COPY_AREA:
                    iprtCopyArea(ctx, args);
                    break;
                case InstructionProto.SET_STROKE:
                    graphicsState.strokeArgs = args;
                    iprtSetStroke(ctx, args);
                    break;
                case InstructionProto.SET_PAINT:
                    graphicsState.paintArgs = args;
                    iprtSetPaint(ctx, args);
                    break;
                case InstructionProto.SET_COMPOSITE:
                    graphicsState.compositeArgs = args;
                    iprtSetComposite(ctx, args);
                    break;
                case InstructionProto.SET_FONT:
                    graphicsState.fontArgs = args;
                    graphicsState.fontTransform = iprtSetFont(ctx, args);
                    break;
                case InstructionProto.TRANSFORM:
                    graphicsState.transform = concatTransform(graphicsState.transform, iprtTransform(ctx, args));
                    break;
                default:
                    console.log("instruction code: " + instruction.inst + " not recognized");
			}
			return Promise.resolve();
		}
		
		function iprtGraphicsDispose(id, imageContext) {
		    delete imageContext.graphicsStates[id];
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
				if (graphicsStates[id].compositeArgs != null) {
					iprtSetComposite(ctx, graphicsStates[id].compositeArgs);
				}
				if (graphicsStates[id].fontArgs != null) {
				    iprtSetFont(ctx, graphicsStates[id].fontArgs);
				}
				if (graphicsStates[id].transform != null) {
					var t = graphicsStates[id].transform;
					ctx.setTransform(t[0], t[1], t[2], t[3], t[4], t[5]);
				}
			} else {
				console.log("Graphics with id " + id + " not initialized!");
			}
			imageContext.currentStateId = id;
		}

		function iprtGraphicsCreate(ctx, id, args, imageContext) {
			var graphicsStates = imageContext.graphicsStates;
			if (graphicsStates[id] == null) {
				graphicsStates[id] = {};
				imageContext.currentStateId = id;
				args.shift();
				graphicsStates[id].transform = iprtTransform(ctx, args, true);
				args.shift();
				iprtSetStroke(ctx, args);
				graphicsStates[id].strokeArgs = args.slice(0, 1);
				args.shift();
				iprtSetComposite(ctx, args);
				graphicsStates[id].compositeArgs = args.slice(0, 1);
				args.shift();
				iprtSetPaint(ctx, args);
				graphicsStates[id].paintArgs = args;
				args.shift();
                graphicsStates[id].fontArgs = args;
                graphicsStates[id].fontTransform = iprtSetFont(ctx, args);
			} else {
				console.log("Graphics with id " + id + " already exist!");
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

		function iprtDrawWebImage(ctx, args, imagedata) {
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

		function iprtDrawString(ctx, args, fontTransform) {
			var string = args[0].string;
			var points = args[1].points.points;
			var clip = args[2];
			ctx.save();
			if (path(ctx, clip)) {
				ctx.clip(fillRule(clip));
			}
			if (fontTransform != null) {
			    var t = fontTransform;
			    ctx.transform(t.m00, t.m10, t.m01, t.m11, t.m02 + points[0], t.m12 + points[1]);
			    ctx.fillText(string, 0, 0);
			} else {
				ctx.fillText(string, points[0], points[1]);
			}
			ctx.restore();
		}
		
		function iprtSetFont(ctx, args) {
		    var font = args[0].font;
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
            return font.transform;
		}

		function iprtCopyArea(ctx, args) {
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
			ctx.drawImage(ctx.canvas, 0, 0);
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

		function iprtSetPaint(ctx, args) {
			var constant = args[0];
			if (constant.color != null) {
				var color = parseColor(constant.color.rgba);
				ctx.fillStyle = color;
				ctx.strokeStyle = color;
			} else if (constant.texture != null) {
				var anchor = constant.texture.anchor;
				var preloadedImage = constant.texture.image.data;
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
			} else if (constant.linearGrad != null) {
				var gradient = iprtLinearGradient(ctx, constant.linearGrad);
				ctx.fillStyle = gradient;
				ctx.strokeStyle = gradient;
			} else if (constant.radialGrad != null) {
				var gradient = iprtRadialGradient(ctx, constant.radialGrad);
				ctx.fillStyle = gradient;
				ctx.strokeStyle = gradient;
			}
		}
		
		function iprtLinearGradient(ctx, g) {
		    var x0 = g.xStart;
		    var y0 = g.yStart;
            var dx = g.xEnd - x0;
            var dy = g.yEnd - y0;
            // in case of cyclic gradient calculate repeat counts
            var repeatCount = 1, increaseCount = repeatCount, decreaseCount = 0;
            if (g.repeat != CyclicMethodProto.NO_CYCLE && (dx != 0 || dy != 0)) {
                // calculate how many times gradient will completely repeat in both directions until it touches canvas corners
                var c = ctx.canvas;
                var times = [calculateTimes(x0, y0, dx, dy, 0, 0),
                             calculateTimes(x0, y0, dx, dy, c.width, 0),
                             calculateTimes(x0, y0, dx, dy, c.width, c.height),
                             calculateTimes(x0, y0, dx, dy, 0, c.height)];
                // increase count is maximum of all positive times rounded up
                increaseCount = Math.ceil(Math.max.apply(Math, times));
                // decrease count is maximum of all negative times rounded up (with inverted sign)
                decreaseCount = Math.ceil(-Math.min.apply(Math, times));
                repeatCount = increaseCount + decreaseCount;
            }
            var gradient = ctx.createLinearGradient(x0 - dx * decreaseCount, y0 - dy * decreaseCount, x0 + dx * increaseCount, y0 + dy * increaseCount);
            for (var rep = -decreaseCount, offset = 0; rep < increaseCount; rep++, offset++) {
                if (g.repeat != CyclicMethodProto.REFLECT || rep % 2 == 0) {
                    for (var i = 0; i < g.colors.length; i++) {
                        gradient.addColorStop((offset + g.fractions[i]) / repeatCount, parseColor(g.colors[i]));
                    }
                } else {
                    // reflect colors
                    for (var i = g.colors.length - 1; i >= 0; i--) {
                        gradient.addColorStop((offset + (1 - g.fractions[i])) / repeatCount, parseColor(g.colors[i]));
                    }
                }
            }
            return gradient;
		}
		
		// calculates how many times vector (dx, dy) will repeat from (x0, y0) until it touches a straight line
		// which goes through (x1, y1) and perpendicular to the vector
		function calculateTimes(x0, y0, dx, dy, x1, y1) {
		    return ((x1 - x0) * dx + (y1 - y0) * dy) / (dx * dx + dy * dy);
		}
		
		function iprtRadialGradient(ctx, g) {
		    fixFocusPoint(g);
		    var fX = g.xFocus;
		    var fY = g.yFocus;
		    var dx = g.xCenter - fX;
		    var dy = g.yCenter - fY;
		    var r = g.radius;
            // in case of cyclic gradient calculate repeat counts
            var repeatCount = 1;
            if (g.repeat != CyclicMethodProto.NO_CYCLE) {
                if (dx == 0 && dy == 0) {
                    // calculate how many times gradient will completely repeat in both directions until it touches canvas corners
                    var c = ctx.canvas;
                    var times = [getDistance(fX, fY, 0, 0) / r,
                                 getDistance(fX, fY, c.width, 0) / r,
                                 getDistance(fX, fY, c.width, c.height) / r,
                                 getDistance(fX, fY, 0, c.height) / r];
                    repeatCount = Math.ceil(Math.max.apply(Math, times));
                } else {
                    var distance = Math.sqrt(dx * dx + dy * dy);
                    // calculate vector which goes from focus point through center to the circle bound
                    var vdX = dx + r * dx / distance;
                    var vdY = dy + r * dy / distance;
                    // and in opposite direction
                    var ovdX = dx - r * dx / distance;
                    var ovdY = dy - r * dy / distance;
                    // calculate how many times gradient will completely repeat in both directions until it touches canvas corners
                    var c = ctx.canvas;
                    var times = [calculateTimes(fX, fY, vdX, vdY, 0, 0),
                                 calculateTimes(fX, fY, vdX, vdY, c.width, 0),
                                 calculateTimes(fX, fY, vdX, vdY, c.width, c.height),
                                 calculateTimes(fX, fY, vdX, vdY, 0, c.height),
                                 calculateTimes(fX, fY, ovdX, ovdY, 0, 0),
                                 calculateTimes(fX, fY, ovdX, ovdY, c.width, 0),
                                 calculateTimes(fX, fY, ovdX, ovdY, c.width, c.height),
                                 calculateTimes(fX, fY, ovdX, ovdY, 0, c.height)];
                    repeatCount = Math.ceil(Math.max.apply(Math, times));
                }
            }
            // in case of repeat focus stays in the same place, radius and distance between focus and center are multiplied
            var gradient = ctx.createRadialGradient(fX, fY, 0, fX + repeatCount * dx, fY + repeatCount * dy, r * repeatCount);
            for (var rep = 0; rep < repeatCount; rep++) {
                if (g.repeat != CyclicMethodProto.REFLECT || rep % 2 == 0) {
                    for (var i = 0; i < g.colors.length; i++) {
                        gradient.addColorStop((rep + g.fractions[i]) / repeatCount, parseColor(g.colors[i]));
                    }
                } else {
                    // reflect colors
                    for (var i = g.colors.length - 1; i >= 0; i--) {
                        gradient.addColorStop((rep + (1 - g.fractions[i])) / repeatCount, parseColor(g.colors[i]));
                    }
                }
            }
            return gradient;
        }
        
        // fix gradient focus point as java does
        function fixFocusPoint(gradient) {
            var dx = gradient.xFocus - gradient.xCenter;
            var dy = gradient.yFocus - gradient.yCenter;
            if (dx == 0 && dy == 0) {
                return;
            }
            var scaleBack = 0.99;
            var radiusSq = gradient.radius * gradient.radius;
            var distSq = (dx * dx) + (dy * dy);
            // test if distance from focus to center is greater than the radius
            if (distSq > radiusSq * scaleBack) {
                // clamp focus to radius
                var scale = Math.sqrt(radiusSq * scaleBack / distSq);
                // modify source object to skip fixes later
                gradient.xFocus = gradient.xCenter + dx * scale;
                gradient.yFocus = gradient.yCenter + dy * scale;
            }
        }
        
        function getDistance(x0, y0, x1, y1) {
            return Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
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

		function path(ctx, arg, biased) {
		    if (arg == null) {
		        return false;
		    }
		
			if (arg.rectangle != null) {
			    ctx.beginPath();
				pathRectangle(ctx, arg.rectangle, biased);
				return true;
			}
			
			if (arg.roundRectangle != null) {
			    ctx.beginPath();
				pathRoundRectangle(ctx, arg.roundRectangle, biased);
				return true;
			}

			if (arg.ellipse != null) {
			    ctx.beginPath();
				pathEllipse(ctx, arg.ellipse, biased);
				return true;
			}

			if (arg.arc != null) {
			    ctx.beginPath();
				pathArc(ctx, arg.arc, biased);
				return true;
			}

			// generic path
			if (arg.path != null) {
			    ctx.beginPath();
				var path = arg.path;
				var bias = calculateBias(ctx, biased);
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
						ctx.quadraticCurveTo(path.points[off + 0] + bias, path.points[off + 1] + bias,
						                     path.points[off + 2] + bias, path.points[off + 3] + bias);
						off += 4;
						break;
					case SegmentTypeProto.CUBIC:
						ctx.bezierCurveTo(path.points[off + 0] + bias, path.points[off + 1] + bias,
						                  path.points[off + 2] + bias, path.points[off + 3] + bias,
								          path.points[off + 4] + bias, path.points[off + 5] + bias);
						off += 6;
						break;
					case SegmentTypeProto.CLOSE:
						ctx.closePath();
						break;
					default:
						console.log("segment.type:" + segment.type + " not recognized");
					}
				});
				return true;
			}
			return false;
		}

		function pathRectangle(ctx, rect, biased) {
			var bias = calculateBias(ctx, biased);
			ctx.rect(rect.x + bias, rect.y + bias, rect.w, rect.h);
		}

		function pathEllipse(ctx, elli, biased) {
			var bias = calculateBias(ctx, biased);
			var kappa = 0.5522847498307933;
			var pcv = 0.5 + kappa * 0.5;
            var ncv = 0.5 - kappa * 0.5;

			ctx.moveTo(elli.x + bias + elli.w, elli.y + bias + 0.5 * elli.h);
			var pts = getEllipseCoords([ 1.0,  pcv,  pcv,  1.0,  0.5,  1.0 ], elli, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
			pts = getEllipseCoords([ ncv,  1.0,  0.0,  pcv,  0.0,  0.5 ], elli, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
			pts = getEllipseCoords([ 0.0,  ncv,  ncv,  0.0,  0.5,  0.0 ], elli, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
			pts = getEllipseCoords([ pcv,  0.0,  1.0,  ncv,  1.0,  0.5 ], elli, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
			ctx.closePath();
		}

		function getEllipseCoords(pts, elli, bias) {
			pts[0] = elli.x + bias + pts[0] * elli.w;
			pts[1] = elli.y + bias + pts[1] * elli.h;
			pts[2] = elli.x + bias + pts[2] * elli.w;
			pts[3] = elli.y + bias + pts[3] * elli.h;
			pts[4] = elli.x + bias + pts[4] * elli.w;
			pts[5] = elli.y + bias + pts[5] * elli.h;
			return pts;
		}

		function pathRoundRectangle(ctx, rr, biased) {
			var bias = calculateBias(ctx, biased);
			var acv = 0.22385762508460333;
			
			var pts = getRRCoords([ 0, 0, 0, 0.5 ], rr, bias);
			ctx.moveTo(pts[0], pts[1]);

			pts = getRRCoords([ 0, 0, 1, -0.5 ], rr, bias);
			ctx.lineTo(pts[0], pts[1]);
			pts = getRRCoords([ 0, 0, 1, -acv, 0, acv, 1, 0, 0, 0.5, 1, 0 ], rr, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

			pts = getRRCoords([ 1, -0.5, 1, 0 ], rr, bias);
			ctx.lineTo(pts[0], pts[1]);
			pts = getRRCoords([ 1, -acv, 1, 0, 1, 0, 1, -acv, 1, 0, 1, -0.5 ], rr, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

			pts = getRRCoords([ 1, 0, 0, 0.5 ], rr, bias);
			ctx.lineTo(pts[0], pts[1]);
			pts = getRRCoords([ 1, 0, 0, acv, 1, -acv, 0, 0, 1, -0.5, 0, 0 ], rr, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

			pts = getRRCoords([ 0, 0.5, 0, 0 ], rr, bias);
			ctx.lineTo(pts[0], pts[1]);
			pts = getRRCoords([ 0, acv, 0, 0, 0, 0, 0, acv, 0, 0, 0, 0.5 ], rr, bias);
			ctx.bezierCurveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);

			ctx.closePath();
		}
		
		function getRRCoords(pts, rr, bias) {
            var coords = [];
            var nc = 0;
            for (var i = 0; i < pts.length; i += 4) {
                coords[nc++] = rr.x + bias + pts[i + 0] * rr.w + pts[i + 1] * Math.abs(rr.arcW);
                coords[nc++] = rr.y + bias + pts[i + 2] * rr.h + pts[i + 3] * Math.abs(rr.arcH);
            }
            return coords;
        }

		function pathArc(ctx, arc, biased) {
			var bias = calculateBias(ctx, biased);
			var w = arc.w / 2, h = arc.h / 2, x = arc.x + bias + w, y = arc.y + bias + h;
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
			for (var i = 0; i < arcSegs; i++) {
				var angle = angStRad + increment * i;
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
		
		function calculateBias(ctx, biased) {
		    return (ctx.lineWidth & 1) && biased ? 0.5 : 0;
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
