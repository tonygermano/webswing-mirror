(function(root, factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define([ 'webswing-dd' ], factory);
	} else {
		root.WebswingBase = factory(root.WebswingDirectDraw);
	}
}(this, function(WebswingDirectDraw) {
	"use strict";

	var api;

	var timer1, timer2;
	var latestMouseMoveEvent = null;
	var latestMouseWheelEvent = null;
	var latestWindowResizeEvent = null;
	var mouseDown = 0;
	var inputEvtQueue = [];

	var windowImageHolders = {};
	var directDraw = new WebswingDirectDraw({});

	function startApplication(name) {
		api.canvas.create();
		registerEventListeners(api.canvas.get());
		resetState();
		api.context = {
			clientId : api.login.user() + api.identity.get() + name,
			appName : name,
			hasControl : true,
			mirrorMode : false,
			canPaint : true
		}
		handshake();
		api.dialog.show(api.dialog.content.startingDialog);
	}

	function startMirrorView(clientId) {
		api.canvas.create();
		registerEventListeners(api.canvas.get());
		resetState();
		api.context = {
			clientId : clientId,
			appName : null,
			hasControl : false,
			mirrorMode : true,
			canPaint : true
		}
		handshake();
		api.dialog.show(api.dialog.content.startingDialog);
	}

	function continueSession() {
		api.dialog.hide();
		api.context.canPaint = true;
		handshake();
		repaint();
		ack();
	}

	function resetState() {
		api.context = {
			clientId : '',
			appName : null,
			hasControl : false,
			mirrorMode : false,
			canPaint : false
		};
		clearInterval(timer1);
		clearInterval(timer2);
		timer1 = setInterval(sendInput, 100);
		timer2 = setInterval(heartbeat, 10000);
		latestMouseMoveEvent = null;
		latestMouseWheelEvent = null;
		latestWindowResizeEvent = null;
		mouseDown = 0;
		inputEvtQueue = [];
		windowImageHolders = {};
		directDraw = new WebswingDirectDraw({});
	}

	function sendInput() {
		if (api.context.hasControl) {
			enqueueInputEvent();
			if (inputEvtQueue.length > 0) {
				api.socket.send({
					events : inputEvtQueue
				});
				inputEvtQueue = [];
			}
		}
	}

	function enqueueMessageEvent(message) {
		inputEvtQueue.push(getMessageEvent(message));
	}

	function enqueueInputEvent(message) {
		if (latestMouseMoveEvent != null) {
			inputEvtQueue.push(latestMouseMoveEvent);
			latestMouseMoveEvent = null;
		}
		if (latestMouseWheelEvent != null) {
			inputEvtQueue.push(latestMouseWheelEvent);
			latestMouseWheelEvent = null;
		}
		if (latestWindowResizeEvent != null) {
			inputEvtQueue.push(latestWindowResizeEvent);
			latestWindowResizeEvent = null;
		}
		if (message != null) {
			if (JSON.stringify(inputEvtQueue[inputEvtQueue.length - 1]) !== JSON.stringify(message)) {
				inputEvtQueue.push(message);
			}
		}
	}

	function heartbeat() {
		enqueueMessageEvent('hb');
	}

	function repaint() {
		enqueueMessageEvent('repaint');
	}

	function ack() {
		enqueueMessageEvent('paintAck');
	}

	function kill() {
		enqueueMessageEvent('killSwing');
	}

	function unload() {
		enqueueMessageEvent('unload');
	}

	function handshake() {
		inputEvtQueue.push(getHandShake(api.canvas.get()));
	}

	function dispose() {
		clearInterval(timer1);
		clearInterval(timer2);
		unload();
		resetState();
	}

	function processMessage(data) {
		if (data.applications != null) {
			api.selector.show(data.applications);
		}
		if (data.event != null) {
			if (data.event == "shutDownNotification") {
				api.dialog.show(api.dialog.content.stoppedDialog);
				api.socket.dispose();
				api.canvas.dispose();
				dispose();
			} else if (data.event == "applicationAlreadyRunning") {
				api.dialog.show(api.dialog.content.applicationAlreadyRunning);
			} else if (data.event == "tooManyClientsNotification") {
				api.dialog.show(api.dialog.content.tooManyClientsNotification);
			} else if (data.event == "continueOldSession") {
				api.context.canPaint = false;
				api.dialog.show(api.dialog.content.continueOldSessionDialog);
			}
			return;
		}
		if (api.context.canPaint) {
			processRequest(api.canvas.get(), data);
		}
	}

	function processRequest(canvas, data) {
		api.dialog.hide();
		var context = canvas.getContext("2d");
		if (data.linkAction != null) {
			if (data.linkAction.action == 'url') {
				api.files.link(data.linkAction.src);
			} else if (data.linkAction.action == 'print') {
				api.files.print(encodeURIComponent('/file?id=' + data.linkAction.src));
			} else if (data.linkAction.action == 'file') {
				api.files.download('/file?id=' + data.linkAction.src);
			}
		}
		if (data.moveAction != null) {
			copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height,
					context);
		}
		if (data.cursorChange != null && api.context.hasControl) {
			canvas.style.cursor = data.cursorChange.cursor;
		}
		if (data.copyEvent != null && api.context.hasControl) {
			window.prompt("Copy to clipboard: Ctrl+C, Enter", data.copyEvent.content);
		}
		if (data.fileDialogEvent != null && api.context.hasControl) {
			if (data.fileDialogEvent.eventType === 'Open') {
				api.files.open(data.fileDialogEvent, api.context.clientId);
			} else if (data.fileDialogEvent.eventType === 'Close') {
				api.files.close();
			}
		}
		if (data.closedWindow != null) {
			delete windowImageHolders[data.closedWindow];
		}
		// firs is always the background
		for ( var i in data.windows) {
			var win = data.windows[i];
			if (win.id == 'BG') {
				if (api.context.mirrorMode) {
					adjustCanvasSize(canvas, win.width, win.height);
				}
				for ( var x in win.content) {
					var winContent = win.content[x];
					if (winContent != null) {
						clear(win.posX + winContent.positionX, win.posY + winContent.positionY, winContent.width, winContent.height, context);
					}
				}
				data.windows.splice(i, 1);
				break;
			}
		}
		// regular windows (background removed)
		if (data.windows != null) {
			data.windows.reduce(
					function(sequence, win) {
						if (win.directDraw != null) {
							// directdraw
							return sequence.then(function(resolved) {
								return directDraw.drawBin(win.directDraw, windowImageHolders[win.id]);
							}).then(
									function(resultImage) {
										windowImageHolders[win.id] = resultImage;
										for ( var x in win.content) {
											var winContent = win.content[x];
											if (winContent != null) {
												context.drawImage(resultImage, winContent.positionX, winContent.positionY, winContent.width,
														winContent.height, win.posX + winContent.positionX, win.posY + winContent.positionY,
														winContent.width, winContent.height);
											}
										}
									});
						} else {
							// imagedraw
							return sequence.then(function(resolved) {
								return win.content.reduce(function(internalSeq, winContent) {
									return internalSeq.then(function(done) {
										return new Promise(function(resolved, rejected) {
											if (winContent != null) {
												var imageObj = new Image();
												imageObj.onload = function() {
													context.drawImage(imageObj, win.posX + winContent.positionX, win.posY + winContent.positionY);
													imageObj.onload = null;
													imageObj.src = '';
													resolved();
												};
												imageObj.src = 'data:image/png;base64,' + winContent.base64Content;
											}
										});
									});
								}, Promise.resolve());
							});
						}
					}, Promise.resolve()).then(function() {
				ack();
			});
		}
	}

	function adjustCanvasSize(canvas, width, height) {
		if (canvas.width != width || canvas.height != height) {
			canvas.width = width;
			canvas.height = height;
		}
	}

	function clear(x, y, w, h, context) {
		context.clearRect(x, y, w, h);
	}

	function copy(sx, sy, dx, dy, w, h, context) {
		var copy = context.getImageData(sx, sy, w, h);
		context.putImageData(copy, dx, dy);
	}

	function registerEventListeners(canvas) {
		bindEvent(canvas, 'mousedown', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousedown');
			latestMouseMoveEvent = null;
			enqueueInputEvent(mousePos);
			canvas.focus();
			return false;
		}, false);
		bindEvent(canvas, 'dblclick', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'dblclick');
			latestMouseMoveEvent = null;
			enqueueInputEvent(mousePos);
			canvas.focus();
			return false;
		}, false);
		bindEvent(canvas, 'mousemove', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousemove');
			mousePos.mouse.button = mouseDown;
			latestMouseMoveEvent = mousePos;
			return false;
		}, false);
		bindEvent(canvas, 'mouseup', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mouseup');
			latestMouseMoveEvent = null;
			enqueueInputEvent(mousePos);
			return false;
		}, false);
		// IE9, Chrome, Safari, Opera
		bindEvent(canvas, "mousewheel", function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousewheel');
			latestMouseMoveEvent = null;
			if (latestMouseWheelEvent != null) {
				mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
			}
			latestMouseWheelEvent = mousePos;
			return false;
		}, false);
		// firefox
		bindEvent(canvas, "DOMMouseScroll", function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousewheel');
			latestMouseMoveEvent = null;
			if (latestMouseWheelEvent != null) {
				mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
			}
			latestMouseWheelEvent = mousePos;
			return false;
		}, false);
		bindEvent(canvas, 'contextmenu', function(event) {
			event.preventDefault();
			event.stopPropagation();
			return false;
		});

		bindEvent(canvas, 'keydown', function(event) {
			// 48-57
			// 65-90
			// 186-192
			// 219-222
			// 226
			// FF (163, 171, 173, ) -> en layout ]\/ keys
			var kc = event.keyCode;
			if (!((kc >= 48 && kc <= 57) || (kc >= 65 && kc <= 90) || (kc >= 186 && kc <= 192) || (kc >= 219 && kc <= 222) || (kc == 226)
					|| (kc == 0) || (kc == 163) || (kc == 171) || (kc == 173) || (kc >= 96 && kc <= 111))) {
				event.preventDefault();
				event.stopPropagation();
			}
			var keyevt = getKBKey('keydown', canvas, event);
			// hanle paste event
			if (keyevt.key.ctrl && keyevt.key.character == 86) { // ctrl+v
				var text = prompt('Press ctrl+v and enter..');
				if (api.context.hasControl) {
					api.socket.send({
						paste : {
							content : text,
							clientId : api.context.clientId
						}
					});
				}
			} else {
				// default action prevented
				if (keyevt.key.ctrl && !keyevt.key.alt && !keyevt.key.altgr) {
					event.preventDefault();
				}
				enqueueInputEvent(keyevt);
			}
			return false;
		}, false);
		bindEvent(canvas, 'keypress', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keypress', canvas, event);
			enqueueInputEvent(keyevt);
			return false;
		}, false);
		bindEvent(canvas, 'keyup', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keyup', canvas, event);
			enqueueInputEvent(keyevt);
			return false;
		}, false);
		bindEvent(document, 'mousedown', function(evt) {
			if (evt.which == 1) {
				mouseDown = 1;
			}
		});
		bindEvent(document, 'mouseout', function(evt) {
			mouseDown = 0;
		});
		bindEvent(document, 'mouseup', function(evt) {
			if (evt.which == 1) {
				mouseDown = 0;
			}
		});
	}

	function getMousePos(canvas, evt, type) {
		var rect = canvas.getBoundingClientRect();
		var root = document.documentElement;
		// return relative mouse position
		var mouseX = evt.clientX - rect.left - root.scrollTop;
		var mouseY = evt.clientY - rect.top - root.scrollLeft;
		var delta = 0;
		if (type == 'mousewheel') {
			delta = -Math.max(-1, Math.min(1, (evt.wheelDelta || -evt.detail)));
		}
		return {
			mouse : {
				clientId : api.context.clientId,
				x : mouseX,
				y : mouseY,
				type : type,
				wheelDelta : delta,
				button : evt.which,
				ctrl : evt.ctrlKey,
				alt : evt.altKey,
				shift : evt.shiftKey,
				meta : evt.metaKey
			}
		};
	}

	function getKBKey(type, canvas, evt) {
		var char = evt.which;
		if (char == 0 && evt.key != null) {
			char = evt.key.charCodeAt(0);
		}
		var kk = evt.keyCode;
		if (kk == 0) {
			kk = char;
		}
		return {
			key : {
				clientId : api.context.clientId,
				type : type,
				character : char,
				keycode : kk,
				alt : evt.altKey,
				ctrl : evt.ctrlKey,
				shift : evt.shiftKey,
				meta : evt.metaKey,
				altgr : evt.altGraphKey
			}
		};
	}

	function getHandShake(canvas) {
		var handshake = {
			handshake : {
				applicationName : api.context.appName,
				clientId : api.context.clientId,
				sessionId : api.socket.uuid(),
				desktopWidth : canvas.offsetWidth,
				desktopHeight : canvas.offsetHeight,
				mirrored : api.context.mirrorMode,
				directDrawSupported : api.typedArraysSupported
			}
		};
		return handshake;
	}

	function getMessageEvent(message) {
		return {
			event : {
				type : message,
				clientId : api.context.clientId
			}
		};
	}

	function bindEvent(el, eventName, eventHandler) {
		if (el.addEventListener != null) {
			el.addEventListener(eventName, eventHandler);
		} else {
			el.bind(eventName, eventHandler);
		}
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			api.context = {
				clientId : '',
				appName : null,
				hasControl : false,
				mirrorMode : false,
				canPaint : false
			}
			api.base = {
				startApplication : startApplication,
				startMirrorView : startMirrorView,
				continueSession : continueSession,

				kill : kill,
				handshake : handshake,
				processMessage : processMessage,
				dispose : dispose,
			}
		}
	};
}));
