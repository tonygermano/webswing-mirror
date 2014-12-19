function WebswingBase(c) {
	"use strict";
	var config = {
		send : c.send || function() {
		},
		onErrorMessage : c.onErrorMessage || function() {
		},
		onContinueOldSession : c.onContinueOldSession || function() {
		},
		onApplicationSelection : c.onApplicationSelection || function() {
		},
		onBeforePaint : c.onBeforePaint || function() {
		},
		onLinkOpenAction : c.onLinkOpenAction || function() {
		},
		onPrintAction : c.onPrintAction || function() {
		},
		onFileDownloadAction : c.onFileDownloadAction || function() {
		},
		onFileDialogAction : c.onFileDialogAction || function() {
		},
		clientId : c.clientId || '',
		hasControl : c.hasControl || false,
		mirrorMode : c.mirrorMode || false
	}

	var clientId = config.clientId;
	var appName = null;
	var uuid = null;
	var latestMouseMoveEvent = null;
	var latestMouseWheelEvent = null;
	var latestWindowResizeEvent = null;
	var canvas;
	var mouseDown = 0;
	var user = null;
	var canPaint = false;
	var mirrorMode = config.mirrorMode;

	var proto = dcodeIO.ProtoBuf.loadProtoFile("directdraw.proto");
	var ddCanvas = document.createElement("canvas");
	var directDraw = new WebswingDirectDraw({
		canvas : ddCanvas,
		proto : proto
	});

	var timer1 = setInterval(mouseMoveEventFilter, 100);
	var timer2 = setInterval(heartbeat, 10000);

	function send(message) {
		config.send(message);
	}

	function sendInput(message) {
		if (config.hasControl) {
			send(message);
		}
	}

	function heartbeat() {
		send('hb' + clientId);
	}

	function mouseMoveEventFilter() {
		if (latestMouseMoveEvent != null && config.hasControl) {
			send(latestMouseMoveEvent);
			latestMouseMoveEvent = null;
		}
		if (latestMouseWheelEvent != null && config.hasControl) {
			send(latestMouseWheelEvent);
			latestMouseWheelEvent = null;
		}
		if (latestWindowResizeEvent != null && config.hasControl) {
			send(latestWindowResizeEvent);
			latestWindowResizeEvent = null;
		}
	}

	function setCanvas(c) {
		canvas = c;
		registerEventListeners(c);
	}

	function repaint() {
		send('repaint' + clientId);
	}

	function ack() {
		send('paintAck' + clientId);
	}

	function kill() {
		send('killSwing' + clientId);
	}

	function unload() {
		send('unload' + clientId);
	}

	function requestDownloadFile() {
		send('downloadFile' + clientId);
	}

	function requestDeleteFile() {
		send('deleteFile' + clientId);
	}

	function handshake() {
		send(getHandShake());
	}

	function resizedWindow() {
		latestWindowResizeEvent = getHandShake();
	}

	function dispose() {
		clearInterval(timer1);
		clearInterval(timer2);
		canvas.parentNode.replaceChild(canvas.cloneNode(true), canvas);
		unload();
		c = {};
	}

	function processTxtMessage(message) {
		if (message == "shutDownNotification") {
			config.onErrorMessage('Application stopped...');
		} else if (message == "applicationAlreadyRunning") {
			config.onErrorMessage('Application is already running in other browser window...');
		} else if (message == "tooManyClientsNotification") {
			config.onErrorMessage('Too many connections. Please try again later...');
		} else if (message == "continueOldSession") {
			config.onContinueOldSession();
		}
		return;
	}

	function processJsonMessage(data) {
		if (data.user != null) {
			user = data.user;
		}
		if (data.applications != null) {
			config.onApplicationSelection(data.applications);
		}
		if (canPaint) {
			processRequest(data);
		}
	}

	function processRequest(data) {
		config.onBeforePaint();
		var context;
		context = canvas.getContext("2d");
		if (data.linkAction != null) {
			if (data.linkAction.action == 'url') {
				config.onLinkOpenAction(data.linkAction.url);
			} else if (data.linkAction.action == 'print') {
				config.onPrintAction(data.linkAction.url);
			} else if (data.linkAction.action == 'file') {
				config.onFileDownloadAction(data.linkAction.url);
			}
		}
		if (data.moveAction != null) {
			copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height,context);
		}
		if (data.cursorChange != null && config.hasControl) {
			canvas.style.cursor = data.cursorChange.cursor;
		}
		if (data.copyEvent != null && config.hasControl) {
			window.prompt("Copy to clipboard: Ctrl+C, Enter", data.copyEvent.content);
		}
		if (data.fileDialogEvent != null && config.hasControl) {
			config.onFileDialogAction(data.fileDialogEvent);
		}
		// firs is always the background
		for ( var i in data.windows) {
			var win = data.windows[i];
			if (win.id == 'BG') {
				if (mirrorMode) {
					adjustCanvasSize(win.width, win.height);
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
			var drawPromisesArray = data.windows.map(function(win) {
				return new Promise(function(resolve, reject) {
					if (win.directDrawB64 != null) {
						ddCanvas.width = win.width;
						ddCanvas.height = win.height;
						directDraw.draw64(win.directDrawB64).then(
								function() {
									for ( var x in win.content) {
										var winContent = win.content[x];
										if (winContent != null) {
											context.drawImage(ddCanvas, winContent.positionX, winContent.positionY, winContent.width,
													winContent.height, win.posX + winContent.positionX, win.posY + winContent.positionY,
													winContent.width, winContent.height);
										}
									}
									resolve();
								});
					} else {
						for ( var x in win.content) {
							var winContent = win.content[x];
							if (winContent != null) {
								var imageObj;
								imageObj = new Image();
								imageObj.onload = function() {
									context.drawImage(imageObj, win.posX + winContent.positionX, win.posY + winContent.positionY);
									imageObj.onload = null;
									imageObj.src = '';
								};
								imageObj.src = 'data:image/png;base64,' + winContent.base64Content;
							}
						}
					}
				});
			});
			Promise.all(drawPromisesArray).then(function() {
				ack();
			})
		}
	}

	function adjustCanvasSize(width, height) {
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
			sendInput(mousePos);
			canvas.focus();
			return false;
		}, false);
		bindEvent(canvas, 'dblclick', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'dblclick');
			latestMouseMoveEvent = null;
			sendInput(mousePos);
			canvas.focus();
			return false;
		}, false);
		bindEvent(canvas, 'mousemove', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousemove');
			mousePos.button = mouseDown;
			latestMouseMoveEvent = mousePos;
			return false;
		}, false);
		bindEvent(canvas, 'mouseup', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mouseup');
			latestMouseMoveEvent = null;
			sendInput(mousePos);
			return false;
		}, false);
		// IE9, Chrome, Safari, Opera
		bindEvent(canvas, "mousewheel", function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousewheel');
			latestMouseMoveEvent = null;
			if (latestMouseWheelEvent != null) {
				mousePos.wheelDelta += latestMouseWheelEvent.wheelDelta;
			}
			latestMouseWheelEvent = mousePos;
			return false;
		}, false);
		// firefox
		bindEvent(canvas, "DOMMouseScroll", function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousewheel');
			latestMouseMoveEvent = null;
			if (latestMouseWheelEvent != null) {
				mousePos.wheelDelta += latestMouseWheelEvent.wheelDelta;
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
			if (keyevt.ctrl && keyevt.character == 86) { // ctrl+v
				var text = prompt('Press ctrl+v and enter..');
				var pasteEvent = {
					content : text,
					clientId : clientId
				};
				sendInput(pasteEvent);
			} else {
				// default action prevented
				if (keyevt.ctrl && !keyevt.alt && !keyevt.altgr) {
					event.preventDefault();
				}
				sendInput(keyevt);
			}
			return false;
		}, false);
		bindEvent(canvas, 'keypress', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keypress', canvas, event);
			sendInput(keyevt);
			return false;
		}, false);
		bindEvent(canvas, 'keyup', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keyup', canvas, event);
			sendInput(keyevt);
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
			clientId : clientId,
			x : mouseX,
			y : mouseY,
			type : type,
			wheelDelta : delta,
			button : evt.which,
			ctrl : evt.ctrlKey,
			alt : evt.altKey,
			shift : evt.shiftKey,
			meta : evt.metaKey
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
			clientId : clientId,
			type : type,
			character : char,
			keycode : kk,
			alt : evt.altKey,
			ctrl : evt.ctrlKey,
			shift : evt.shiftKey,
			meta : evt.metaKey,
			altgr : evt.altGraphKey
		};
	}

	function getHandShake() {
		var handshake = {
			applicationName : appName,
			clientId : clientId,
			sessionId : uuid,
			desktopWidth : canvas.width,
			desktopHeight : canvas.height,
			mirrored : mirrorMode
		};
		return handshake;
	}

	function bindEvent(el, eventName, eventHandler) {
		el.addEventListener(eventName, eventHandler, false);
	}

	return {
		repaint : function() {
			repaint();
		},
		ack : function() {
			ack();
		},
		kill : function() {
			kill();
		},
		handshake : function() {
			handshake();
		},
		requestDownloadFile : function() {
			requestDownloadFile();
		},
		requestDeleteFile : function() {
			requestDeleteFile();
		},
		setUuid : function(param) {
			uuid = param;
		},
		getUser : function() {
			return user;
		},
		resizedWindow : function() {
			resizedWindow();
		},
		setCanvas : function(c) {
			setCanvas(c);
		},
		setApplication : function(app) {
			appName = app;
		},
		getClientId : function() {
			return clientId;
		},
		setClientId : function(id) {
			clientId = id;
		},
		canPaint : function(bool) {
			canPaint = bool;
		},
		canControl : function(bool) {
			config.hasControl = bool;
		},
		processTxtMessage : function(message) {
			processTxtMessage(message);
		},
		processJsonMessage : function(data) {
			processJsonMessage(data);
		},
		dispose : function() {
			dispose();
		}
	};
}