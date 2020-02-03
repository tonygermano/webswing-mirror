import Util from './webswing-util'
import Base from './webswing-base'
import Socket from './webswing-socket'
import Files from './webswing-files'
import Dialog from './webswing-dialog'
import Login from './webswing-login'
import Canvas from './webswing-canvas'
import Identity from './webswing-identity'
import JsLink from './webswing-jslink'
import Clipboard from './webswing-clipboard'
import Playback from './webswing-playback'
import Input from './webswing-input'
import Touch from './webswing-touch'
import Injector from './webswing-inject'
import Translate from './webswing-translate'
import Ping from './webswing-ping'

export default function Webswing(i18n) {
        "use strict";

    var util = Util(i18n);
        var globalName = $('[data-webswing-global-var]');
        var global = {};
        var typedArraysSupported = isArrayBufferSupported();
        
        if (!Element.prototype.matches) {
        	// fix for IE matches selector
        	Element.prototype.matches = Element.prototype.msMatchesSelector;
        }
        
        if (!Element.prototype.closest) {
        	Element.prototype.closest = function(s) {
        		var el = this;
        		do {
        			if (el.matches(s)) return el;
        			el = el.parentElement || el.parentNode;
        		} while (el !== null && el.nodeType === 1);
        		return null;
        	};
        }

        if (globalName != null && globalName.length !== 0) {
            var name = globalName.data('webswingGlobalVar');
            global = window[name] = {
                scan: scanForInstances,
                bootstrap: bootstrap,
                utils: util,
                $: $
            };
            scanForInstances(window);
        } else {
            scanForInstances(window);
        }

        function scanForInstances(root) {
            root = root != null ? root : global;
            var result = {};
            var instances = $('[data-webswing-instance]');
            instances.each(function (index, instance) {
                var id = $(instance).data('webswingInstance');
                var active = $(instance).data('webswingActive');
                if (!active) {
                    var customization;
                    if (root[id] != null && root[id].options != null) {
                        $(instance).data("webswingOptions", root[id].options);
                        if (root[id].options.customization != null && typeof root[id].options.customization === 'function') {
                            customization = root[id].options.customization;
                        }
                    }
                    $(instance).find("#loading").remove();
                    var wsInstance = bootstrap($(instance), customization);
                    $(instance).attr('data-webswing-active', 'true');
                    if (id != null) {
                        result[id] = wsInstance;
                    }
                }
            });
            for (var exportName in result) {
                if(root[exportName] !=null && root[exportName].disconnect !=null){
                    console.warn("Bootstrapping Webswing instance named '"+exportName+"'.Instance with this name has already been bootstrapped. Disconnecting old instance.");
                    root[exportName].disconnect();
                    delete root[exportName];
                }
                root[exportName] = $.extend(result[exportName], root[exportName]);
            }
        }

        function bootstrap(element, customization) {
            var injector = new Injector();
            injector.module('webswing', new WebswingInstance(element));
            injector.module('dialog', new Dialog());
        	injector.module('canvas', new Canvas(util));
       		injector.module('base', new Base(util));
        	injector.module('input', new Input(util));
        	injector.module('touch', new Touch(util));
            injector.module('socket', new Socket());
        	injector.module('files', new Files(util));
        	injector.module('login', new Login(util));
        	injector.module('identity', new Identity(util));
            injector.module('jslink', new JsLink());
        	injector.module('clipboard', new Clipboard(util));
            injector.module('playback', new Playback());
            injector.module('translate', new Translate());
            injector.module('ping', new Ping());
            var externalObj = {
                start: 'webswing.start',
                disconnect: 'webswing.disconnect',
                configure: 'webswing.configure',
                kill: 'base.kill',
                setControl: 'webswing.setControl',
                repaint: 'base.repaint',
                uuid: 'socket.uuid',
                requestComponentTree: 'base.requestComponentTree',
                getWindows: 'base.getWindows',
                getWindowById: 'base.getWindowById',
                performAction: 'base.performAction'
            };
            injector.module('external', {
                provides: externalObj,
                injects: externalObj
            });

            if (customization != null) {
                customization(injector);
            }
            injector.injectAndVerify();

            return externalObj;
        }

        function WebswingInstance(rootElement) {
            var module = this;
            var api;
            module.injects = api = {
                cfg: 'webswing.config',
                start: 'webswing.start',
                disconnect: 'webswing.disconnect',
                login: 'login.login',
                connect: 'socket.connect',
                showDialog: 'dialog.show',
                readyDialog: 'dialog.content.readyDialog',
                initializingDialog: 'dialog.content.initializingDialog',
                cookiesDisabledDialog: 'dialog.content.cookiesDisabledDialog',
                disposeIdentity: 'identity.dispose',
                disposeBase: 'base.dispose',
                disposeCanvas: 'canvas.dispose',
                disposeTouch: 'touch.dispose',
                disposeInput: 'input.dispose',
                disposeSocket: 'socket.dispose',
                disposeFileDialog: 'files.close',
                disposeCopyBar: 'clipboard.dispose',
                disposePing: 'ping.dispose',
                startPing: 'ping.start',
                showPlaybackControls: 'playback.showControls',
                externalApi: 'external'
            };
            module.provides = {
                config: defaultCtxConfig(),
                start: start,
                newSession: newSession,
                reTrySession: reTrySession,
                disconnect: disconnect,
                setControl: setControl,
                configure: configure
            };
            module.ready = function () {
                configure();
                if (typeof api.cfg.onReady ===  'function'){
                    api.login(function () {
                        api.cfg.onReady(api.externalApi);
                    });
                }
                if (api.cfg.autoStart) {
                    api.start();
                } else {
                    api.showDialog(api.readyDialog);
                }
            };

            function defaultCtxConfig() {
                return {
                    rootElementWrapper: setupRootElement(rootElement),
                    rootElement: setupRootElementContent(rootElement),
                    autoStart: false,
                    args: null,
                    connectionUrl: location.origin + location.pathname,
                    mirror: false,
                    recordingPlayback: false,
                    typedArraysSupported: typedArraysSupported,
                    binarySocket: true,
                    recording: false,
                    debugPort: null,
                    javaCallTimeout: 3000,
                    documentBase: document.location.origin + document.location.pathname,
                    ieVersion: util.detectIE(),
                    isMac: util.detectMac(),
                    /* webswing instance context */
                    clientId: null,
                    viewId: null,
                    hasControl: false,
                    mirrorMode: false,
                    canPaint: false,
                    virtualKB: false,
                    debugLog: false,
                    traceLog: false,
                    touchMode: false,
                    pingParams: {count : 6, interval : 5, maxLatency : 500, notifyIf : 3},
                    compositingWindowsListener: {
                    	windowOpening: function(htmlOrCanvasWindow) {},
                    	windowOpened: function(htmlOrCanvasWindow) {},
                    	windowClosing: function(htmlOrCanvasWindow) {},
                    	windowClosed: function(htmlOrCanvasWindow) {},
                    	windowModalBlockedChanged: function(htmlOrCanvasWindow) {}
                    },
                    hideTouchBar: false
                };
            }

            function setupRootElement(rootElement) {
                return rootElement.addClass('webswing-element');
            }
            
            function setupRootElementContent(rootElement) {
            	var detachedContent = rootElement.children().detach();
            	var wrapper = $("<div class='webswing-element-content' />");
            	rootElement.append(wrapper);
            	wrapper.append(detachedContent);
            	
            	return wrapper;
            }

            function start() {
                if(!util.checkCookie()){
                    api.showDialog(api.cookiesDisabledDialog)
                    return;
                }
                api.login(function () {
                    api.startPing();
                    api.showDialog(api.initializingDialog);
                    api.connect();
                });
            }

            function newSession() {
                api.disconnect();
                api.disposeIdentity();
                api.start();
            }

            function reTrySession() {
                api.disconnect();
                api.start();
            }

            function disconnect() {
                api.disposeFileDialog();
                api.disposeBase();
                api.disposeInput();
                api.disposeTouch();
                api.disposeCanvas();
                api.disposeSocket();
                api.disposeCopyBar();
                api.disposePing();
            }

            function configure(options, appletParams) {
                var cfg = api.cfg;
                options = options != null ? options : readOptions(cfg.rootElementWrapper);
                if (options != null) {
                    cfg.autoStart = options.autoStart != null ? JSON.parse(options.autoStart) : cfg.autoStart;
                    cfg.securityToken = options.securityToken != null ? options.securityToken : cfg.securityToken;
                    cfg.realm = options.realm != null ? options.realm : cfg.realm;
                    cfg.args = options.args != null ? options.args : cfg.args;
                    cfg.binarySocket = options.binarySocket != null ? JSON.parse(options.binarySocket) : cfg.binarySocket;
                    cfg.recording = options.recording != null ? JSON.parse(options.recording) : cfg.recording;
                    cfg.clientId = options.clientId != null ? options.clientId : cfg.clientId;
                    cfg.mirror = options.mirrorMode != null ? JSON.parse(options.mirrorMode) : cfg.mirror;
                    cfg.connectionUrl = options.connectionUrl != null ? options.connectionUrl : cfg.connectionUrl;
                    cfg.debugPort = options.debugPort != null ? options.debugPort : cfg.debugPort;
                    cfg.debugLog = options.debugLog != null ? JSON.parse(options.debugLog) : cfg.debugLog;
                    cfg.traceLog = options.traceLog != null ? JSON.parse(options.traceLog) : cfg.traceLog;
                    cfg.javaCallTimeout = options.javaCallTimeout != null ? parseInt(options.javaCallTimeout, 10) : cfg.javaCallTimeout;
                    cfg.pingParams = options.pingParams != null ? options.pingParams : cfg.pingParams;
                    if (cfg.connectionUrl.substr(cfg.connectionUrl.length - 1) !== '/') {
                        cfg.connectionUrl = cfg.connectionUrl + '/';
                    }
                    if (options.recordingPlayback != null) {
                        cfg.recordingPlayback = options.recordingPlayback;
                        api.showPlaybackControls();
                    }
                    cfg.onReady = typeof options.onReady === 'function' ? options.onReady : cfg.onReady;
                    cfg.compositingWindowsListener = typeof options.compositingWindowsListener === 'object' ? options.compositingWindowsListener : cfg.compositingWindowsListener;
                    cfg.hideTouchBar = options.hideTouchBar != null ? JSON.parse(options.hideTouchBar) : cfg.hideTouchBar;
                }else{
                    return $.extend(true, [], cfg);
                }
                appletParams = appletParams != null ? appletParams : readAppletParams(cfg.rootElementWrapper);
                if (appletParams != null) {
                    cfg.params = [];
                    for (var prop in appletParams) {
                        if (typeof appletParams[prop] === 'string') {
                            cfg.params.push({
                                name: prop,
                                value: appletParams[prop]
                            });
                        }
                    }
                }
            }

            function setControl(value) {
                api.cfg.hasControl = value ? true : false;
            }

            function readOptions(element) {
                var options = element.data("webswingOptions");
                return options;
            }

            function readAppletParams(element) {
                var result = {};
                var params = $(element).find('webswing-param');
                for (var i = 0; i < params.length; i++) {
                    var name = params[i].getAttribute('name');
                    var val = params[i].getAttribute('value');
                    if (name != null) {
                        result[name] = val;
                    }
                }
                return result;
            }
        }

	    function isArrayBufferSupported() {
	        if ('ArrayBuffer' in window && ArrayBuffer.toString().indexOf("[native code]") !== -1) {
	            return true;
	        }
	        return false;
	    }
}
