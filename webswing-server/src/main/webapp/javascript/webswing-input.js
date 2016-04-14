define([ 'jquery', 'webswing-util' ], function amdFactory($, util) {
    "use strict";

    return function InputModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg : 'webswing.config',
            send : 'socket.send',
            getInput : 'canvas.getInput',
            getCanvas : 'canvas.get',
            cut : 'clipboard.cut',
            copy : 'clipboard.copy',
            paste : 'clipboard.paste',
        };
        module.provides = {
            register : register,
            sendInput : sendInput,
            dispose : dispose
        };

        module.ready = function() {
        };

        var latestMouseMoveEvent = null;
        var latestMouseWheelEvent = null;
        var latestWindowResizeEvent = null;
        var mouseDown = 0;
        var inputEvtQueue = [];

        function sendInput(message) {
            enqueueInputEvent();
            var evts = inputEvtQueue;
            inputEvtQueue = [];
            if (message != null) {
                evts.push(message);
            }
            if (evts.length > 0) {
                api.send({
                    events : evts
                });
            }
        }

        function enqueueInputEvent(message) {
            if (api.cfg.hasControl) {
                if (latestMouseMoveEvent != null) {
                    inputEvtQueue.push(latestMouseMoveEvent);
                    latestMouseMoveEvent = null;
                }
                if (latestMouseWheelEvent != null) {
                    inputEvtQueue.push(latestMouseWheelEvent);
                    latestMouseWheelEvent = null;
                }
                if (latestWindowResizeEvent != null) {
                    inputEvtQueue.push(latestWindowResizeEvent);
                    latestWindowResizeEvent = null;
                }
                if (message != null) {
                    if (JSON.stringify(inputEvtQueue[inputEvtQueue.length - 1]) !== JSON.stringify(message)) {
                        inputEvtQueue.push(message);
                    }
                }
            }
        }

        function resetInput() {
            latestMouseMoveEvent = null;
            latestMouseWheelEvent = null;
            latestWindowResizeEvent = null;
            mouseDown = 0;
            inputEvtQueue = [];
        }

        function dispose() {
            document.removeEventListener('mousedown', mouseDownEventHandler);
            document.removeEventListener('mouseout', mouseOutEventHandler);
            document.removeEventListener('mouseup', mouseUpEventHandler);
        }

        function register() {
            var canvas = api.getCanvas();
            var input = api.getInput();
            resetInput();
            focusInput(input);
            util.bindEvent(canvas, 'mousedown', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousedown');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            util.bindEvent(canvas, 'dblclick', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'dblclick');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            util.bindEvent(canvas, 'mousemove', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousemove');
                mousePos.mouse.button = mouseDown;
                latestMouseMoveEvent = mousePos;
                return false;
            }, false);
            util.bindEvent(canvas, 'mouseup', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mouseup');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            // IE9, Chrome, Safari, Opera
            util.bindEvent(canvas, "mousewheel", function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousewheel');
                latestMouseMoveEvent = null;
                if (latestMouseWheelEvent != null) {
                    mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
                }
                latestMouseWheelEvent = mousePos;
                return false;
            }, false);
            // firefox
            util.bindEvent(canvas, "DOMMouseScroll", function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousewheel');
                latestMouseMoveEvent = null;
                if (latestMouseWheelEvent != null) {
                    mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
                }
                latestMouseWheelEvent = mousePos;
                return false;
            }, false);
            util.bindEvent(canvas, 'contextmenu', function(event) {
                event.preventDefault();
                event.stopPropagation();
                return false;
            });

            util.bindEvent(input, 'keydown', function(event) {
                // 48-57
                // 65-90
                // 186-192
                // 219-222
                // 226
                // FF (163, 171, 173, ) -> en layout ]\/ keys
                var kc = event.keyCode;
                if (!((kc >= 48 && kc <= 57) || (kc >= 65 && kc <= 90) || (kc >= 186 && kc <= 192) || (kc >= 219 && kc <= 222) || (kc == 226)
                        || (kc == 0) || (kc == 163) || (kc == 171) || (kc == 173) || (kc >= 96 && kc <= 111) || (kc == 59) || (kc == 61))) {
                    if(!api.cfg.virtualKB){
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }
                var keyevt = getKBKey('keydown', canvas, event);
                // hanle paste event
                if (!(keyevt.key.ctrl && (keyevt.key.character == 88 || keyevt.key.character == 67 || keyevt.key.character == 86))) { // cut copy
                    // default action prevented
                    if (keyevt.key.ctrl && !keyevt.key.alt && !keyevt.key.altgr) {
                        event.preventDefault();
                    }
                    enqueueInputEvent(keyevt);
                }
                return false;
            }, false);
            util.bindEvent(input, 'keypress', function(event) {
                var keyevt = getKBKey('keypress', canvas, event);
                if (!(keyevt.key.ctrl && (keyevt.key.character == 120 || keyevt.key.character == 24 || keyevt.key.character == 99
                        || keyevt.key.character == 118 || keyevt.key.character == 22))) { // cut copy paste handled separately
                    event.preventDefault();
                    event.stopPropagation();
                    enqueueInputEvent(keyevt);
                }
                return false;
            }, false);
            util.bindEvent(input, 'keyup', function(event) {
                var keyevt = getKBKey('keyup', canvas, event);
                if (!(keyevt.key.ctrl && (keyevt.key.character == 88 || keyevt.key.character == 67 || keyevt.key.character == 86))) { // cut copy
                    event.preventDefault();
                    event.stopPropagation();
                    enqueueInputEvent(keyevt);
                    sendInput();
                }
                return false;
            }, false);

            util.bindEvent(input, 'cut', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.cut(event);
                return false;
            }, false);
            util.bindEvent(input, 'copy', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.copy(event);
                return false;
            }, false);
            util.bindEvent(input, 'paste', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.paste(event);
                return false;
            }, false);
            util.bindEvent(document, 'mousedown', mouseDownEventHandler);
            util.bindEvent(document, 'mouseout', mouseOutEventHandler);
            util.bindEvent(document, 'mouseup', mouseUpEventHandler);
        }

        function mouseDownEventHandler(evt) {
            if (evt.which == 1) {
                mouseDown = 1;
            }
        }

        function mouseOutEventHandler(evt) {
            mouseDown = 0;
        }

        function mouseUpEventHandler(evt) {
            if (evt.which == 1) {
                mouseDown = 0;
            }
        }

        function focusInput(input) {
            // In order to ensure that the browser will fire clipboard events, we always need to have something selected
            input.value = ' ';
            input.focus();
            input.select();
        }

        function getMousePos(canvas, evt, type) {
            var rect = canvas.getBoundingClientRect();
            // return relative mouse position
            var mouseX = Math.round(evt.clientX - rect.left);
            var mouseY = Math.round(evt.clientY - rect.top);
            var delta = 0;
            if (type == 'mousewheel') {
                delta = -Math.max(-1, Math.min(1, (evt.wheelDelta || -evt.detail)));
            }
            return {
                mouse : {
                    x : mouseX,
                    y : mouseY,
                    type : type,
                    wheelDelta : delta,
                    button : evt.which,
                    ctrl : evt.ctrlKey,
                    alt : evt.altKey,
                    shift : evt.shiftKey,
                    meta : evt.metaKey
                }
            };
        }

        function getKBKey(type, canvas, evt) {
            var char = evt.which;
            if (char == 0 && evt.key != null) {
                char = evt.key.charCodeAt(0);
            }
            var kk = evt.keyCode;
            if (kk == 0) {
                kk = char;
            }
            return {
                key : {
                    type : type,
                    character : char,
                    keycode : kk,
                    alt : evt.altKey,
                    ctrl : evt.ctrlKey,
                    shift : evt.shiftKey,
                    meta : evt.metaKey
                }
            };
        }

    };

});