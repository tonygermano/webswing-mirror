define(['webswing-util'], function amdFactory(util) {
    "use strict";
    return function CanvasModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            sendHandshake: 'base.handshake'
        };
        module.provides = {
            dispose: dispose,
            get: get,
            getInput: getInput
        };

        var canvas;
        var inputHandler;
        var resizeCheck;

        function create() {
            if (canvas == null) {
                var dpr = util.dpr;
                api.cfg.rootElement.append('<canvas data-id="canvas" style="display:block; width:' + width() + 'px;height:' + height() + 'px;" width="' + width() * dpr + '" height="' + height() * dpr + '" tabindex="-1"/>');
                api.cfg.rootElement.append('<input data-id="input-handler" class="ws-input-hidden" type="text" value="" />');
                canvas = api.cfg.rootElement.find('canvas[data-id="canvas"]');
                //canvas[0].getContext("2d").scale(dpr, dpr);
                inputHandler = api.cfg.rootElement.find('input[data-id="input-handler"]');
            }
            if (resizeCheck == null) {
                resizeCheck = setInterval(function () {
                    if (!api.cfg.mirror && (canvas.width() !== width() || canvas.height() !== height())) {
                        var snapshot = get().getContext("2d").getImageData(0, 0, get().width, get().height);
                        var w=width();
                        var h= height();
                        get().width = w * dpr;
                        get().height = h * dpr;
                        get().style.width = w + 'px';
                        get().style.height = h + 'px';
                        get().getContext("2d").putImageData(snapshot, 0, 0);
                        api.sendHandshake();
                    }
                }, 500);
            }
        }

        function dispose() {
            if (canvas != null) {
                canvas.remove();
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
            return api.cfg.rootElement.width();
        }

        function height() {
            return api.cfg.rootElement.height();
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
    };

});