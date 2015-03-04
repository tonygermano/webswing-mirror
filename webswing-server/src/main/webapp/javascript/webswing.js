define('webswing', [ 'jquery', 'webswing-base', 'webswing-upload', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'atmosphere',
		'ProtoBuf', 'bootstrap' ], function($, webswingBase, upload, dialog, selector, login, atmosphere, ProtoBuf) {
	"use strict";

	var config = {
		send : function(message) {
			if (socket != null && socket.request.isOpen) {
				if (typeof message == "string") {
					socket.push(message);
				}
				if (typeof message === "object") {
					socket.push(atmosphere.util.stringifyJSON(message));
				}
			}
		},
		onErrorMessage : function(text) {
			wsApi.dialog.show({
				content : 'Error' + text
			});
			atmosphere.unsubscribe();
		},
		onContinueOldSession : function() {
			ws.canPaint(false);
			wsApi.dialog.show(wsApi.dialog.content.continueOldSessionDialog);
		},
		onApplicationSelection : function(apps) {
			wsApi.dialog.hide();
			wsApi.selector.show(apps);
		},
		onApplicationShutdown : function() {
			wsApi.dialog.show(wsApi.dialog.content.stoppedDialog);
			atmosphere.unsubscribe();
		},
		onBeforePaint : function() {
			wsApi.dialog.hide();
		},
		onLinkOpenAction : function(url) {
			window.open(url, '_blank');
		},
		onPrintAction : function(url) {
			window.open('/print/viewer.html?file=' + encodeURIComponent('/file?id=' + url), '_blank');
		},
		onFileDownloadAction : function(url) {
			downloadURL('/file?id=' + url);
		},
		onFileDialogAction : function(data) {
			if (data.eventType === 'Open') {
				wsApi.upload.open(data, ws.getClientId());
			} else if (data.eventType === 'Close') {
				wsApi.upload.close();
			}
		},
		clientId : setupClientID(),
		hasControl : true,
		mirrorMode : false
	};
	var ws = webswingBase(config);

	var wsApi = {
		rootElement : $('#body'),
		autoStart : true,
		start : function() {
			createCanvas();
			wsApi.dialog.show(wsApi.dialog.content.initializingDialog);
			connect();
		},
		newSession : function() {
			ws.kill();
			eraseCookie('webswingID');
			location.reload();
		},
		continueSession : function() {
			wsApi.dialog.hide();
			ws.canPaint(true);
			ws.handshake();
			ws.repaint();
			ws.ack();
		},
		startApplication : function(name) {
			ws.setClientId(ws.getUser() + ws.getClientId() + name);
			ws.setApplication(name);
			ws.canPaint(true);
			ws.handshake();
			wsApi.dialog.show(wsApi.dialog.content.startingDialog);
		}
	};
	dialog.init(wsApi);
	upload.init(wsApi);
	selector.init(wsApi);
	login.init(wsApi);
	wsApi.ws = ws;

	wsApi.login.login();

	var proto = ProtoBuf.loadProtoFile("/webswing.proto");
	var typedArraysSupported = false;
	var canvas = null;
	var socket = null;

	function connect() {
		var request = {
			url : document.location.toString() + 'async/swing',
			contentType : "application/json",
			logLevel : 'debug',
			transport : 'websocket',
			trackMessageLength : true,
			reconnectInterval : 5000,
			fallbackTransport : 'long-polling'
		};

		if (typedArraysSupported) {
			request.headers = {
				'X-Atmosphere-Binary' : true
			};
			request.enableProtocol = false;
			request.trackMessageLength = false;
			request.contentType = 'application/octet-stream';
			request.webSocketBinaryType = 'arraybuffer';
		}

		request.onOpen = function(response) {
			ws.setUuid(response.request.uuid);
		};

		request.onReopen = function(response) {
			wsApi.dialog.hide();
		};

		request.onMessage = function(response) {
			var message = response.responseBody;
			try {
				var data = atmosphere.util.parseJSON(message);
				ws.processJsonMessage(data);
			} catch (e) {
				console.error(e);
				// ws.processTxtMessage(response.responseBody);
				return;
			}
		};

		request.onClose = function(response) {
			if (wsApi.dialog.current() !== wsApi.dialog.content.stoppedDialog) {
				wsApi.dialog.show(wsApi.dialog.content.disconnectedDialog);
			}
		};

		request.onError = function(response) {
			// TODO:handle
		};

		request.onReconnect = function(request, response) {
			wsApi.dialog.show(wsApi.dialog.content.initializingDialog);
		};

		socket = atmosphere.subscribe(request);
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

	function createCanvas() {
		$("#body").append('<canvas id="canvas" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
		canvas = document.getElementById("canvas");
		ws.setCanvas(canvas);
		window.onresize = function() {
			canvas.width = width();
			canvas.height = height();
			ws.resizedWindow();
		};
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

	function width() {
		return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 0;
	}

	function height() {
		return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 0;
	}

	window.webswing = {
		setTypedArraysSupported : function(supported) {
			ws.setDirectDrawSupported(supported);
			typedArraysSupported = supported;
		}
	};
});
