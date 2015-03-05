define('webswing', [ 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas', 'webswing-identity' ], function(webswingBase, socket, files, dialog, selector, login, canvas, identity) {
	"use strict";

	var config = {
		send : function(message) {
			wsApi.socket.send(message);
		},
		onErrorMessage : function(text) {
			wsApi.dialog.show({
				content : 'Error: ' + text
			});
			wsApi.socket.dispose();
		},
		onContinueOldSession : function() {
			wsApi.ws.canPaint(false);
			wsApi.dialog.show(wsApi.dialog.content.continueOldSessionDialog);
		},
		onApplicationSelection : function(apps) {
			wsApi.dialog.hide();
			wsApi.selector.show(apps);
		},
		onApplicationShutdown : function() {
			wsApi.dialog.show(wsApi.dialog.content.stoppedDialog);
			wsApi.socket.dispose();
		},
		onBeforePaint : function() {
			wsApi.dialog.hide();
		},
		onLinkOpenAction : function(url) {
			wsApi.files.link(url);
		},
		onPrintAction : function(id) {
			swApi.files.print(encodeURIComponent('/file?id=' + id));
		},
		onFileDownloadAction : function(id) {
			wsApi.files.download('/file?id=' + id);
		},
		onFileDialogAction : function(data) {
			if (data.eventType === 'Open') {
				wsApi.files.open(data, ws.getClientId());
			} else if (data.eventType === 'Close') {
				wsApi.files.close();
			}
		},
		clientId : wsApi.identity.create(),
		hasControl : true,
		mirrorMode : false
	};
	var ws = webswingBase(config);

	var wsApi = {
		rootElement : $('#body'),
		autoStart : true,
		typedArraysSupported : true,
		start : function() {
			wsApi.login.login(function(){
				wsApi.canvas.create();
				wsApi.dialog.show(wsApi.dialog.content.initializingDialog);
				wsApi.socket.connect();
			});
		},
		newSession : function() {
			wsApi.ws.kill();
			wsApi.ws.dispose();
			wsApi.canvas.dispose();
			wsApi.identity.dispose();
			wsApi.socket.dispose();
			wsApi.start();
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
	socket.init(wsApi);
	dialog.init(wsApi);
	files.init(wsApi);
	selector.init(wsApi);
	login.init(wsApi);
	canvas.init(wsApi);
	identity.init(wsApi);
	wsApi.ws = ws;

	wsApi.start();

	window.webswing = {
		setTypedArraysSupported : function(supported) {
			wsApi.ws.setDirectDrawSupported(supported);
			wsApi.typedArraysSupported = supported;
		}
	};
});
