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
            dispose: dispose,
            get: get,
            getInput: getInput,
            focusInput: focusInput,
            getDesktopSize: getDesktopSize,
            processComponentTree: processComponentTree
        };

        var canvas;
        var inputHandler;
        var resizeCheck;
        var lastRootWidth = 0;
        var lastRootHeight = 0;

        function create() {
            if (canvas == null) {
                var dpr = util.dpr;
                api.cfg.rootElement.append('<canvas data-id="canvas" style="display:block; width:' + width() + 'px;height:' + height() + 'px;" width="' + width() * dpr + '" height="' + height() * dpr + '" tabindex="-1"/>');
                api.cfg.rootElement.append('<input data-id="input-handler" class="ws-input-hidden" type="text" autocorrect="off" autocapitalize="none" autocomplete="off" value="" />');
                canvas = api.cfg.rootElement.find('canvas[data-id="canvas"]');
                canvas.addClass("webswing-canvas");
                //canvas[0].getContext("2d").scale(dpr, dpr);
                inputHandler = api.cfg.rootElement.find('input[data-id="input-handler"]');
                lastRootWidth = width();
                lastRootHeight = height();
            }
            if (resizeCheck == null) {
                resizeCheck = setInterval(function () {
                    if (!api.cfg.mirror && !api.cfg.touchMode && canvas != null && (Math.floor(canvas.width()) !== width() || Math.floor(canvas.height()) !== height())) {
                    	if (api.cfg.rootElement.is(".composition")) {
                    		// when using compositing window manager, the root canvas has 0 size
                    		// we need to do a handshake only if the root element has changed size
                    		if (lastRootWidth !== width() || lastRootHeight !== height()) {
                    			lastRootWidth = width();
                    			lastRootHeight = height();
                    			api.sendHandshake();
                    		}
                    	} else {
                    		var snapshot = get().getContext("2d").getImageData(0, 0, get().width, get().height);
                    		var w = width();
                    		var h = height();
                    		get().width = w * dpr;
                    		get().height = h * dpr;
                    		get().style.width = w + 'px';
                    		get().style.height = h + 'px';
                    		get().getContext("2d").putImageData(snapshot, 0, 0);
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
            return Math.floor(api.cfg.rootElement.width());
        }

        function height() {
            return Math.floor(api.cfg.rootElement.height());
        }
        
        function get() {
            if (canvas == null || resizeCheck != null) {
                create();
            }
            return canvas[0];
        }
        
        function getInput() {
            if (inputHandler == null) {
                create();
            }
            return inputHandler[0];
        }
        
        function focusInput() {
        	var input = getInput();
        	input.focus({preventScroll: true});
        }
        
        function processComponentTree(componentTree) {
        	// not implemented, to be customized
        }
        
        function getDesktopSize() {
        	if (api.cfg.rootElement.is(".composition")) {
        		return {width: width(), height: height()};
        	}
        	return {width: get().offsetWidth, height: get().offsetHeight};
        }
        
    }