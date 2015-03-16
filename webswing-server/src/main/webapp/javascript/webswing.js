define([ 'jquery', 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas',
		'webswing-identity', 'webswing-polyfill' ], function($, base, socket, files, dialog, selector, login, canvas, identity, polyfill) {
	"use strict";

	function initInstance(rootElement, options) {
		var api = {
			rootElement : rootElement,
			autoStart : options.autoStart != null ? options.autoStart : true,
			applicationName : options.applicationName,
			connectionUrl : options.connectionUrl != null ? options.connectionUrl : document.location.toString(),
			typedArraysSupported : false,
			binarySocket : false,// not working yet
			start : function() {
				api.login.login(function() {
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

		dialog.init(api);
		base.init(api);
		socket.init(api);
		files.init(api);
		selector.init(api);
		login.init(api);
		canvas.init(api);
		identity.init(api);
		polyfill.init(api);

		if (options.customization != null && typeof options.customization === 'function') {
			options.customization(api);
		}

		if (api.autoStart) {
			api.start();
		}

		return {
			start : api.start
		};

	}

	var result = {};

	var instances = $('[data-webswing-id]');
	instances.each(function(index, instance) {
		var id = $(instance).data('webswingId');
		var options = $(instance).data('webswingOptions');
		if (typeof options !== 'object') {
			try{
				options= eval("(function(){return " + options + ";})()");
			}catch(e){
				throw Error("Configuration of webswing instance '" + id + "' is not a valid json object. " + options);
			}
		}
		var wsInstance = initInstance($(instance), options);
		if (options.exportName != null) {
			result[options.exportName] = wsInstance;
		}
	});

	return result;
});
