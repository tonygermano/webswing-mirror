(function() {
	"use strict";
	
	var initializingDialog=$('#initializingDialog');
	var connectingDialog=$('#connectingDialog');
	var startingDialog=$('#startingDialog');
	var stoppedDialog=$('#stoppedDialog');
	var tooManyConnectionsDialog=$('#tooManyConnectionsDialog');
	var clientId = GUID();
	var uuid=null;
	var latestMouseMoveEvent=null;
	var latestMouseWheelEvent=null;
	var mouseDown = 0;
	var canvas=null;
	var socket=null;
	var transport = 'websocket';
	var fallbackTransport = 'long-polling';
	
	
	start();

	function start() {
		createCanvas();
		showDialog(initializingDialog);
		connect();
		setInterval(mouseMoveEventFilter, 100);

		$(window).bind("beforeunload", function() {
			socket.push('unload' + clientId);
		});

	}
	
	function connect() {
		var request = {
			url : document.location.toString() + 'async/swing',
			contentType : "application/json",
			logLevel : 'debug',
			transport : transport,
			trackMessageLength : true,
			reconnectInterval : 5000,
			fallbackTransport : fallbackTransport
		};

		request.onOpen = function(response) {
			uuid = response.request.uuid;
			var handshake={
				applicationName : "SwingSet3",
				clientId : clientId,
				sessionId : uuid,
				desktopWidth : canvas.width,
				desktopHeight : canvas.height
			};
			socket.push(atmosphere.util.stringifyJSON(handshake));
			showDialog(startingDialog);
			transport = response.transport;
		};

		request.onReopen = request.onOpen;

		request.onMessage = function(response) {
			var message = response.responseBody;
			try {
				var data = atmosphere.util.parseJSON(message);
			} catch (e) {
				if (message == "shutDownNotification") {
					showDialog(stoppedDialog);
				} else if (message == "tooManyClientsNotification") {
					showDialog(tooManyConnectionsDialog);
				}
				return;
			}
			processRequest(data);
		};

		request.onClose = function(response) {
			socket.push('disconnecting');
		};

		request.onError = function(response) {
			//TODO:handle
		};

		request.onReconnect = function(request, response) {
			//TODO: handle
		};

		socket = atmosphere.subscribe(request);
	}
	
	function processRequest(data) {
		showDialog(null);
		
		if (data.linkAction != null) {
			window.open(data.linkAction.url, '_blank');
		}

		for (var i in data.windows) {
			var win=data.windows[i];
			var winContent=win.content;
			if(winContent!=null){
				draw(win.posX+winContent.positionX,win.posY+winContent.positionY, winContent.base64Content);
			}
		}
		socket.push("paintAck" + clientId);
	}

	function draw(x,y, b64image) {
		var context, imageObj;
		context = canvas.getContext("2d");
		imageObj = new Image();
		imageObj.onload = function() {
			context.drawImage(imageObj, x, y);
		};
		imageObj.src = 'data:image/png;base64,' + b64image;
	}

	function mouseMoveEventFilter() {
		if (latestMouseMoveEvent != null) {
			socket.push(atmosphere.util.stringifyJSON(latestMouseMoveEvent));
			latestMouseMoveEvent = null;
		}
		if (latestMouseWheelEvent != null) {
			socket.push(atmosphere.util.stringifyJSON(latestMouseWheelEvent));
			latestMouseWheelEvent = null;
		}
	}

	function registerEventListeners(canvas) {
		bindEvent(canvas, 'mousedown', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'mousedown');
			latestMouseMoveEvent = null;
			socket.push(atmosphere.util.stringifyJSON(mousePos));
			canvas.focus();
			return false;
		}, false);
		bindEvent(canvas, 'dblclick', function(evt) {
			var mousePos = getMousePos(canvas, evt, 'dblclick');
			latestMouseMoveEvent = null;
			socket.push(atmosphere.util.stringifyJSON(mousePos));
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
			socket.push(atmosphere.util.stringifyJSON(mousePos));
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
			socket.push(atmosphere.util.stringifyJSON(keyevt));
			return false;
		}, false);
		bindEvent(canvas, 'keypress', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keypress', canvas, event);
			socket.push(atmosphere.util.stringifyJSON(keyevt));
			return false;
		}, false);
		bindEvent(canvas, 'keyup', function(event) {
			event.preventDefault();
			event.stopPropagation();
			var keyevt = getKBKey('keyup', canvas, event);
			socket.push(atmosphere.util.stringifyJSON(keyevt));
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

	function GUID() {
		var S4 = function() {
			return Math.floor(Math.random() * 0x10000).toString(16);
		};
		return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
	}

	function bindEvent(el, eventName, eventHandler) {
		if (el.addEventListener) {
			el.addEventListener(eventName, eventHandler, false);
		} else if (el.attachEvent) {
			el.attachEvent('on' + eventName, eventHandler);
		}
	}
	
	function createCanvas() {
		//$("#root").append('<canvas id="canvas" width="' + $(window).width + '" height="' +  $(window).height + '" tabindex="-1"/>');
		$("#root").append('<canvas id="canvas" width="' + 1000 + '" height="' +  800 + '" tabindex="-1"/>');
		canvas = document.getElementById("canvas");
		registerEventListeners(canvas);
		return canvas;
	}

	
	function showDialog(dialog){
		connectingDialog.modal('hide');
		startingDialog.modal('hide');
		initializingDialog.modal('hide');
		stoppedDialog.modal('hide');
		if(dialog!=null){
			dialog.modal('show');
		}
	}
	
})();






