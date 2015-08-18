define([ 'webswing-dd' ], function amdFactory(WebswingDirectDraw) {
    "use strict";
    return function BaseModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg : 'webswing.config',
            disconnect:'webswing.disconnect',
            send : 'socket.send',
            getSocketId : 'socket.uuid',
            getCanvas : 'canvas.get',
            getInput : 'canvas.getInput',
            getUser : 'login.user',
            getIdentity : 'identity.get',
            showDialog : 'dialog.show',
            hideDialog : 'dialog.hide',
            startingDialog : 'dialog.content.startingDialog',
            stoppedDialog : 'dialog.content.stoppedDialog',
            applicationAlreadyRunning : 'dialog.content.applicationAlreadyRunning',
            tooManyClientsNotification : 'dialog.content.tooManyClientsNotification',
            continueOldSessionDialog : 'dialog.content.continueOldSessionDialog',
            showSelector : 'selector.show',
            openFileDialog : 'files.open',
            closeFileDialog : 'files.close',
            openLink : 'files.link',
            print : 'files.print',
            download : 'files.download',
            cut : 'clipboard.cut',
            copy : 'clipboard.copy',
            paste : 'clipboard.paste',
            displayCopyBar: 'clipboard.displayCopyBar',
            processJsLink : 'jslink.process'
        };
        module.provides = {
            startApplication : startApplication,
            startMirrorView : startMirrorView,
            continueSession : continueSession,
            kill : kill,
            handshake : handshake,
            processMessage : processMessage,
            dispose : dispose
        };

        var timer1, timer2;
        var latestMouseMoveEvent = null;
        var latestMouseWheelEvent = null;
        var latestWindowResizeEvent = null;
        var mouseDown = 0;
        var inputEvtQueue = [];

        var windowImageHolders = {};
        var directDraw = new WebswingDirectDraw({});

        function startApplication(name, applet) {
            registerEventListeners(api.getCanvas(), api.getInput());
            resetState();
            api.cfg.clientId = api.getUser() + api.getIdentity() + name;
            api.cfg.appName = name;
            api.cfg.hasControl = true;
            api.cfg.mirrorMode = false;
            api.cfg.canPaint = true;
            api.cfg.applet = applet;
            handshake();
            api.showDialog(api.startingDialog);
        }

        function startMirrorView(clientId, appName) {
            registerEventListeners(api.getCanvas(), api.getInput());
            resetState();
            api.cfg.clientId = clientId;
            api.cfg.appName = appName;
            api.cfg.hasControl = false;
            api.cfg.mirrorMode = true;
            api.cfg.canPaint = true;
            handshake();
            repaint();
            api.showDialog(api.startingDialog);
        }

        function continueSession() {
            api.hideDialog();
            api.cfg.canPaint = true;
            handshake();
            repaint();
            ack();
        }

        function resetState() {
            api.cfg.clientId = '';
            api.cfg.appName = null;
            api.cfg.hasControl = false;
            api.cfg.mirrorMode = false;
            api.cfg.canPaint = false;
            clearInterval(timer1);
            clearInterval(timer2);
            timer1 = setInterval(sendInput, 100);
            timer2 = setInterval(heartbeat, 10000);
            latestMouseMoveEvent = null;
            latestMouseWheelEvent = null;
            latestWindowResizeEvent = null;
            mouseDown = 0;
            inputEvtQueue = [];
            windowImageHolders = {};
            directDraw = new WebswingDirectDraw({});
        }

        function sendInput() {
            enqueueInputEvent();
            if (inputEvtQueue.length > 0) {
                api.send({
                    events : inputEvtQueue
                });
                inputEvtQueue = [];
            }
        }

        function enqueueMessageEvent(message) {
            inputEvtQueue.push(getMessageEvent(message));
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

        function heartbeat() {
            enqueueMessageEvent('hb');
        }

        function repaint() {
            enqueueMessageEvent('repaint');
        }

        function ack() {
            enqueueMessageEvent('paintAck');
            sendInput();
        }

        function kill() {
            enqueueMessageEvent('killSwing');
        }

        function unload() {
            enqueueMessageEvent('unload');
        }

        function handshake() {
            inputEvtQueue.push(getHandShake(api.getCanvas()));
        }

        function dispose() {
            clearInterval(timer1);
            clearInterval(timer2);
            unload();
            sendInput();
            resetState();
            document.removeEventListener('mousedown', mouseDownEventHandler);
            document.removeEventListener('mouseout', mouseOutEventHandler);
            document.removeEventListener('mouseup', mouseUpEventHandler);
            window.removeEventListener('beforeunload', beforeUnloadEventHandler);
        }

        function processMessage(data) {
            if (data.applications != null && data.applications.length != 0) {
                api.showSelector(data.applications);
            }
            if (data.event != null) {
                if (data.event == "shutDownNotification") {
                    api.showDialog(api.stoppedDialog);
                    api.disconnect();
                } else if (data.event == "applicationAlreadyRunning") {
                    api.showDialog(api.applicationAlreadyRunning);
                } else if (data.event == "tooManyClientsNotification") {
                    api.showDialog(api.tooManyClientsNotification);
                } else if (data.event == "continueOldSession") {
                    api.cfg.canPaint = false;
                    api.showDialog(api.continueOldSessionDialog);
                }
                return;
            }
            if (data.jsRequest != null && api.cfg.mirrorMode == false) {
                api.processJsLink(data.jsRequest);
            }
            if (api.cfg.canPaint) {
                processRequest(api.getCanvas(), data);
            }
        }

        function processRequest(canvas, data) {
            api.hideDialog();
            var context = canvas.getContext("2d");
            if (data.linkAction != null) {
                if (data.linkAction.action == 'url') {
                    api.openLink(data.linkAction.src);
                } else if (data.linkAction.action == 'print') {
                    api.print(encodeURIComponent(location.pathname + 'file?id=' + data.linkAction.src));
                } else if (data.linkAction.action == 'file') {
                    api.download('file?id=' + data.linkAction.src);
                }
            }
            if (data.moveAction != null) {
                copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height,
                        context);
            }
            if (data.cursorChange != null && api.cfg.hasControl) {
                canvas.style.cursor = data.cursorChange.cursor;
            }
            if (data.copyEvent != null && api.cfg.hasControl) {
                api.displayCopyBar(data.copyEvent);
            }
            if (data.fileDialogEvent != null && api.cfg.hasControl) {
                if (data.fileDialogEvent.eventType === 'Open') {
                    api.openFileDialog(data.fileDialogEvent, api.cfg.clientId);
                } else if (data.fileDialogEvent.eventType === 'Close') {
                    api.closeFileDialog();
                }
            }
            if (data.closedWindow != null) {
                delete windowImageHolders[data.closedWindow];
            }
            // firs is always the background
            for ( var i in data.windows) {
                var win = data.windows[i];
                if (win.id == 'BG') {
                    if (api.cfg.mirrorMode) {
                        adjustCanvasSize(canvas, win.width, win.height);
                    }
                    for ( var x in win.content) {
                        var winContent = win.content[x];
                        if (winContent != null) {
                            clear(win.posX + winContent.positionX, win.posY + winContent.positionY, winContent.width, winContent.height, context);
                        }
                    }
                    data.windows.splice(i, 1);
                    break;
                }
            }
            // regular windows (background removed)
            if (data.windows != null) {
                data.windows.reduce(
                        function(sequence, win) {
                            if (win.directDraw != null) {
                                // directdraw
                                return sequence.then(function(resolved) {
                                    if (typeof win.directDraw === 'string') {
                                        return directDraw.draw64(win.directDraw, windowImageHolders[win.id]);
                                    } else {
                                        return directDraw.drawBin(win.directDraw, windowImageHolders[win.id]);
                                    }
                                }).then(
                                        function(resultImage) {
                                            windowImageHolders[win.id] = resultImage;
                                            for ( var x in win.content) {
                                                var winContent = win.content[x];
                                                if (winContent != null) {
                                                    context.drawImage(resultImage, winContent.positionX, winContent.positionY, winContent.width,
                                                            winContent.height, win.posX + winContent.positionX, win.posY + winContent.positionY,
                                                            winContent.width, winContent.height);
                                                }
                                            }
                                        });
                            } else {
                                // imagedraw
                                return sequence.then(function(resolved) {
                                    return win.content.reduce(function(internalSeq, winContent) {
                                        return internalSeq.then(function(done) {
                                            return new Promise(
                                                    function(resolved, rejected) {
                                                        if (winContent != null) {
                                                            var imageObj = new Image();
                                                            var onloadFunction = function() {
                                                                context.drawImage(imageObj, win.posX + winContent.positionX, win.posY
                                                                        + winContent.positionY);
                                                                resolved();
                                                                imageObj.onload = null;
                                                                imageObj.src = '';
                                                                if (imageObj.clearAttributes != null) {
                                                                    imageObj.clearAttributes();
                                                                }
                                                                imageObj = null;
                                                            };
                                                            imageObj.onload = function() {
                                                                // fix for ie - onload is fired before the image is ready for rendering to canvas.
                                                                // This is
                                                                // a ugly quickfix
                                                                if (api.cfg.ieVersion && api.cfg.ieVersion <= 10) {
                                                                    window.setTimeout(onloadFunction, 20);
                                                                } else {
                                                                    onloadFunction();
                                                                }
                                                            };
                                                            imageObj.src = getImageString(winContent.base64Content);
                                                        }
                                                    });
                                        });
                                    }, Promise.resolve());
                                });
                            }
                        }, Promise.resolve()).then(function() {
                    ack();
                });
            }
        }

        function getImageString(data) {
            if (typeof data === 'object') {
                var binary = '';
                var bytes = new Uint8Array(data.buffer, data.offset, data.limit - data.offset);
                for ( var i = 0, l = bytes.byteLength; i < l; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }
                data = window.btoa(binary);
            }
            return 'data:image/png;base64,' + data;
        }

        function adjustCanvasSize(canvas, width, height) {
            if (canvas.width != width || canvas.height != height) {
                var snapshot = canvas.getContext("2d").getImageData(0, 0, canvas.width, canvas.height);
                canvas.width = width;
                canvas.height = height;
                canvas.getContext("2d").putImageData(snapshot, 0, 0);
            }
        }

        function clear(x, y, w, h, context) {
            context.clearRect(x, y, w, h);
        }

        function copy(sx, sy, dx, dy, w, h, context) {
            var copy = context.getImageData(sx, sy, w, h);
            context.putImageData(copy, dx, dy);
        }

        function registerEventListeners(canvas, input) {
            bindEvent(canvas, 'mousedown', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousedown');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            bindEvent(canvas, 'dblclick', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'dblclick');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            bindEvent(canvas, 'mousemove', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousemove');
                mousePos.mouse.button = mouseDown;
                latestMouseMoveEvent = mousePos;
                return false;
            }, false);
            bindEvent(canvas, 'mouseup', function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mouseup');
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
                return false;
            }, false);
            // IE9, Chrome, Safari, Opera
            bindEvent(canvas, "mousewheel", function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousewheel');
                latestMouseMoveEvent = null;
                if (latestMouseWheelEvent != null) {
                    mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
                }
                latestMouseWheelEvent = mousePos;
                return false;
            }, false);
            // firefox
            bindEvent(canvas, "DOMMouseScroll", function(evt) {
                var mousePos = getMousePos(canvas, evt, 'mousewheel');
                latestMouseMoveEvent = null;
                if (latestMouseWheelEvent != null) {
                    mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
                }
                latestMouseWheelEvent = mousePos;
                return false;
            }, false);
            bindEvent(canvas, 'contextmenu', function(event) {
                event.preventDefault();
                event.stopPropagation();
                return false;
            });

            bindEvent(input, 'keydown', function(event) {
                // 48-57
                // 65-90
                // 186-192
                // 219-222
                // 226
                // FF (163, 171, 173, ) -> en layout ]\/ keys
                var kc = event.keyCode;
                if (!((kc >= 48 && kc <= 57) || (kc >= 65 && kc <= 90) || (kc >= 186 && kc <= 192) || (kc >= 219 && kc <= 222) || (kc == 226)
                        || (kc == 0) || (kc == 163) || (kc == 171) || (kc == 173) || (kc >= 96 && kc <= 111) || (kc == 59) || (kc == 61))) {
                    event.preventDefault();
                    event.stopPropagation();
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
            bindEvent(input, 'keypress', function(event) {
                var keyevt = getKBKey('keypress', canvas, event);
                if (!(keyevt.key.ctrl && (keyevt.key.character == 120 || keyevt.key.character == 24 || keyevt.key.character == 99
                        || keyevt.key.character == 118 || keyevt.key.character == 22))) { // cut copy paste handled separately
                    event.preventDefault();
                    event.stopPropagation();
                    enqueueInputEvent(keyevt);
                }
                return false;
            }, false);
            bindEvent(input, 'keyup', function(event) {
                var keyevt = getKBKey('keyup', canvas, event);
                if (!(keyevt.key.ctrl && (keyevt.key.character == 88 || keyevt.key.character == 67 || keyevt.key.character == 86))) { // cut copy
                    event.preventDefault();
                    event.stopPropagation();
                    enqueueInputEvent(keyevt);
                    sendInput();
                }
                return false;
            }, false);
            bindEvent(input, 'cut', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.cut(event);
                return false;
            }, false);
            bindEvent(input, 'copy', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.copy(event);
                return false;
            }, false);
            bindEvent(input, 'paste', function(event) {
                event.preventDefault();
                event.stopPropagation();
                api.paste(event);
                return false;
            }, false);
            bindEvent(document, 'mousedown', mouseDownEventHandler);
            bindEvent(document, 'mouseout', mouseOutEventHandler);
            bindEvent(document, 'mouseup', mouseUpEventHandler);
            bindEvent(window, 'beforeunload', beforeUnloadEventHandler);
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
        function beforeUnloadEventHandler(evt) {
            dispose();
        }

        function focusInput(input) {
            // In order to ensure that the browser will fire clipboard events, we always need to have something selected
            input.value = ' ';
            input.focus();
            input.select();
        }

        function getMousePos(canvas, evt, type) {
            var rect = canvas.getBoundingClientRect();
            var root = document.documentElement;
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

        function getHandShake(canvas) {
            var handshake = {
                applicationName : api.cfg.appName,
                clientId : api.cfg.clientId,
                sessionId : api.getSocketId(),
                mirrored : api.cfg.mirrorMode,
                directDrawSupported : api.cfg.typedArraysSupported
            };

            if (!api.cfg.mirrorMode) {
                handshake.applet = api.cfg.applet;
                handshake.documentBase = api.cfg.documentBase;
                handshake.params = api.cfg.params;
                handshake.desktopWidth = canvas.offsetWidth;
                handshake.desktopHeight = canvas.offsetHeight;
            }
            return {
                handshake : handshake
            };
        }

        function getMessageEvent(message) {
            return {
                event : {
                    type : message
                }
            };
        }

        function bindEvent(el, eventName, eventHandler) {
            if (el.addEventListener != null) {
                el.addEventListener(eventName, eventHandler);
            }
        }
    };
});
