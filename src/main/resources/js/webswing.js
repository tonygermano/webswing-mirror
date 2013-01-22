var clientId = GUID();
var socket = io.connect('http://localhost:7070', {
	'reconnection delay' : 2000,
	'sync disconnect on unload' : true
});
var busy = {};
var nextRequest = {};
var latestMouseMoveEvent;
var canvasIndex = 1;
var seq = 0;
var mouseDown = 0;

function processRequest(data) {
	busy[data.windowInfo.id] = true;
	var canvas = document.getElementById(data.windowInfo.id);
	if (canvas == null) {
		canvas = createCanvasWinodow(data.windowInfo.id, data.windowInfo.title, data.windowInfo.width, data.windowInfo.height);
	}
	var context = canvas.getContext("2d");
	if (data.type == "paint") {
		var imageObj = new Image();
		imageObj.onload = function() {
			context.drawImage(imageObj, data.x, data.y);
			var currentDialog = $("#" + data.windowInfo.id + "Window");
			if (currentDialog.dialog("isOpen") == false) {
				currentDialog.dialog("open");
			}
			if (data.windowInfo.hasFocus) {
				currentDialog.dialog("moveToTop");
			}
			if (nextRequest[data.windowInfo.id] != null && nextRequest[data.windowInfo.id] != data) {
				processRequest(nextRequest[data.windowInfo.id]);
			} else {
				delete nextRequest[data.windowInfo.id];
				busy[data.windowInfo.id] = false;
			}
		};
		imageObj.src = 'swing/' + clientId + '/' + data.windowInfo.id + '/' + (seq++);
	}
}

function createCanvasWinodow(name, title, width, height) {
	$("#root").append('<div id="' + name + 'Window"><canvas id="' + name + '" width="' + width + '" height="' + height + '" tabindex="-1"/></div>');
	$("#" + name + "Window").dialog({
		width : "auto",
		heigth : "auto",
		title : title,
		resizable : "false",
		beforeClose : sendCloseWindowEvent
	});
	var canvas = document.getElementById(name);
	registerEventListeners(canvas);
	return canvas;
}

function sendCloseWindowEvent(event, ui) {
	var e = {
		'@class' : 'sk.viktor.ignored.model.c2s.JsonEventWindow',
		'type' : 'close',
		'windowId' : this.id.substring(0, this.id.length - 6),
		'clientId' : clientId
	};
	socket.json.send(e);
}

function mouseMoveEventFilter() {
	if (latestMouseMoveEvent != null) {
		socket.json.send(latestMouseMoveEvent);
		latestMouseMoveEvent = null;
	}
}

function registerEventListeners(canvas) {
	bindEvent(canvas,'mousedown', function(evt) {
		var mousePos = getMousePos(canvas, evt, 'mousedown');
		latestMouseMoveEvent = null;
		socket.json.send(mousePos);
		canvas.focus();
		return false;
	}, false);
	bindEvent(canvas,'dblclick', function(evt) {
		var mousePos = getMousePos(canvas, evt, 'dblclick');
		latestMouseMoveEvent = null;
		socket.json.send(mousePos);
		canvas.focus();
		return false;
	}, false);
	bindEvent(canvas,'mousemove', function(evt) {
		var mousePos = getMousePos(canvas, evt, 'mousemove');
		mousePos.button = mouseDown;
		latestMouseMoveEvent = mousePos;
		return false;
	}, false);
	bindEvent(canvas,'mouseup', function(evt) {
		var mousePos = getMousePos(canvas, evt, 'mouseup');
		latestMouseMoveEvent = null;
		socket.json.send(mousePos);
		return false;
	}, false);
	// IE9, Chrome, Safari, Opera
	bindEvent(canvas,"mousewheel", function(evt) {
		var mousePos = getMousePos(canvas, evt, 'mousewheel');
		latestMouseMoveEvent = null;
		socket.json.send(mousePos);
		return false;
	}, false);
	// firefox
	bindEvent(canvas,"DOMMouseScroll", function(evt) {
		var mousePos = getMousePos(canvas, evt, 'mousewheel');
		latestMouseMoveEvent = null;
		socket.json.send(mousePos);
		return false;
	}, false);
	bindEvent(canvas,'contextmenu', function(event) {
		event.preventDefault();
		event.stopPropagation();
		return false;
	});

	bindEvent(canvas,'keydown', function(event) {
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
		socket.json.send(keyevt);
		return false;
	}, false);
	bindEvent(canvas,'keypress', function(event) {
		event.preventDefault();
		event.stopPropagation();
		var keyevt = getKBKey('keypress', canvas, event);
		socket.json.send(keyevt);
		return false;
	}, false);
	bindEvent(canvas,'keyup', function(event) {
		event.preventDefault();
		event.stopPropagation();
		var keyevt = getKBKey('keyup', canvas, event);
		socket.json.send(keyevt);
		return false;
	}, false);

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
		'@class' : 'sk.viktor.ignored.model.c2s.JsonEventMouse',
		'windowId' : canvas.id,
		'clientId' : clientId,
		'x' : mouseX,
		'y' : mouseY,
		'type' : type,
		'wheelDelta' : delta,
		'button' : evt.which,
		'ctrl' : evt.ctrlKey,
		'alt' : evt.altKey,
		'shift' : evt.shiftKey,
		'meta' : evt.metaKey
	};
}

function getKBKey(type, canvas, evt) {
	return {
		'@class' : 'sk.viktor.ignored.model.c2s.JsonEventKeyboard',
		'windowId' : canvas.id,
		'clientId' : clientId,
		'type' : type,
		'character' : evt.which,
		'keycode' : evt.keyCode,
		'alt' : evt.altKey,
		'ctrl' : evt.ctrlKey,
		'shift' : evt.shiftKey,
		'meta' : evt.metaKey,
		'altgr' : evt.altGraphKey
	}
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

function start() {

	setInterval(mouseMoveEventFilter, 100);
	socket.on('message', function(data) {
		if (data.clazz == 'sk.viktor.ignored.model.s2c.JsonWindowRequest') {
			$("#" + data.windowId + "Window").dialog("close");
			delete nextRequest[data.windowId];
			delete busy[data.windowId];
			return;
		}

		if (!(data.windowInfo.id in data) || busy[data.windowInfo.id] != true) {
			processRequest(data);
		} else {
			nextRequest[data.windowInfo.id] = data;
		}
	});
	socket.on('connect', function() {
		socket.json.send({
			'@class' : 'sk.viktor.ignored.model.c2s.JsonConnectionHandshake',
			'clientId' : clientId
		});
	});

	bindEvent(document,'mousedown', function(evt) {
		++mouseDown;
	});
	bindEvent(document,'mouseup', function(evt) {
		--mouseDown;
	});
}