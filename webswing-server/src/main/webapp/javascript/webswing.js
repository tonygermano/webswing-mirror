define([ 'jquery', 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas',
		'webswing-identity', 'webswing-polyfill' ], function($, base, socket, files, dialog, selector, login, canvas, identity, polyfill) {
	"use strict";

	function initInstance(rootElement, options) {
		var api = {
			rootElement : rootElement,
			autoStart : true,
			applicationName : null,
			connectionUrl : document.location.toString(),
			typedArraysSupported : false,
			binarySocket : false,// not working yet
			start : function() {
				api.login.login(function() {
					api.dialog.show(api.dialog.content.initializingDialog);
					api.socket.connect();
					if(api.context.mirrorMode){
						api.base.startMirrorView();
					}
				});
			},
			newSession : function() {
				api.disconnect();
				api.identity.dispose();
				api.start();
			},
			disconnect : function() {
				api.base.dispose();
				api.canvas.dispose();
				api.socket.dispose();
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
		configure();

		if (options.customization != null && typeof options.customization === 'function') {
			options.customization(api);
		}

		if (api.autoStart) {
			api.start();
		} else {
			api.dialog.show(api.dialog.content.readyDialog);
		}

		function configure() {
			var options = readOptions(api.rootElement);
			api.autoStart = options.autoStart != null ? options.autoStart : api.autoStart;
			api.applicationName = options.applicationName != null ? options.applicationName : api.applicationName;
			api.connectionUrl = options.connectionUrl != null ? options.connectionUrl : api.connectionUrl;
			if(api.connectionUrl.substr(api.connectionUrl.length - 1)!=='/'){
				api.connectionUrl=api.connectionUrl+'/';
			}
			api.context.hasControl = options.control != null ? options.control : api.context.hasControl;
			api.context.clientId = options.clientId != null ? options.clientId : api.context.clientId;
			api.context.mirrorMode = options.mirrorMode != null ? options.mirrorMode : api.context.mirrorMode;
		}

		return {
			start : api.start,
			disconnect : api.disconnect,
			configure : configure,
			kill : api.base.kill
		};

	}

	function readOptions(element) {
		var options = element.data('webswingOptions');
		if (typeof options !== 'object') {
			try {
				options = eval("(function(){return " + options + ";})()");
			} catch (e) {
				throw Error("Configuration of webswing instance is not a valid json object. " + options);
			}
		}
		return options;
	}

	function scanForInstances() {
		var result = {};
		var instances = $('[data-webswing-instance]');
		instances.each(function(index, instance) {
			var id = $(instance).data('webswingInstance');
			var api = $(instance).data('webswingInstanceApi');
			if (api == null) {
				var options = readOptions($(instance));
				var wsInstance = initInstance($(instance), options);
				$(instance).data('webswingInstanceApi', wsInstance);
				if (id != null) {
					result[id] = wsInstance;
				}
			}
		});
		return result;
	}

	var result = scanForInstances();

	var global = $('[data-webswing-global-var]')
	if (global != null && global.length != 0) {
		var name = global.data('webswingGlobalVar');
		result[name] = {
			scan : scanForInstances
		}
	}

	return result;
});
