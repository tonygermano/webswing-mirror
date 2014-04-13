(function() {
	"use strict";

	var initializingDialog = $('#initializingDialog');
	var connectingDialog = $('#connectingDialog');
	var applicationSelectorDialog = $('#applicationSelectorDialog');
	var continueOldSessionDialog = $('#continueOldSessionDialog');
	var startingDialog = $('#startingDialog');
	var stoppedDialog = $('#stoppedDialog');
	var disconnectedDialog = $('#disconnectedDialog');
	var tooManyConnectionsDialog = $('#tooManyConnectionsDialog');
	var applicationAlreadyRunningDialog = $('#applicationAlreadyRunningDialog');
	var clientId = setupClientID();
	var appName = null;
	var uuid = null;
	var latestMouseMoveEvent = null;
	var latestMouseWheelEvent = null;
	var latestWindowResizeEvent = null;
	var mouseDown = 0;
	var canvas = null;
	var socket = null;
	var transport = 'websocket';
	var fallbackTransport = 'long-polling';

	window.webswing = {
		continueSession : function(toContinue) {
			if (toContinue) {
				showDialog(null);
				socket.push('repaint' + clientId);
				socket.push('paintAck' + clientId);
			} else {
				socket.push('killSwing' + clientId);
				eraseCookie('webswingID');
				location.reload();
			}
		},
		startApplication : function(name) {
			appName = name;
			clientId = clientId+appName;
			socket.push(atmosphere.util.stringifyJSON(getHandShake()));
			showDialog(startingDialog);
		}
	};

	start();

	function start() {
		createCanvas();
		showDialog(initializingDialog);
		connect();
		setInterval(mouseMoveEventFilter, 100);
		setInterval(heartbeat, 10000);
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
			transport = response.transport;
		};

		request.onReopen = function(response) {
			showDialog(null);
		};

		request.onMessage = function(response) {
			var message = response.responseBody;
			try {
				var data = atmosphere.util.parseJSON(message);
			} catch (e) {
				if (message == "shutDownNotification") {
					showDialog(stoppedDialog);
					atmosphere.unsubscribe();
				} else if (message == "applicationAlreadyRunning") {
					showDialog(applicationAlreadyRunningDialog);
					atmosphere.unsubscribe();
				} else if (message == "tooManyClientsNotification") {
					showDialog(tooManyConnectionsDialog);
					atmosphere.unsubscribe();
				} else if (message == "continueOldSession") {
					showDialog(continueOldSessionDialog);
				}
				return;
			}
			if (appName == null) {
				selectApplication(data);
			} else {
				if (!continueOldSessionDialog.hasClass('in')) {// check if open
					processRequest(data);
				}
			}
		};

		request.onClose = function(response) {
			// need to wait until animated transition finish
			setTimeout(function() {
				if (!stoppedDialog.hasClass('in') && !applicationAlreadyRunningDialog.hasClass('in') && !tooManyConnectionsDialog.hasClass('in')) {
					showDialog(disconnectedDialog);
				}
			}, 3000);
		};

		request.onError = function(response) {
			// TODO:handle
		};

		request.onReconnect = function(request, response) {
			showDialog(initializingDialog);
		};

		socket = atmosphere.subscribe(request);
	}

	function selectApplication(data) {
		for ( var i in data.applications) {
			var app = data.applications[i];
			$('#applicationsList').append('<div class="col-sm-6 col-md-4"><div class="thumbnail" onclick="webswing.startApplication(\'' + app.name + '\')"><img src="data:image/png;base64,' + app.base64Icon + '" class="img-thumbnail"/><div class="caption">' + app.name + '</div></div></div>');
		}
		if (i == 0) {
			webswing.startApplication(data.applications[i].name);
		} else {
			showDialog(applicationSelectorDialog);
		}
	}

	function processRequest(data) {
		showDialog(null);

		if (data.linkAction != null) {
			if (data.linkAction.action == 'url') {
				window.open(data.linkAction.url, '_blank');
			} else if (data.linkAction.action == 'print') {
				window.open('/print/viewer.html?file=' + encodeURIComponent('../file?id=' + data.linkAction.url), '_blank');
			} else if (data.linkAction.action == 'file') {
				downloadURL('/file?id=' + data.linkAction.url);
			}
		}
		if (data.moveAction != null) {
			copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height);
		}
		if (data.cursorChange != null) {
			canvas.style.cursor = data.cursorChange.cursor;
		}
		if (data.copyEvent != null) {
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
		socket.push("paintAck" + clientId);
	}

	function draw(x, y, b64image) {
		var context, imageObj;
		context = canvas.getContext("2d");
		imageObj = new Image();
		imageObj.onload = function() {
			context.drawImage(imageObj, x, y);
			imageObj.onload=null;
			imageObj.src=null;
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

	function mouseMoveEventFilter() {
		if (latestMouseMoveEvent != null) {
			socket.push(atmosphere.util.stringifyJSON(latestMouseMoveEvent));
			latestMouseMoveEvent = null;
		}
		if (latestMouseWheelEvent != null) {
			socket.push(atmosphere.util.stringifyJSON(latestMouseWheelEvent));
			latestMouseWheelEvent = null;
		}
		if (latestWindowResizeEvent != null) {
			socket.push(atmosphere.util.stringifyJSON(latestWindowResizeEvent));
			latestWindowResizeEvent = null;
		}
	}

	function heartbeat() {
		socket.push('hb' + clientId);
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
			// hanle paste event
			if (keyevt.ctrl && keyevt.character == 86) {// ctrl+v
				var text = prompt('Press ctrl+v and enter..');
				var pasteEvent = {
					content : text,
					clientId : clientId
				};
				socket.push(atmosphere.util.stringifyJSON(pasteEvent));
			} else {
				socket.push(atmosphere.util.stringifyJSON(keyevt));
			}
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

	function setupClientID() {
		var cookieName = 'webswingID';
		var id = readCookie(cookieName);
		if (id != null) {
			eraseCookie(cookieName);
		} else {
			id = GUID();
		}
		createCookie(cookieName, id, 1);
		return id;
	}

	function GUID() {
		var S4 = function() {
			return Math.floor(Math.random() * 0x10000).toString(16);
		};
		return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
	}

	function createCookie(name, value, days) {
		var expires;

		if (days) {
			var date = new Date();
			date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
			expires = "; expires=" + date.toGMTString();
		} else {
			expires = "";
		}
		document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
	}

	function readCookie(name) {
		var nameEQ = escape(name) + "=";
		var ca = document.cookie.split(';');
		for ( var i = 0; i < ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0) === ' ')
				c = c.substring(1, c.length);
			if (c.indexOf(nameEQ) === 0)
				return unescape(c.substring(nameEQ.length, c.length));
		}
		return null;
	}

	function eraseCookie(name) {
		createCookie(name, "", -1);
	}

	function bindEvent(el, eventName, eventHandler) {
		if (el.addEventListener) {
			el.addEventListener(eventName, eventHandler, false);
		} else if (el.attachEvent) {
			el.attachEvent('on' + eventName, eventHandler);
		}
	}

	function createCanvas() {
		$("#body").append('<canvas id="canvas" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
		canvas = document.getElementById("canvas");
		registerEventListeners(canvas);
		window.onresize = function() {
			canvas.width = width();
			canvas.height = height();
			latestWindowResizeEvent = getHandShake();
		};
	}

	function showDialog(dialog) {
		connectingDialog.modal('hide');
		startingDialog.modal('hide');
		initializingDialog.modal('hide');
		stoppedDialog.modal('hide');
		disconnectedDialog.modal('hide');
		continueOldSessionDialog.modal('hide');
		applicationSelectorDialog.modal('hide');
		applicationAlreadyRunningDialog.modal('hide');
		tooManyConnectionsDialog.modal('hide');
		if (dialog != null) {
			dialog.modal('show');
		}
	}

	function downloadURL(url) {
		var hiddenIFrameID = 'hiddenDownloader', iframe = document.getElementById(hiddenIFrameID);
		if (iframe === null) {
			iframe = document.createElement('iframe');
			iframe.id = hiddenIFrameID;
			iframe.style.display = 'none';
			document.body.appendChild(iframe);
		}
		iframe.src = url;
	}
	;
	function width() {
		return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 0;
	}

	function height() {
		return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 0;
	}

})();
