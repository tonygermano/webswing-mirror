import { DirectDraw as WebswingDirectDraw} from "webswing-directdraw-javascript"
    "use strict";
    export default  function BaseModule(util) {
        var module = this;
        var api;
        module.injects = api = {
        	external: 'external',
            cfg: 'webswing.config',
            disconnect: 'webswing.disconnect',
            getSocketId: 'socket.uuid',
            send: 'socket.send',
            getCanvas: 'canvas.get',
            getInput: 'canvas.getInput',
            focusInput: 'canvas.focusInput',
            getDesktopSize: 'canvas.getDesktopSize',
            processComponentTree: 'canvas.processComponentTree',
            registerInput: 'input.register',
            sendInput: 'input.sendInput',
            registerTouch: 'touch.register',
            touchCursorChanged: 'touch.cursorChanged',
            touchInputFocusGained: 'touch.inputFocusGained',
            touchBarConfig: 'touch.touchBarConfig',
            getUser: 'login.user',
            login: 'login.login',
            logout: 'login.logout',
            touchSession: 'login.touchSession',
            getIdentity: 'identity.get',
            disposeIdentity: 'identity.dispose',
            getLocale: 'translate.getLocale',
            showDialog: 'dialog.show',
            showDialogBar: 'dialog.showBar',
            hideDialog: 'dialog.hide',
            hideDialogBar: 'dialog.hideBar',
            dialogBarContent: 'dialog.currentBar',
            currentDialog: 'dialog.current',
            startingDialog: 'dialog.content.startingDialog',
            stoppedDialog: 'dialog.content.stoppedDialog',
            timedoutDialog: 'dialog.content.timedoutDialog',
            unauthorizedAccess: 'dialog.content.unauthorizedAccess',
            applicationAlreadyRunning: 'dialog.content.applicationAlreadyRunning',
            sessionStolenNotification: 'dialog.content.sessionStolenNotification',
            tooManyClientsNotification: 'dialog.content.tooManyClientsNotification',
            continueOldSessionDialog: 'dialog.content.continueOldSessionDialog',
            inactivityTimeoutWarningDialog: 'dialog.content.inactivityTimeoutWarningDialog',
            applicationBusyDialog: 'dialog.content.applicationBusyDialog',
            processFileDialogEvent: 'files.process',
            closeFileDialog: 'files.close',
            openLink: 'files.link',
            print: 'files.print',
            download: 'files.download',
            redirect: 'files.redirect',
            displayCopyBar: 'clipboard.displayCopyBar',
            displayPasteDialog: 'clipboard.displayPasteDialog',
            processJsLink: 'jslink.process',
            playbackInfo: 'playback.playbackInfo',
            paintDoneCallback: 'base.paintDoneCallback',
            performAction: 'base.performAction',
            handleActionEvent: 'base.handleActionEvent'
        };
        module.provides = {
            startApplication: startApplication,
            startMirrorView: startMirrorView,
            continueSession: continueSession,
            kill: kill,
            handshake: handshake,
            processMessage: processMessage,
            dispose: dispose,
            repaint: repaint,
            requestComponentTree: requestComponentTree,
            paintDoneCallback: paintDoneCallback,
            getWindows: getWindows,
            getWindowById: getWindowById,
            performAction: performAction,
            handleActionEvent: handleActionEvent
        };
        module.ready = function () {
            directDraw = new WebswingDirectDraw({logTrace: api.cfg.traceLog, logDebug: api.cfg.debugLog, ieVersion: api.cfg.ieVersion, dpr: util.dpr});
        };

        var timer1, timer3;
        var drawingLock;
        var drawingQ = [];
        var warningTimeout = null;
        var windowImageHolders = {}; // <id> : <CanvasWindow/HtmlWindow>
        var closedWindows = {}; // <id> : <boolean>, map of windows requested to be closed, rendering of windows in this map will be ignored, until message about closed window is received
        var directDraw;
        var compositingWM = false;
        var compositionBaseZIndex = 100;
        const JFRAME_MAXIMIZED_STATE = 6;

        function startApplication() {
            initialize(null, false);
        }

        function startMirrorView(clientId) {
            initialize(clientId, true)
        }

        function initialize(clientId, isMirror) {
            api.registerInput();
            if (!isMirror) {
            	api.registerTouch(api.touchBarConfig);
            }
            window.addEventListener('beforeunload', beforeUnloadEventHandler);
            if (!isMirror) {
                window.addEventListener('hashchange', handshake);
            }
            resetState();
            api.cfg.clientId = clientId;
            api.cfg.canPaint = true;
            api.cfg.hasControl = !isMirror;
            api.cfg.mirrorMode = isMirror;
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
            api.cfg.hasControl = false;
            api.cfg.mirrorMode = false;
            api.cfg.canPaint = false;
            clearInterval(timer1);
            clearInterval(timer3);
            timer1 = setInterval(api.sendInput, 100);
            timer3 = setInterval(servletHeartbeat, 100000);
            compositingWM = false;
            windowImageHolders = {};
            closedWindows = {};
            directDraw.dispose();
            directDraw = new WebswingDirectDraw({logTrace: api.cfg.traceLog, logDebug: api.cfg.debugLog, ieVersion: api.cfg.ieVersion, dpr: util.dpr});
        }

        function sendMessageEvent(message) {
            api.sendInput({
                event: {
                    type: message
                }
            });
        }

        function servletHeartbeat() {
            // touch servlet session to avoid timeout
            api.touchSession();
        }

        function repaint() {
            sendMessageEvent('repaint');
        }

        function ack(data) {
            sendMessageEvent('paintAck');
            if (data != null) {
                api.sendInput({
                    timestamps: {
                        startTimestamp: data.startTimestamp,
                        sendTimestamp: data.sendTimestamp,
                        renderingTime: "" + (new Date().getTime() - data.receivedTimestamp)
                    }
                });
            }
        }

        function kill() {
            if (api.cfg.hasControl) {
                sendMessageEvent('killSwing');
            }
        }

        function unload() {
            if (!api.cfg.mirrorMode) {
                sendMessageEvent('unload');
            }
        }

        function handshake() {
            api.sendInput(getHandShake(api.getCanvas()));
        }

        function dispose() {
            clearInterval(timer1);
            unload();
            api.sendInput();
            resetState();
            api.closeFileDialog();
            api.hideDialogBar();
            window.removeEventListener('beforeunload', beforeUnloadEventHandler);
            directDraw.dispose();
        }
        
        function requestComponentTree() {
        	sendMessageEvent('requestComponentTree');
        	repaint();
        }

        function processMessage(data) {
            if (data.playback != null) {
                api.playbackInfo(data);
            }
            if (data.applications != null && data.applications.length != 0) {
                if (api.cfg.mirror) {
                    startMirrorView(api.cfg.clientId);
                } else if (data.applications.length === 1) {
                    startApplication();
                }
                return;
            }
            if (data.event != null && !api.cfg.recordingPlayback) {
                if (data.event == "shutDownAutoLogoutNotification") {
                    api.disconnect();
                    if (api.cfg.mirror == false) {
                        api.logout();
                    }
                } else if (data.event == "shutDownNotification") {
                    if (api.currentDialog() !== api.timedoutDialog) {
                        api.showDialog(api.stoppedDialog);
                    }
                    api.disconnect();
                } else if (data.event == "applicationAlreadyRunning") {
                    api.showDialog(api.applicationAlreadyRunning);
                } else if (data.event == "tooManyClientsNotification") {
                    api.showDialog(api.tooManyClientsNotification);
                } else if (data.event == "continueOldSession") {
                    continueSession();
                    var oldBarContent = api.dialogBarContent();
                    api.showDialogBar(api.continueOldSessionDialog);
                    api.dialogBarContent().focused = false;
                    window.setTimeout(function () {
                        if (api.dialogBarContent() != null && !api.dialogBarContent().focused) {
                            api.hideDialogBar();
                            api.showDialogBar(oldBarContent);
                        }
                    }, 5000);
                } else if (data.event == "sessionStolenNotification") {
                    api.cfg.canPaint = false;
                    api.showDialog(api.sessionStolenNotification);
                } else if (data.event == "unauthorizedAccess") {
                    api.cfg.canPaint = false;
                    api.showDialog(api.unauthorizedAccess);
                } else if (data.event == "sessionTimeoutWarning") {
                    window.clearTimeout(warningTimeout);
                    api.showDialogBar(api.inactivityTimeoutWarningDialog);
                    warningTimeout = window.setTimeout(function () {
                        api.hideDialogBar();
                    }, 2000);
                } else if (data.event == "sessionTimedOutNotification") {
                    api.showDialog(api.timedoutDialog);
                    api.disconnect();
                } else if (data.event == "applicationBusy") {
                    if (api.currentDialog() == null) {
                        api.showDialog(api.applicationBusyDialog);
                    }
                }
                return;
            }
            if (data.jsRequest != null && api.cfg.mirrorMode == false && !api.cfg.recordingPlayback) {
                api.processJsLink(data.jsRequest);
            }
            if (data.pixelsRequest != null && api.cfg.mirrorMode == false && !api.cfg.recordingPlayback) {
                var pixelsResponse = getPixels(data.pixelsRequest);
                api.send(pixelsResponse);
            }
            if (api.cfg.canPaint) {
                queuePaintingRequest(data);
            }
            if (data.componentTree) {
            	api.processComponentTree(data.componentTree);
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
                    processRequest(drawingLock);
                } catch (error) {
                    drawingLock = null;
                    throw error;
                }
            } else {
            	// empty queue
            	api.paintDoneCallback();
            }
        }
        
        function paintDoneCallback() {
        	// to be customized
        }

        function processRequest(data) {
        	var windowsData = null;
        	if (data.windows != null && data.windows.length > 0) {
        		windowsData = data.windows;
        	}
        	if (data.compositingWM && !compositingWM) {
    			compositingWM = true;
    			api.cfg.rootElement.addClass("composition");
    			$(api.getCanvas()).css({"width": "0px", "height": "0px", "position": "absolute", "top" : "0", "left": "0"});
        	}
        	
            if (windowsData != null) {
                api.hideDialog();
            }
            if (data.linkAction != null && !api.cfg.recordingPlayback) {
                if (data.linkAction.action == 'url') {
                    api.openLink(data.linkAction.src);
                } else if (data.linkAction.action == 'print') {
                    api.print('file?id=' + data.linkAction.src);
                } else if (data.linkAction.action == 'file') {
                    api.download('file?id=' + data.linkAction.src);
                } else if (data.linkAction.action == 'redirect' && !api.cfg.mirrorMode) {
                	api.redirect(data.linkAction.src);
                }
            }
            if (data.moveAction != null) {
            	// this applies only to non-CWM 
                copy(data.moveAction.sx, data.moveAction.sy, data.moveAction.dx, data.moveAction.dy, data.moveAction.width, data.moveAction.height, api.getCanvas().getContext("2d"));
            }
            if (data.focusEvent != null) {
                var input = api.getInput();

                input.classList.remove('editable');
                input.classList.remove('focused-with-caret');
                var focusElement;
                if ((data.focusEvent.type === 'focusWithCarretGained' || data.focusEvent.type === 'focusPasswordGained') && (data.focusEvent.x + data.focusEvent.w > 0) && (data.focusEvent.y + data.focusEvent.h > 0)) {
                    if (data.focusEvent.type === 'focusPasswordGained') {
                    	input.type = 'password';
                    } else {
                    	input.type = 'text';
                    }
                    
                    updateInputPosition(input, data.focusEvent);
                    input.classList.add('focused-with-caret');
                    if (data.focusEvent.editable) {
                    	input.classList.add('editable');
                    }
                    api.touchInputFocusGained();
                    focusElement =input
                } else {
                    updateInputPosition(input, null);
                    input.value = '';
                    focusElement= api.getCanvas()
                }

                util.focusWithPreventScroll(focusElement);
            }
            if (data.cursorChange != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                $("canvas.webswing-canvas").each(function(i, canvas) {
                	canvas.style.cursor = getCursorStyle(data.cursorChange);
                });
                api.touchCursorChanged(data.cursorChange);
            }
            if (data.copyEvent != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                api.displayCopyBar(data.copyEvent);
            }
            if (data.pasteRequest != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                api.displayPasteDialog(data.pasteRequest);
            }
            if (data.fileDialogEvent != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                api.processFileDialogEvent(data.fileDialogEvent);
            }
            if (data.closedWindow != null) {
            	var canvasWindow = windowImageHolders[data.closedWindow.id];

            	if (canvasWindow) {
            		if (compositingWM) {
            			windowClosing(canvasWindow);
            			try {
            				canvasWindow.windowClosing();
                        } catch (e) {
                            console.error(e);
                        }
            		}

            		$(canvasWindow.element).remove();
            		delete windowImageHolders[data.closedWindow.id];

            		if (compositingWM) {
            			windowClosed(canvasWindow);
            			try {
            				canvasWindow.windowClosed();
                        } catch (e) {
                            console.error(e);
                        }
            		}
            	}

            	delete closedWindows[data.closedWindow.id];
            }
            if (data.actionEvent != null && api.cfg.hasControl && !api.recordingPlayback) {
            	try {
            		if (data.actionEvent.windowId && windowImageHolders[data.actionEvent.windowId]) {
            			windowImageHolders[data.actionEvent.windowId].handleActionEvent(data.actionEvent.actionName, data.actionEvent.data, data.actionEvent.binaryData);
            		} else {
            			api.handleActionEvent(data.actionEvent.actionName, data.actionEvent.data, data.actionEvent.binaryData);
            		}
                } catch (e) {
                    console.error(e);
                }
            }
            
            if (windowsData != null) {
            	// first is always the background
            	for (var i in windowsData) {
            		var win = windowsData[i];
            		if (win.id == 'BG') {
            			if (api.cfg.mirrorMode || api.cfg.recordingPlayback) {
            				adjustCanvasSize(api.getCanvas(), win.width, win.height);
            			}
            			for (var x in win.content) {
            				var winContent = win.content[x];
            				if (winContent != null) {
            					clear(win.posX + winContent.positionX, win.posY + winContent.positionY, winContent.width, winContent.height, api.getCanvas().getContext("2d"));
            				}
            			}
            			windowsData.splice(i, 1);
            			break;
            		}
            	}
            	
            	// regular windows (background removed)
            	windowsData.reduce(function (sequence, window, index) {
            		return sequence.then(function () {
            			return renderWindow(window, compositingWM ? windowsData.length - index - 1 : index, data.directDraw);
            		}, errorHandler);
            	}, Promise.resolve()).then(function () {
            		ack(data);
            		processNextQueuedFrame();
            	}, errorHandler);
            } else {
                processNextQueuedFrame();
            }
        }

        function updateInputPosition(el, focusEvent) {
            if (focusEvent == null) {
                el.style.top = null;
                el.style.left = null;
                el.style.height = null;
            } else {
                var maxX = focusEvent.x + focusEvent.w;
                var maxY = focusEvent.y + focusEvent.h;
                el.style.top = Math.min(Math.max((focusEvent.y + focusEvent.caretY), focusEvent.y), maxY) + 'px';
                el.style.left = Math.min(Math.max((focusEvent.x + focusEvent.caretX), focusEvent.x), maxX) + 'px';
                el.style.height = focusEvent.caretH + 'px';
            }
        }

        function renderWindow(win, index, directDraw) {
        	if (closedWindows[win.id]) {
        		// ignore this window as it has been requested to be closed
        		return Promise.resolve();
        	}
        	
            if (directDraw) {
            	if (compositingWM) {
            		return renderDirectDrawComposedWindow(win, index);
            	}
                return renderDirectDrawWindow(win, api.getCanvas().getContext("2d"));
            } else {
            	if (compositingWM) {
            		return renderPngDrawComposedWindow(win, index);
            	}
                return renderPngDrawWindow(win, api.getCanvas().getContext("2d"));
            }
        }

        function renderPngDrawWindow(win, context) {
        	return renderPngDrawWindowInternal(win, context);
        }
        
        function renderPngDrawComposedWindow(win, index) {
			if (win.html) {
				var newWindowOpened = false;

				if (windowImageHolders[win.id] == null) {
 					var htmlDiv = document.createElement("div");
 					htmlDiv.classList.add("webswing-html-canvas");

 					windowImageHolders[win.id] = new HtmlWindow(win.id, htmlDiv, win.name);
 					newWindowOpened = true;
 					$(htmlDiv).attr('data-id', win.id).css("position", "absolute");

 					windowOpening(windowImageHolders[win.id]);
 					if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
						windowImageHolders[win.ownerId].element.parentNode.append(htmlDiv);
					} else {
						api.cfg.rootElement.append(htmlDiv);
					}
				}

				var htmlDiv = windowImageHolders[win.id].element;

				$(htmlDiv).css({"z-index": (compositionBaseZIndex + index + 1), "width": win.width + 'px', "height": win.height + 'px'});
				if ($(htmlDiv).is(".modal-blocked") != win.modalBlocked) {
					$(htmlDiv).toggleClass("modal-blocked", win.modalBlocked);
					windowModalBlockedChanged(windowImageHolders[win.id]);
				}
				if (isVisible(htmlDiv.parentNode)) {
					$(htmlDiv).css({"left": win.posX + 'px', "top": win.posY + 'px'});
				}

				if (newWindowOpened) {
					windowOpened(windowImageHolders[win.id]);
					if (!api.cfg.mirrorMode) {
						performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
					}
				}

				return Promise.resolve();
			} else {
				var newWindowOpened = false;

				if (windowImageHolders[win.id] == null) {
					var canvas = document.createElement("canvas");
					canvas.classList.add("webswing-canvas");

					windowImageHolders[win.id] = new CanvasWindow(win.id, canvas, win.name, win.title);
					newWindowOpened = true;
					$(canvas).attr('data-id', win.id).css("position", "absolute");

					windowOpening(windowImageHolders[win.id]);
					if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
						windowImageHolders[win.ownerId].element.parentNode.append(canvas);
					} else {
						api.cfg.rootElement.append(canvas);
					}
				}

				var canvas = windowImageHolders[win.id].element;
				var canvasWin = windowImageHolders[win.id];

				if ($(canvas).width() != win.width || $(canvas).height() != win.height) {
					$(canvas).css({"width": win.width + 'px', "height": win.height + 'px'});
					$(canvas).attr("width", win.width).attr("height", win.height);
				}

				validateAndPositionWindow(canvasWin, win.posX, win.posY);

				// update z-order
				$(canvas).css({"z-index": (compositionBaseZIndex + index + 1)});
				if ($(canvas).is(".modal-blocked") != win.modalBlocked) {
					$(canvas).toggleClass("modal-blocked", win.modalBlocked);
					windowModalBlockedChanged(canvasWin);
				}

				if (newWindowOpened) {
					windowOpened(canvasWin);
					if (!api.cfg.mirrorMode) {
						performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
					}
				}

				if (typeof win.state !== 'undefined' && win.state == JFRAME_MAXIMIZED_STATE) {
					canvasWin.state = win.state;
					if (!api.cfg.mirrorMode && canvas.parentNode) {
						// window has been maximized, we need to set its bounds according to its parent node (could be detached)
						var rectP = canvas.parentNode.getBoundingClientRect();
						var rectC = canvas.getBoundingClientRect();
						if (rectC.width != rectP.width || rectC.height != rectP.height) {
							canvasWin.setBounds(0, 0, rectP.width, rectP.height);
						}
					}
				}

				return renderPngDrawWindowInternal(win, canvas.getContext("2d"));
			}
        }
        
        function renderPngDrawWindowInternal(win, context) {
        	if (!win.content) {
        		return Promise.resolve();
        	}
        	
        	return win.content.reduce(function (sequence, winContent) {
				return sequence.then(function () {
					return new Promise(function (resolved, rejected) {
						if (winContent != null) {
							var imageObj = new Image();
							var onloadFunction = function () {
								context.save();
								if (compositingWM) {
									context.drawImage(imageObj, winContent.positionX, winContent.positionY);
								} else {
									context.setTransform(util.dpr, 0, 0, util.dpr, 0, 0);
									context.drawImage(imageObj, win.posX + winContent.positionX, win.posY + winContent.positionY);
								}
								context.restore();
								resolved();
								imageObj.onload = null;
								imageObj.src = '';
								if (imageObj.clearAttributes != null) {
									imageObj.clearAttributes();
								}
								imageObj = null;
							};
							imageObj.onload = function () {
								// fix for ie - onload is fired before the image
								// is ready for rendering to canvas.
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
            return new Promise(function (resolved, rejected) {
            	var ddPromise;
            	var wih = windowImageHolders[win.id] != null ? windowImageHolders[win.id].element : null;
            	
                if (typeof win.directDraw === 'string') {
			        ddPromise = directDraw.draw64(win.directDraw, wih);
			    } else {
			        ddPromise = directDraw.drawBin(win.directDraw, wih);
			    }
                
			    ddPromise.then(function (canvas) {
			        windowImageHolders[win.id] = new CanvasWindow(win.id, canvas, win.name, win.title);
                    var dpr = util.dpr;
                    for (var x in win.content) {
                        var winContent = win.content[x];
                        if (winContent != null) {
                            var sx = Math.min(canvas.width, Math.max(0, winContent.positionX * dpr));
                            var sy = Math.min(canvas.height, Math.max(0, winContent.positionY * dpr));
                            var sw = Math.min(canvas.width - sx, winContent.width * dpr - (sx - winContent.positionX * dpr));
                            var sh = Math.min(canvas.height - sy, winContent.height * dpr - (sy - winContent.positionY * dpr));
			            
                            var dx = win.posX * dpr + sx;
                            var dy = win.posY * dpr + sy;
                            var dw = sw;
                            var dh = sh;
							if (dx <= context.canvas.width && dy <= context.canvas.height && dx + dw > 0 && dy + dh > 0 && sw>0 && sh>0 && dw>0 && dh>0) {
                                context.drawImage(canvas, sx, sy, sw, sh, dx, dy, dw, dh);
                            }
                        }
                    }
                    resolved();
                }, function (error) {
                    rejected(error);
                });
            });
        }
        
        function renderDirectDrawComposedWindow(win, index) {
        	return new Promise(function (resolved, rejected) {
        		var ddPromise;
        		var wih = windowImageHolders[win.id] != null ? windowImageHolders[win.id].element : null;
        		
        		if (win.html) {
        			// we don't need to draw html window, also do not create canvas
        			ddPromise = Promise.resolve(null);
        		} else if (win.directDraw == null) {
        			// no render data
        			if (wih == null) {
        				// new window and no data -> do not process
        				resolved();
        				return;
        			}
        			
       				ddPromise = Promise.resolve(wih);
        		} else if (typeof win.directDraw === 'string') {
        			ddPromise = directDraw.draw64(win.directDraw, wih);
        		} else {
        			ddPromise = directDraw.drawBin(win.directDraw, wih);
        		}
        		
        		ddPromise.then(function (canvas) {
        			var newWindowOpened = false;
        			
        			if (canvas != null) {
        				if (windowImageHolders[win.id] == null) {
        					windowImageHolders[win.id] = new CanvasWindow(win.id, canvas, win.name, win.title);
        					newWindowOpened = true;
        					$(canvas).attr('data-id', win.id).css("position", "absolute");

        					windowOpening(windowImageHolders[win.id]);
        					if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
        						windowImageHolders[win.ownerId].element.parentNode.append(canvas);
        					} else {
        						api.cfg.rootElement.append(canvas);
        					}
        				}
        			} else if (win.html) {
        				if (windowImageHolders[win.id] == null) {
        					var htmlDiv = document.createElement("div");
        					htmlDiv.classList.add("webswing-html-canvas");

        					windowImageHolders[win.id] = new HtmlWindow(win.id, htmlDiv, win.name);
        					newWindowOpened = true;
        					$(htmlDiv).attr('data-id', win.id).css("position", "absolute");

        					windowOpening(windowImageHolders[win.id]);
        					if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
        						windowImageHolders[win.ownerId].element.parentNode.append(htmlDiv);
        					} else {
        						api.cfg.rootElement.append(htmlDiv);
        					}
        				}
        			}

        			var htmlOrCanvasElement = $(windowImageHolders[win.id].element);
        			var htmlOrCanvasWin = windowImageHolders[win.id];

        			if (newWindowOpened) {
        				windowOpened(htmlOrCanvasWin);
        				if (!api.cfg.mirrorMode) {
        					performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
        				}
        			}

        			htmlOrCanvasElement.css({"z-index": (compositionBaseZIndex + index + 1), "width": win.width + 'px', "height": win.height + 'px'});
    				if (htmlOrCanvasElement.is(".modal-blocked") != win.modalBlocked) {
    					htmlOrCanvasElement.toggleClass("modal-blocked", win.modalBlocked);
    					windowModalBlockedChanged(htmlOrCanvasWin);
    				}

    				validateAndPositionWindow(htmlOrCanvasWin, win.posX, win.posY);

    				if (!htmlOrCanvasWin.htmlWindow && typeof win.state !== 'undefined' && win.state == JFRAME_MAXIMIZED_STATE) {
    					htmlOrCanvasWin.state = win.state;
    					if (!api.cfg.mirrorMode && htmlOrCanvasElement[0].parentNode) {
    						// window has been maximized, we need to set its bounds according to its parent node (could be detached)
    						var rectP = htmlOrCanvasElement[0].parentNode.getBoundingClientRect();
    						var rectC = htmlOrCanvasElement[0].getBoundingClientRect();
    						if (rectC.width != rectP.width || rectC.height != rectP.height) {
    							htmlOrCanvasWin.setBounds(0, 0, rectP.width, rectP.height);
    						}
    					}
    				}

        			resolved();
        		}, function (error) {
        			rejected(error);
        		});
        	});
        }
        
        function isVisible(element) {
        	if (!element || element == null) {
        		return false;
        	}
        	
        	return !!(element.offsetWidth || element.offsetHeight || element.getClientRects().length);
        }
        
        function validateAndPositionWindow(htmlOrCanvasWin, posX, posY) {
        	if (isVisible(htmlOrCanvasWin.element.parentNode)) {
				var winPosX = posX;
				var winPosY = posY;

				if (!htmlOrCanvasWin.htmlWindow) {
					var threshold = 40;
					var overrideLocation = false;
					var rect = htmlOrCanvasWin.element.parentNode.getBoundingClientRect();

					if (winPosX > rect.width - threshold) {
						winPosX = rect.width - threshold;
						overrideLocation = true;
					}
					if (winPosY > rect.height - threshold) {
						winPosY = rect.height - threshold;
						overrideLocation = true;
					}

					if (!api.cfg.mirrorMode && overrideLocation) {
						htmlOrCanvasWin.setLocation(winPosX, winPosY);
					}
				}

				$(htmlOrCanvasWin.element).css({"left": winPosX + 'px', "top": winPosY + 'px'});
			}
        }

        function adjustCanvasSize(canvas, width, height) {
            if (canvas.width != width * util.dpr || canvas.height != height * util.dpr) {
                var ctx = canvas.getContext("2d");
                var snapshot;
                if(canvas.width!==0 && canvas.height!=0){
                    snapshot=ctx.getImageData(0, 0, canvas.width, canvas.height);
                }
                canvas.width = width * util.dpr;
                canvas.height = height * util.dpr;
                canvas.style.width = width + 'px';
                canvas.style.height = height + 'px';
                if(snapshot!=null){
                    ctx.putImageData(snapshot, 0, 0);
                }
            }
        }

        function clear(x, y, w, h, context) {
            var dpr = util.dpr;
            context.clearRect(x * dpr, y * dpr, w * dpr, h * dpr);
        }

        function copy(sx, sy, dx, dy, w, h, context) {
            var dpr = util.dpr;
            var copy = context.getImageData(sx * dpr, sy * dpr, w * dpr, h * dpr);
            context.putImageData(copy, dx * dpr, dy * dpr);
        }

        function getCursorStyle(cursorMsg) {
            if (cursorMsg.b64img == null) {
                return cursorMsg.cursor;
            } else {
                if (api.cfg.ieVersion) {
                    return 'url(\'' + api.cfg.connectionUrl + 'file?id=' + cursorMsg.curFile + '\'), auto';
                } else {
                    var data = util.getImageString(cursorMsg.b64img);
                    return 'url(' + data + ') ' + cursorMsg.x + ' ' + cursorMsg.y + ' , auto';
                }
            }
        }

        function getHandShake(canvas) {
            var handshake = {
                clientId: api.cfg.clientId,
                browserId: api.getIdentity(),
                viewId: api.cfg.viewId,
                connectionId: api.getSocketId(),
                locale: api.getLocale(),
                timeZone: util.getTimeZone(),
                mirrored: api.cfg.mirrorMode,
                directDrawSupported: api.cfg.typedArraysSupported && !(api.cfg.ieVersion && api.cfg.ieVersion <= 10),
                url: window.location.href
            };

            if (!api.cfg.mirrorMode) {
            	var desktopSize = api.getDesktopSize();
                handshake.documentBase = api.cfg.documentBase;
                handshake.params = api.cfg.params;
                handshake.desktopWidth = desktopSize.width;
                handshake.desktopHeight = desktopSize.height;
            }
            return {
                handshake: handshake
            };
        }

        function getPixels(request) {
            var result = document.createElement("canvas");
            result.width = request.w;
            result.height = request.h;
            var resctx = result.getContext("2d");
            
            if (compositingWM) {
            	var wins = getWindows(true);
            	wins.sort((a,b) => (a.element.style.zIndex > b.element.style.zIndex) ? 1 : ((a.element.style.zIndex < b.element.style.zIndex) ? -1 : 0));
            	for (var i = 0; i < wins.length; i++) {
            		var win = wins[i];
            		if (!win.htmlWindow) {
            			var rect = win.element.getBoundingClientRect();
            			if ((rect.left > request.x + request.w) || (rect.top > request.y + request.h)
            					|| (request.x > rect.left + rect.width) || (request.y > rect.top + rect.height)) {
            				continue;
            			}
            			var left = Math.max(request.x, rect.left);
            			var top = Math.max(request.y, rect.top);
            			var width = Math.min(request.x + request.w - left, rect.left + rect.width - left);
            			var height = Math.min(request.y + request.h - top, rect.top + rect.height - top);
            			resctx.putImageData(win.element.getContext("2d").getImageData(left - rect.left, top - rect.top, width, height), left - request.x, top - request.y);
            		}
            	}
            } else {
            	resctx.putImageData(api.getCanvas().getContext("2d").getImageData(request.x, request.y, request.w, request.h), 0, 0);
            }
            
            var dataurl = result.toDataURL("image/png");
            return {
                pixelsResponse: {
                    correlationId: request.correlationId,
                    pixels: dataurl
                }
            };
        }
        
        function errorHandler(error) {
            drawingLock = null;
            throw error;
        }
        
        function CanvasWindow(id, element, name, title) {
        	this.id = id;
        	this.element = element;
        	this.name = name;
        	this.title = title;
        	this.htmlWindow = false;
        	this.state = 0;
        	this.webswingInstance = api.external;
        }
        
        CanvasWindow.prototype.isModalBlocked = function() {
        	return this.element.classList.contains('modal-blocked');
        };

        CanvasWindow.prototype.setBounds = function(x, y, width, height) {
        	api.send({window: {id: this.id, x: Math.floor(x), y: Math.floor(y), width: Math.floor(width), height: Math.floor(height)}});
    	};
    	
    	CanvasWindow.prototype.setLocation = function(x, y) {
    		var rect = this.element.getBoundingClientRect();
    		api.send({window: {id: this.id, x: Math.floor(x), y: Math.floor(y), width: Math.floor(rect.width), height: Math.floor(rect.height)}});
    	};
    	
    	CanvasWindow.prototype.setSize = function(width, height) {
    		var rect = this.element.getBoundingClientRect();
    		api.send({window: {id: this.id, x: Math.floor(rect.x + document.body.scrollLeft), y: Math.floor(rect.y + document.body.scrollTop), width: Math.floor(width), height: Math.floor(height)}});
    	};
    	
    	CanvasWindow.prototype.detach = function() {
    		if (!this.element.parentNode) {
    			console.error("Cannot detach window, it is not attached to any parent!");
    			return;
    		}
    		
    		return this.element.parentNode.removeChild(this.element);
    	};
    	
    	CanvasWindow.prototype.attach = function(parent, pos) {
    		if (this.element.parentNode) {
    			console.error("Cannot attach window, it is still attached to another parent!");
    			return;
    		}
    		
    		if (parent) {
    			parent.append(this.element);
    			if (pos) {
    				this.setLocation(pos.x, pos.y);
    			}
    		}
    	};

    	CanvasWindow.prototype.isRelocated = function() {
    		return this.element.parentNode != api.cfg.rootElement[0];
    	}
    	
    	CanvasWindow.prototype.close = function() {
    		api.send({window: {id: this.id, close: true}});
    		closedWindows[this.id] = true;
    	};

    	CanvasWindow.prototype.performAction = function(options) {
    		performAction($.extend({"windowId": this.id}, options));
    	}
    	
    	CanvasWindow.prototype.handleActionEvent = function(actionName, data, binaryData) {
        	// to be customized
        }
    	
    	CanvasWindow.prototype.windowClosing = function() {
    		// to be customized
    	}
    	
    	CanvasWindow.prototype.windowClosed = function() {
    		// to be customized
    	}
    	
    	function HtmlWindow(id, element, name) {
        	this.id = id;
        	this.element = element;
        	this.name = name;
        	this.htmlWindow = true;
        	this.webswingInstance = api.external;
        }
    	
    	HtmlWindow.prototype.isModalBlocked = function() {
        	return this.element.classList.contains('modal-blocked');
        };
        
    	HtmlWindow.prototype.performAction = function(options) {
    		performAction($.extend({"windowId": this.id}, options));
    	}

    	HtmlWindow.prototype.handleActionEvent = function(actionName, data, binaryData) {
        	// to be customized
        }
    	
    	HtmlWindow.prototype.windowClosing = function() {
    		// to be customized
    	}
    	
    	HtmlWindow.prototype.windowClosed = function() {
    		// to be customized
    	}

        function getWindows(canvasOnly) {
        	if (!compositingWM) {
        		// compositingWM only
        		return [];
        	}
        	
        	var wins = [];
        	for (var id in windowImageHolders) {
        		if (canvasOnly && windowImageHolders[id].htmlWindow) {
        			continue;
        		}
        		wins.push(windowImageHolders[id]);
        	}
        	
        	return wins;
        }
        
        function getWindowById(winId) {
        	if (!compositingWM) {
        		// compositingWM only
        		return;
        	}
        	
        	return windowImageHolders[winId];
        }
        
        
        function performAction(options) {
        	// options = {actionName, data, binaryData, windowId}
        	performActionInternal($.extend({"eventType": "user"}, options));
        }
        
        function performActionInternal(options) {
        	api.send({
                action: {
                	actionName: options.actionName,
                	eventType: options.eventType,
                	data: options.data || "",
                	binaryData: options.binaryData || null,
                	windowId: options.windowId || ""
                }
            });
        }
        
        function windowOpening(htmlOrCanvasWindow) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowOpening) {
        			api.cfg.compositingWindowsListener.windowOpening(htmlOrCanvasWindow);
        		}
            } catch (e) {
                console.error(e);
            }
        }
        
        function windowOpened(htmlOrCanvasWindow) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowOpened) {
        			api.cfg.compositingWindowsListener.windowOpened(htmlOrCanvasWindow);
        		}
            } catch (e) {
                console.error(e);
            }
        }
        
        function windowClosing(htmlOrCanvasWindow) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowClosing) {
        			api.cfg.compositingWindowsListener.windowClosing(htmlOrCanvasWindow);
        		}
            } catch (e) {
                console.error(e);
            }
        }
        
        function windowClosed(htmlOrCanvasWindow) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowClosed) {
        			api.cfg.compositingWindowsListener.windowClosed(htmlOrCanvasWindow);
        		}
            } catch (e) {
                console.error(e);
            }
        }
        
        function windowModalBlockedChanged(htmlOrCanvasWindow) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowModalBlockedChanged) {
        			api.cfg.compositingWindowsListener.windowModalBlockedChanged(htmlOrCanvasWindow);
        		}
            } catch (e) {
                console.error(e);
            }
        }
        
        function handleActionEvent(actionName, data, binaryData) {
        	// to be customized
        }
        
    }

