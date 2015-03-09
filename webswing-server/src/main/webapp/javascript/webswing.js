define('webswing', [ 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas', 'webswing-identity' ], function(base, socket, files, dialog, selector, login, canvas, identity) {
	"use strict";

	var api = {
		rootElement : $('#body'),
		autoStart : true,
		typedArraysSupported : true,
		start : function() {
			api.login.login(function(){
				api.dialog.show(api.dialog.content.initializingDialog);
				api.socket.connect();
			});
		},
		newSession : function() {
			api.base.kill();
			api.base.dispose();
			api.canvas.dispose();
			api.identity.dispose();
			api.socket.dispose();
			api.start();
		}
	};

	base.init(api);
	socket.init(api);
	dialog.init(api);
	files.init(api);
	selector.init(api);
	login.init(api);
	canvas.init(api);
	identity.init(api);

	api.start();
});
