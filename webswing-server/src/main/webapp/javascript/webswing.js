define([ 'jquery', 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas',
		'webswing-identity', 'webswing-polyfill' ], function($, base, socket, files, dialog, selector, login, canvas, identity, polyfill) {
	"use strict";

	function initInstance(rootElement, options) {
		var api = {
			rootElement : rootElement,
			autoStart : false,
			applicationName : null,
			args : null,
			anonym : false,
			connectionUrl : location.origin + location.pathname,
			mirror : false,
			typedArraysSupported : false,
			binarySocket : true,
			recording : false,
			start : function(customization) {
				if (customization != null) {
					customization(api);
				}
				api.login.login(function() {
					api.dialog.show(api.dialog.content.initializingDialog);
					api.socket.connect();
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

		if (api.autoStart) {
			api.start();
		} else {
			api.dialog.show(api.dialog.content.readyDialog);
		}

		function configure(options) {
			options = options != null ? options : readOptions(api.rootElement);
			if (options != null) {
				api.autoStart = options.autoStart != null ? JSON.parse(options.autoStart) : api.autoStart;
				api.applicationName = options.applicationName != null ? options.applicationName : api.applicationName;
				api.args = options.args != null ? options.args : api.args;
				api.anonym = options.anonym != null ? JSON.parse(options.anonym) : api.anonym;
				api.binarySocket = options.binarySocket != null ? JSON.parse(options.binarySocket) : api.binarySocket;
				api.recording = options.recording != null ? JSON.parse(options.recording) : api.recording ;
				api.clientId = options.clientId != null ? options.clientId : api.clientId;
				api.mirror = options.mirrorMode != null ? JSON.parse(options.mirrorMode) : api.mirror;
				api.connectionUrl = options.connectionUrl != null ? options.connectionUrl : api.connectionUrl;
				if (api.connectionUrl.substr(api.connectionUrl.length - 1) !== '/') {
					api.connectionUrl = api.connectionUrl + '/';
				}
			}
		}

		function setControl(value) {
			if (value) {
				api.context.hasControl = true;
			} else {
				api.context.hasControl = false;
			}
		}

		return {
			start : api.start,
			disconnect : api.disconnect,
			configure : configure,
			kill : api.base.kill,
			setControl : setControl
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

	function scanForInstances(root) {
		root = root != null ? root : global;
		var result = {};
		var instances = $('[data-webswing-instance]');
		instances.each(function(index, instance) {
			var id = $(instance).data('webswingInstance');
			var active = $(instance).data('webswingActive');
			if (!active) {
				var wsInstance = initInstance($(instance));
				$(instance).attr('data-webswing-active', 'true');
				if (id != null) {
					result[id] = wsInstance;
				}
			}
		});
		for ( var exportName in result) {
			root[exportName] = result[exportName];
		}
	}

	// fix for ie
	if (!window.location.origin) {
		window.location.origin = window.location.protocol + "//" + window.location.hostname
				+ (window.location.port ? ':' + window.location.port : '');
	}

	var globalName = $('[data-webswing-global-var]');
	var global = {};
	if (globalName != null && globalName.length != 0) {
		var name = globalName.data('webswingGlobalVar');
		global = window[name] = {
			scan : scanForInstances
		};
		scanForInstances(window[name]);
	} else {
		scanForInstances(window);
	}
});
