define([ 'webswing-dd', 'webswing-util' ], function amdFactory(WebswingDirectDraw, util) {
    "use strict";
    return function BaseModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg : 'webswing.config',
            disconnect : 'webswing.disconnect',
            getSocketId : 'socket.uuid',
            getCanvas : 'canvas.get',
            registerInput : 'input.register',
            sendInput : 'input.sendInput',
            disposeInput : 'input.dispose',
            registerTouch : 'touch.register',
            disposeTouch : 'touch.dispose',
            getUser : 'login.user',
            login : 'login.login',
            getIdentity : 'identity.get',
            disposeIdentity : 'identity.dispose',
            getLocale : 'identity.getLocale',
            showDialog : 'dialog.show',
            hideDialog : 'dialog.hide',
            startingDialog : 'dialog.content.startingDialog',
            stoppedDialog : 'dialog.content.stoppedDialog',
            applicationAlreadyRunning : 'dialog.content.applicationAlreadyRunning',
            sessionStolenNotification : 'dialog.content.sessionStolenNotification',
            tooManyClientsNotification : 'dialog.content.tooManyClientsNotification',
            continueOldSessionDialog : 'dialog.content.continueOldSessionDialog',
            showSelector : 'selector.show',
            openFileDialog : 'files.open',
            closeFileDialog : 'files.close',
            openLink : 'files.link',
            print : 'files.print',
            download : 'files.download',
            displayCopyBar : 'clipboard.displayCopyBar',
            processJsLink : 'jslink.process',
            playbackInfo : 'playback.playbackInfo'
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

        var timer1, timer2, timer3;
        var drawingLock;
        var drawingQ = [];

        var windowImageHolders = {};
        var directDraw = new WebswingDirectDraw({});

        function startApplication(name, applet, alwaysReset) {
            if (alwaysReset===true){
                api.disposeIdentity();
            }
            initialize(api.getUser() + api.getIdentity() + name, name, applet, false);
        }

        function startMirrorView(clientId, appName) {
            initialize(clientId, appName, null, true)
        }

        function initialize(clientId, name, applet, isMirror) {
            api.registerInput();
            api.registerTouch();
            window.addEventListener('beforeunload', beforeUnloadEventHandler);
            resetState();
            api.cfg.clientId = clientId;
            api.cfg.appName = name;
            api.cfg.canPaint = true;
            api.cfg.hasControl = !isMirror;
            api.cfg.mirrorMode = isMirror;
            api.cfg.applet = applet != null ? applet : api.cfg.applet;
            handshake();
            if (isMirror) {
                repaint();
            }
            api.showDialog(api.startingDialog);
        }

        function beforeUnloadEventHandler(evt) {
            dispose();
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
            api.cfg.viewId = util.GUID(); 
            api.cfg.appName = null;
            api.cfg.hasControl = false;
            api.cfg.mirrorMode = false;
            api.cfg.canPaint = false;
            clearInterval(timer1);
            clearInterval(timer2);
            clearInterval(timer3);
            timer1 = setInterval(api.sendInput, 100);
            timer2 = setInterval(heartbeat, 10000);
            timer3 = setInterval(servletHeartbeat, 100000);
            windowImageHolders = {};
            directDraw.dispose();
            directDraw = new WebswingDirectDraw({});
        }

        function sendMessageEvent(message) {
            api.sendInput({
                event : {
                    type : message
                }
            });
        }

        function heartbeat() {
            sendMessageEvent('hb');
        }

        function servletHeartbeat() {
        	//touch servlet session to avoid timeout
        	api.login(function(){},function(){});
        }

        function repaint() {
            sendMessageEvent('repaint');
        }

        function ack() {
            sendMessageEvent('paintAck');
        }

        function kill() {
        	if (api.cfg.hasControl) {
        		sendMessageEvent('killSwing');
        	}
        }

        function unload() {
        	if(!api.cfg.mirrorMode){
        		sendMessageEvent('unload');
        	}
        }

        function handshake() {
            api.sendInput(getHandShake(api.getCanvas()));
        }

        function dispose() {
            clearInterval(timer1);
            clearInterval(timer2);
            unload();
            api.sendInput();
            resetState();
            api.disposeInput();
            api.disposeTouch();
            window.removeEventListener('beforeunload', beforeUnloadEventHandler);
            directDraw.dispose();
        }

        function processMessage(data) {
            if (data.playback != null) {
                api.playbackInfo(data);
            }
            if (data.applications != null && data.applications.length != 0) {
                api.showSelector(data.applications);
            }
            if (data.event != null && !api.cfg.recordingPlayback) {
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
                }else if (data.event == "sessionStolenNotification") {
                	api.cfg.canPaint = false;
                	api.showDialog(api.sessionStolenNotification);
                }
                return;
            }
            if (data.jsRequest != null && api.cfg.mirrorMode == false && !api.cfg.recordingPlayback) {
                api.processJsLink(data.jsRequest);
            }
            if (api.cfg.canPaint) {
                queuePaintingRequest(data);
            }
        }

        function queuePaintingRequest(data) {
            drawingQ.push(data);
            if (!drawingLock) {
                processNextQueuedFrame();
            }
        }

        function processNextQueuedFrame() {
            drawingLock = null;
            if (drawingQ.length > 0) {
                drawingLock = drawingQ.shift();
                try {
                    processRequest(api.getCanvas(), drawingLock);
                } catch (error) {
                    drawingLock = null;
                    throw error;
                }
            }
        }

        function processRequest(canvas, data) {
            api.hideDialog();
            var context = canvas.getContext("2d");
            if (data.linkAction != null && !api.cfg.recordingPlayback) {
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
            if (data.cursorChange != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                canvas.style.cursor = data.cursorChange.cursor;
            }
            if (data.copyEvent != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                api.displayCopyBar(data.copyEvent);
            }
            if (data.fileDialogEvent != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                if (data.fileDialogEvent.eventType === 'Open') {
                    api.openFileDialog(data.fileDialogEvent, api.cfg.clientId);
                } else if (data.fileDialogEvent.eventType === 'Close') {
                    api.closeFileDialog();
                }
            }
            if (data.closedWindow != null) {
                delete windowImageHolders[data.closedWindow];
            }
            // first is always the background
            for ( var i in data.windows) {
                var win = data.windows[i];
                if (win.id == 'BG') {
                    if (api.cfg.mirrorMode || api.cfg.recordingPlayback) {
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
                data.windows.reduce(function(sequence, window) {
                    return sequence.then(function() {
                        return renderWindow(window, context);
                    }, errorHandler);
                }, Promise.resolve()).then(function() {
                    ack();
                    processNextQueuedFrame();
                }, errorHandler);
            } else {
                processNextQueuedFrame();
            }
        }

        function renderWindow(win, context) {
            if (win.directDraw != null) {
                return renderDirectDrawWindow(win, context);
            } else {
                return renderPngDrawWindow(win, context);
            }
        }

        function renderPngDrawWindow(win, context) {
            return win.content.reduce(function(sequence, winContent) {
                return sequence.then(function() {
                    return new Promise(function(resolved, rejected) {
                        if (winContent != null) {
                            var imageObj = new Image();
                            var onloadFunction = function() {
                                context.drawImage(imageObj, win.posX + winContent.positionX, win.posY + winContent.positionY);
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
                                // This is an ugly quickfix
                                if (api.cfg.ieVersion && api.cfg.ieVersion <= 10) {
                                    window.setTimeout(onloadFunction, 20);
                                } else {
                                    onloadFunction();
                                }
                            };
                            imageObj.src = util.getImageString(winContent.base64Content);
                        }
                    });
                }, errorHandler);
            }, Promise.resolve());
        }

        function renderDirectDrawWindow(win, context) {
            return new Promise(function(resolved, rejected) {
                var ddPromise;
                if (typeof win.directDraw === 'string') {
                    ddPromise = directDraw.draw64(win.directDraw, windowImageHolders[win.id]);
                } else {
                    ddPromise = directDraw.drawBin(win.directDraw, windowImageHolders[win.id]);
                }
                ddPromise.then(function(resultImage) {
                    windowImageHolders[win.id] = resultImage;
                    for ( var x in win.content) {
                        var winContent = win.content[x];
                        if (winContent != null) {
                            context.drawImage(resultImage, winContent.positionX, winContent.positionY, winContent.width, winContent.height, win.posX
                                    + winContent.positionX, win.posY + winContent.positionY, winContent.width, winContent.height);
                        }
                    }
                    resolved();
                }, function(error) {
                    rejected(error);
                });
            });
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

        function getHandShake(canvas) {
            var handshake = {
                applicationName : api.cfg.appName,
                clientId : api.cfg.clientId,
                viewId : api.cfg.viewId,
                sessionId : api.getSocketId(),
                locale : api.getLocale(),
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

        function errorHandler(error) {
            drawingLock = null;
            throw error;
        }
    };
});
