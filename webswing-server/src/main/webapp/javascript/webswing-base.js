function WebswingBase(config){
	"use strict";
	
	var clientId = config.clientId;
	var appName = null;
	var uuid = null;
	var latestMouseMoveEvent = null;
	var latestMouseWheelEvent = null;
	var latestWindowResizeEvent = null;
	var canvas;
	var mouseDown = 0;
	var hasControl=config.hasControl;
	var user=null;
	var canPaint=false;

	setInterval(mouseMoveEventFilter, 100);
	setInterval(heartbeat, 10000);

	function send(message){
		config.send(message);
	}

	function sendInput(message){
		if(hasControl){
			send(message);
		}
	}

	function heartbeat() {
		send('hb' + clientId);
	}

	function mouseMoveEventFilter() {
		if (latestMouseMoveEvent != null && hasControl) {
			send(latestMouseMoveEvent);
			latestMouseMoveEvent = null;
		}
		if (latestMouseWheelEvent != null && hasControl) {
			send(latestMouseWheelEvent);
			latestMouseWheelEvent = null;
		}
		if (latestWindowResizeEvent != null && hasControl) {
			send(latestWindowResizeEvent);
			latestWindowResizeEvent = null;
		}
	}

	function setCanvas(c){
		canvas=c;
		registerEventListeners(c);
	}

	function repaint(){
		send('repaint' + clientId);
	}

	function ack(){
		send('paintAck' + clientId);
	}

	function kill(){
		if(hasControl){
			send('killSwing' + clientId);
		}
	}

	function handshake(){
		if(hasControl){
			send(getHandShake());
		}
	}

	function resizedWindow(){
		latestWindowResizeEvent = getHandShake();
	}

	function processTxtMessage(message){
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

	function processJsonMessage(data){
		if(data.user != null){
			user = data.user;
		}
		if(data.applications != null){
			config.onApplicationSelection( data.applications);
		}
		if(canPaint){
			processRequest(data);
		}
	}


	function processRequest(data) {
		config.onBeforePaint();

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
			copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height);
		}
		if (data.cursorChange != null && hasControl) {
			canvas.style.cursor = data.cursorChange.cursor;
		}
		if (data.copyEvent != null && hasControl) {
			window.prompt("Copy to clipboard: Ctrl+C, Enter", data.copyEvent.content);
		}
		for ( var i in data.windows) {
			var win = data.windows[i];
			for ( var x in win.content) {
				var winContent = win.content[x];
				if (winContent != null) {
					if (win.id == 'backgroundWindowId') {
						clear(win.posX + winContent.positionX, win.posY + winContent.positionY, winContent.width, winContent.height);
					} else {
						draw(win.posX + winContent.positionX, win.posY + winContent.positionY, winContent.base64Content);
					}
				}
			}
		}
		ack();
	}

	function draw(x, y, b64image) {
		var context, imageObj;
		context = canvas.getContext("2d");
		imageObj = new Image();
		imageObj.onload = function() {
			context.drawImage(imageObj, x, y);
			imageObj.onload = null;
			imageObj.src = '';
		};
		imageObj.src = 'data:image/png;base64,' + b64image;
	}

	function clear(x, y, w, h) {
		var context;
		context = canvas.getContext("2d");
		context.clearRect(x, y, w, h);
	}

	function copy(sx, sy, dx, dy, w, h) {
		var context;
		context = canvas.getContext("2d");
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
			var kc = event.keyCode;
			if (!((kc >= 48 && kc <= 57) || (kc >= 65 && kc <= 90) || (kc >= 186 && kc <= 192) || (kc >= 219 && kc <= 222) || (kc == 226))) {
				event.preventDefault();
				event.stopPropagation();
			}
			var keyevt = getKBKey('keydown', canvas, event);
			// hanle paste event
			if (keyevt.ctrl && keyevt.character == 86 ) {// ctrl+v
				var text = prompt('Press ctrl+v and enter..');
				var pasteEvent = {
					content : text,
					clientId : clientId
				};
				sendInput(pasteEvent);
			} else {
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
		var rect = canvas.getBoundingClientRect(), root = document.documentElement;
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
		return {
			clientId : clientId,
			type : type,
			character : evt.which,
			keycode : evt.keyCode,
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
			desktopHeight : canvas.height
		};
		return handshake;
	}

	function bindEvent(el, eventName, eventHandler) {
		if (el.addEventListener) {
			el.addEventListener(eventName, eventHandler, false);
		} else if (el.attachEvent) {
			el.attachEvent('on' + eventName, eventHandler);
		}
	}


	return {
		repaint : function(){
			repaint();
		},
		ack : function(){
			ack();
		},
		kill : function(){
			kill();
		},
		handshake : function(){
			handshake();
		},
		setUuid : function(param){
			uuid = param;
		},
		getUser : function(){
			return user;
		},
		resizedWindow : function(){
			resizedWindow();
		},
		setCanvas : function(c){
			setCanvas(c);
		},
		setApplication : function(app){
			appName = app;
		},
		getClientId : function(){
			return clientId;
		},
		setClientId : function(id){
			clientId = id;
		},
		canPaint : function(bool){
			canPaint = bool;
		},
		processTxtMessage : function(message){
			processTxtMessage(message);
		},
		processJsonMessage : function(data){
			processJsonMessage(data);
		}
	};
}