    "use strict";
    export default function Canvas(util) {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            sendHandshake: 'base.handshake',
            repaint: 'base.repaint'
        };
        module.provides = {
        	init: init,
            dispose: dispose,
            get: get,
            width: width,
            height: height,
            getDesktopSize: getDesktopSize,
            processComponentTree: processComponentTree
        };
        module.ready = function () {
        	init();
        };

        var canvas;
        var inputHandler;
        var resizeCheck;
        var lastRootWidth = 0;
        var lastRootHeight = 0;
        var touchWidth = 0;
        var touchHeight = 0;

        function init() {
            if (canvas == null) {
                var dpr = util.dpr;
                if (api.cfg.rootElement.parent().data("touch-width")) {
                	touchWidth = api.cfg.rootElement.parent().data("touch-width");
                }
                if (api.cfg.rootElement.parent().data("touch-height")) {
                	touchHeight = api.cfg.rootElement.parent().data("touch-height");
                }
                api.cfg.rootElement.append('<canvas role="presentation" aria-hidden="true" data-id="canvas" style="display:block; width:' + width() + 'px;height:' + height() + 'px;" width="' + width() * dpr + '" height="' + height() * dpr + '" tabindex="-1"/>');
                api.cfg.rootElement.append('<input role="presentation" aria-hidden="true" data-id="input-handler" class="ws-input-hidden" type="text" autocorrect="off" autocapitalize="none" autocomplete="off" value="" />');
                canvas = api.cfg.rootElement.find('canvas[data-id="canvas"]');
                canvas.addClass("webswing-canvas");
                //canvas[0].getContext("2d").scale(dpr, dpr);
                inputHandler = api.cfg.rootElement.find('input[data-id="input-handler"]');
                lastRootWidth = width();
                lastRootHeight = height();
                api.cfg.rootElement.attr("role", "application");
            }
            if (resizeCheck == null) {
                resizeCheck = setInterval(function () {
                    if (!api.cfg.mirror && !api.cfg.touchMode && canvas != null && (Math.round(canvas.width()) !== width() || Math.round(canvas.height()) !== height())) {
                    	if (api.cfg.rootElement.is(".composition")) {
                    		// when using compositing window manager, the root canvas has 0 size
                    		// we need to do a handshake only if the root element has changed size
                    		if (lastRootWidth !== width() || lastRootHeight !== height()) {
                    			lastRootWidth = width();
                    			lastRootHeight = height();
                    			api.sendHandshake();
                    		}
                    	} else {
                            var snapshot =null
                            if(get().width!==0 && get().height !== 0){
                                snapshot = get().getContext("2d").getImageData(0, 0, get().width, get().height);
                            }
                    		var w = width();
                    		var h = height();
                    		get().width = w * dpr;
                    		get().height = h * dpr;
                    		get().style.width = w + 'px';
                    		get().style.height = h + 'px';
                    		if(snapshot!=null){
                                get().getContext("2d").putImageData(snapshot, 0, 0);
                            }
                    		api.sendHandshake();
                    	}
                    }
                }, 500);
            }
        }

        function dispose() {
            if (canvas != null) {
            	api.cfg.rootElement.find('canvas').remove();
            	$(".webswing-canvas, .webswing-html-canvas").remove();
                inputHandler.remove();
                canvas = null;
                inputHandler = null;
            }
            if (resizeCheck != null) {
                clearInterval(resizeCheck);
                resizeCheck = null;
            }
        }

        function width() {
        	if (api.cfg.touchMode && touchWidth != 0) {
        		return touchWidth;
        	}
            return Math.floor(api.cfg.rootElement.width());
        }

        function height() {
        	var offset = 0;
        	
        	if (api.cfg.touchMode && touchHeight != 0) {
        		var touchBar = api.cfg.rootElement.parent().find('div[data-id="touchBar"]');
        		if (touchBar.length) {
        			offset += touchBar.height();
        		}
        		
        		return touchHeight - offset;
        	}
        	
            return Math.floor(api.cfg.rootElement.height());
        }
        
        function get() {
            if (canvas == null || resizeCheck != null) {
                init();
            }
            return canvas[0];
        }
        
        function processComponentTree(componentTree) {
        	// not implemented, to be customized
        }
        
        function getDesktopSize() {
        	if (api.cfg.rootElement.is(".composition")) {
        		return {width: width(), height: height()};
        	}
        	if (api.cfg.touchMode && touchWidth != 0 && touchHeight != 0) {
        		return {width: width(), height: height()};
        	}
        	return {width: get().offsetWidth, height: get().offsetHeight};
        }
        
    }
