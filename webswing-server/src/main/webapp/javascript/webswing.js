define([ 'jquery', 'webswing-base', 'webswing-socket', 'webswing-files', 'webswing-dialog', 'webswing-selector', 'webswing-login', 'webswing-canvas',
		'webswing-identity', 'webswing-polyfill', 'webswing-jslink', 'webswing-clipboard' ], function($, base, socket, files, dialog, selector,
		login, canvas, identity, polyfill, jslink, clipboard) {
	"use strict";

	function initInstance(rootElement, options) {
		// fix for ie - resolving documentBase
		if (!window.location.origin) {
			window.location.origin = window.location.protocol + "//" + window.location.hostname
					+ (window.location.port ? ':' + window.location.port : '');
		}

		var extObject = {};

		var api = {
			extObject : extObject,
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
			debugPort : null,
			javaCallTimeout : 3000,
			documentBase : document.location.origin + document.location.pathname,
			ieVersion : detectIE(),
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
		jslink.init(api);
		clipboard.init(api);
		configure();

		if (api.autoStart) {
			api.start();
		} else {
			api.dialog.show(api.dialog.content.readyDialog);
		}

		function configure(options, appletParams) {
			options = options != null ? options : readOptions(api.rootElement);
			if (options != null) {
				api.autoStart = options.autoStart != null ? JSON.parse(options.autoStart) : api.autoStart;
				api.applicationName = options.applicationName != null ? options.applicationName : api.applicationName;
				api.args = options.args != null ? options.args : api.args;
				api.anonym = options.anonym != null ? JSON.parse(options.anonym) : api.anonym;
				api.binarySocket = options.binarySocket != null ? JSON.parse(options.binarySocket) : api.binarySocket;
				api.recording = options.recording != null ? JSON.parse(options.recording) : api.recording;
				api.clientId = options.clientId != null ? options.clientId : api.clientId;
				api.mirror = options.mirrorMode != null ? JSON.parse(options.mirrorMode) : api.mirror;
				api.connectionUrl = options.connectionUrl != null ? options.connectionUrl : api.connectionUrl;
				api.debugPort = options.debugPort != null ? options.debugPort : api.debugPort;
				api.javaCallTimeout = options.javaCallTimeout != null ? parseInt(options.javaCallTimeout, 10) : api.javaCallTimeout;
				if (api.connectionUrl.substr(api.connectionUrl.length - 1) !== '/') {
					api.connectionUrl = api.connectionUrl + '/';
				}
			}
			appletParams = appletParams != null ? appletParams : readAppletParams(api.rootElement);
			if (appletParams != null) {
				api.params = [];
				for ( var prop in appletParams) {
					if (typeof appletParams[prop] === 'string') {
						api.params.push({
							name : prop,
							value : appletParams[prop]
						});
					}
				}
			}
		}

		function setControl(value) {
			api.context.hasControl = value ? true : false;
		}

		extObject.start = api.start;
		extObject.disconnect = api.disconnect;
		extObject.configure = configure;
		extObject.kill = api.base.kill;
		extObject.setControl = setControl;
		return extObject;

	}

	function detectIE() {
		var ua = window.navigator.userAgent;

		var msie = ua.indexOf('MSIE ');
		if (msie > 0) {
			// IE 10 or older => return version number
			return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
		}

		var trident = ua.indexOf('Trident/');
		if (trident > 0) {
			// IE 11 => return version number
			var rv = ua.indexOf('rv:');
			return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
		}

		var edge = ua.indexOf('Edge/');
		if (edge > 0) {
			// IE 12 => return version number
			return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
		}
		// other browser
		return false;
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

	function readAppletParams(element) {
		var result = {}
		var params = $(element).find('webswing-param');
		for ( var i = 0; i < params.length; i++) {
			var name = params[i].getAttribute('name');
			var val = params[i].getAttribute('value');
			if (name != null) {
				result[name] = val;
			}
		}
		return result;
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
