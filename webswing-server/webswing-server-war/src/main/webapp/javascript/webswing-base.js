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
            getDesktopSize: 'canvas.getDesktopSize',
            processComponentTree: 'canvas.processComponentTree',
            registerInput: 'input.register',
            sendInput: 'input.sendInput',
            registerUndockedCanvas: 'input.registerUndockedCanvas',
            registerTouch: 'touch.register',
            initCanvas: 'canvas.init',
            touchCursorChanged: 'touch.cursorChanged',
            touchBarConfig: 'touch.touchBarConfig',
            getUser: 'login.user',
            login: 'login.login',
            logout: 'login.logout',
            touchSession: 'login.touchSession',
            getIdentity: 'identity.get',
            disposeIdentity: 'identity.dispose',
            getLocale: 'translate.getLocale',
            translate: 'translate.translate',
            showDialog: 'dialog.show',
            showDialogBar: 'dialog.showBar',
            hideDialog: 'dialog.hide',
            hideDialogBar: 'dialog.hideBar',
            showOverlay: 'dialog.showOverlay',
            hideOverlay: 'dialog.hideOverlay',
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
            dockingVisibilityOverlay: 'dialog.content.dockingVisibilityOverlay',
            dockingModalityOverlay: 'dialog.content.dockingModalityOverlay',
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
            handleActionEvent: 'base.handleActionEvent',
            handleAccessible: 'accessible.handleAccessible',
            isAccessiblityEnabled: 'accessible.isEnabled',
            showWindowSwitcher: 'accessible.showWindowSwitcher',
            manageFocusEvent: 'focusManager.manageFocusEvent'
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
            handleActionEvent: handleActionEvent,
            hasUndockedWindows: hasUndockedWindows,
            getFocusedWindow: getFocusedWindow,
            getAllWindows: getAllWindows,
            getMainWindowVisibilityState: getMainWindowVisibilityState,
            closeWindow: closeWindow
        };
        module.ready = function () {
            directDraw = new WebswingDirectDraw({logTrace: api.cfg.traceLog, logDebug: api.cfg.debugLog, ieVersion: api.cfg.ieVersion, dpr: util.dpr});
        };

        var timer1, timer3, timerDockResize;
        var drawingLock;
        var drawingQ = [];
        var warningTimeout = null;
        var windowImageHolders = {}; // <id> : <CanvasWindow/HtmlWindow>
        var internalFrameWrapperHolders = {}; // <id> : <IFW element>
        var closedWindows = {}; // <id> : <boolean>, map of windows requested to be closed, rendering of windows in this map will be ignored, until message about closed window is received
        var directDraw;
        var compositingWM = false;
        var compositionBaseZIndex = 100;
        var audio = {};
        var focusedUndockedWindow = null;
        var dockResizeInterval = 250;
        var popupStartupResizeInterval = 1000;
        const JFRAME_MAXIMIZED_STATE = 6;

        function startApplication() {
            initialize(null, false);
        }

        function startMirrorView(clientId) {
            initialize(clientId, true)
        }

        function initialize(clientId, isMirror) {
        	api.initCanvas();
            api.registerInput();
            if (!isMirror) {
            	api.registerTouch(api.touchBarConfig);
            }
            window.addEventListener('beforeunload', beforeUnloadEventHandler);
            if (!isMirror) {
                window.addEventListener('hashchange', handshake);
            }
            document.addEventListener("visibilitychange", visibilityChangeHandler);
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
        	if (compositingWM) {
    			for (var winId in windowImageHolders) {
    				var win = windowImageHolders[winId];
    				
    				try {
    					if (win.dockOwner) {
    						// close any undocked popup windows
    						win.preventDockClose = true;
    						win.dockOwner.close();
    					} else {
    						$(win.element).remove();
    					}
        			} catch (err) {
        				console.error(err);
        			}
    			}
        	}
        	$(".internal-frames-wrapper").remove();
        	
            api.cfg.clientId = '';
            api.cfg.viewId = util.GUID();
            api.cfg.hasControl = false;
            api.cfg.mirrorMode = false;
            api.cfg.canPaint = false;
            api.cfg.browserId = api.getIdentity();
            clearInterval(timer1);
            clearInterval(timer3);
            clearInterval(timerDockResize);
            timer1 = setInterval(api.sendInput, 100);
            timer3 = setInterval(servletHeartbeat, 100000);
            timerDockResize = setInterval(dockResize, dockResizeInterval);
            compositingWM = false;
            windowImageHolders = {};
            internalFrameWrapperHolders = {};
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
            api.sendInput(getHandShake());
        }

        function dispose() {
            clearInterval(timer1);
            clearInterval(timerDockResize);
            unload();
            api.sendInput();
            resetState();
            api.closeFileDialog();
            api.hideDialogBar();
            window.removeEventListener('beforeunload', beforeUnloadEventHandler);
            document.removeEventListener("visibilitychange", visibilityChangeHandler);
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
            if (data.windowSwitchList) {
            	api.showWindowSwitcher($(getFocusedWindow().document).find(".webswing-element-content"), data.windowSwitchList);
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
                api.manageFocusEvent(data.focusEvent);
            }
            if (data.accessible != null) {
            	api.handleAccessible(data.accessible, $(getFocusedWindow().document).find(".webswing-element-content"));
            }
            if (data.cursorChange != null && api.cfg.hasControl && !api.cfg.recordingPlayback) {
                var element = compositingWM ? findCanvasWindowsById(data.cursorChange.winId) : $("canvas.webswing-canvas");
                element.each(function(i, canvas) {
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
            	closeWindowInternal(data.closedWindow.id);
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
            if (data.audioEvent != null) {
            	switch (data.audioEvent.eventType) {
	            	case "play": {
	            		var clip = audio[data.audioEvent.id];
	            		
	            		if (!clip) {
	            			// new clip
	            			var blob = new Blob([data.audioEvent.data], { type: 'audio/mpeg' });
	            			var url = window.URL.createObjectURL(blob);
	            			
	            			clip = new Audio();
	            			clip.src = url;
	            			clip.id = data.audioEvent.id;
	            			clip.loopCount = data.audioEvent.loop || 0;
	            			if (data.audioEvent.time != null && parseFloat(data.audioEvent.time) > 0) {
	            				clip.currentTime = parseFloat(data.audioEvent.time);
	            			}
	            			clip.addEventListener('ended', audioEndedHandler);
	            			
	            			audio[data.audioEvent.id] = clip;
	            			
	            			clip.play();
	            		} else {
	            			// existing clip
	            			// if a clip is running we ignore another play event
	            			if (clip.paused) {
	            				clip.loopCount = data.audioEvent.loop || 0;
	            				if (data.audioEvent.time != null && parseFloat(data.audioEvent.time) > 0) {
	            					clip.currentTime = parseFloat(data.audioEvent.time);
	            				}
	            				
	            				clip.play();
	            			}
	            		}
	        		    
	            		break;
	            	}
	            	case "stop": {
	            		var clip = audio[data.audioEvent.id];
	            		
	            		if (clip) {
	            			clip.pause();
	            		}
	            		break;
	            	}
	            	case "update": {
	            		var clip = audio[data.audioEvent.id];
	            		
	            		if (clip) {
	            			if (data.audioEvent.time != null && parseFloat(data.audioEvent.time) > 0) {
	            				clip.currentTime = parseFloat(data.audioEvent.time);
	            			}
	            		}
	            		break;
	            	}
	            	case "dispose": {
	            		var clip = audio[data.audioEvent.id];
	            		
	            		if (clip && clip.paused) {
	            			delete audio[data.audioEvent.id];
	            		}
	            		break;
	            	}
            	}
            }
            
            if (windowsData != null) {
            	var winMap = {};
            	
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
            		} else if (win.internalWindows) {
            			for (var j=0; j<win.internalWindows.length; j++) {
    						winMap[win.internalWindows[j].id] = true;
            			}
            		}
            		winMap[win.id] = true;
            	}
            	
            	if (compositingWM) {
            		// with CWM we always get all the windows, so if an already open window is missing in windowsData we should close it
            		for (var winId in windowImageHolders) {
            			var win = windowImageHolders[winId];
            			
            			if (!winMap[winId] && win.element && $(win.element).is(":not(.close-prevented)")) {
            				console.log("closing obsolete window " + winId);
            				closeWindowInternal(winId);
            			}
            		}
            	}
            	
            	// regular windows (background removed)
            	windowsData.reduce(function (sequence, window, index) {
            		return sequence.then(function () {
            			return renderWindow(window, compositingWM ? windowsData.length - index - 1 : index, data.directDraw);
            		}, errorHandler);
            	}, Promise.resolve()).then(function () {
            		if (compositingWM) {
            			// dispose of empty internal-frames-wrappers
            			for (var id in internalFrameWrapperHolders) {
            				var ifw = internalFrameWrapperHolders[id];
            				if ($(ifw).is(":empty")) {
            					$(ifw).remove();
            				}
            			}
            		}
            		
            		ack(data);
            		processNextQueuedFrame();
            	}, errorHandler);
            	
            	checkCanvasAccessibleInfo();
            } else {
                processNextQueuedFrame();
            }
        }
        
        function dockResize() {
        	var popups = [];
        	var now = new Date().getTime();
			for (var winId in windowImageHolders) {
				// go through each undocked popup only once
				var popup = windowImageHolders[winId].dockOwner;
				if (popup && popup != null && popups.indexOf(popup) === -1 && (now - popup.createTime > popupStartupResizeInterval) && (now - popup.ignoreDockResizeTimestamp > dockResizeInterval)) {
					popups[popup] = true;
	            	var maxWidth = 0;
	            	var maxHeight = 0;
	            	$(popup.document).find("canvas.webswing-canvas:not(.internal)").each(function() {
	            		var rect = $(this)[0].getBoundingClientRect();
	            		if (rect.left + rect.width > maxWidth) {
	            			maxWidth = rect.left + rect.width;
	            		}
	            		if (rect.top + rect.height > maxHeight) {
	            			maxHeight = rect.top + rect.height;
	            		}
	            	});
	            	var deltaX = maxWidth - popup.innerWidth;
	            	var deltaY = maxHeight - popup.innerHeight;
	            	if (deltaX != 0 || deltaY != 0) {
	            		popup.resizeBy(deltaX, deltaY);
	            	}
	            }
			}
        }
        
        function checkCanvasAccessibleInfo() {
        	var enabled = api.isAccessiblityEnabled();
        	var canvasSelector = enabled ? ".webswing-element-content canvas.accessibility-off" : ".webswing-element-content canvas:not(.accessibility-off)";
        	var rootSelector = enabled ? ".webswing-element-content.accessibility-off" : ".webswing-element-content:not(.accessibility-off)";
        	
        	jQuery(rootSelector).each(function() {
        		setRootElementAccessibleInfo(jQuery(this), enabled);
        	});
        	jQuery(canvasSelector).each(function() {
        		setCanvasAccessibleInfo(jQuery(this), enabled);
        	});
        	for (var winId in windowImageHolders) {
    			var dockOwner = windowImageHolders[winId].dockOwner;
        		if (dockOwner != null && dockOwner.document) {
        			$(dockOwner.document).find(rootSelector).each(function() {
        				setRootElementAccessibleInfo(jQuery(this), enabled);
        			});
    				$(dockOwner.document).find(canvasSelector).each(function() {
    					setCanvasAccessibleInfo(jQuery(this), enabled);
    				});
    			}
        	}
        }
        
        function setRootElementAccessibleInfo(rootElement, enabled) {
        	if (enabled) {
        		rootElement.removeAttr("aria-label").removeClass("accessibility-off");
        	} else {
        		rootElement.attr("aria-label", api.translate("accessibility.turnOn")).addClass("accessibility-off");
        	}
        }
        
        function setCanvasAccessibleInfo(canvas, enabled) {
        	if (enabled) {
        		canvas.removeAttr("aria-label").removeAttr("role").removeClass("accessibility-off");
        	} else {
        		canvas.attr("role", "img").attr("aria-label", api.translate("accessibility.turnOn")).addClass("accessibility-off");
        	}
        }
        
        function audioEndedHandler(e) {
        	var clip = e.srcElement;
        	
        	if (!clip) {
        		return;
        	}
        	
        	if (!clip.loopCount || clip.loopCount == 1) {
        		clip.loopCount = 0;
        		clip.currentTime = 0;
        		clip.pause();
        		api.send({audio: {id: clip.id, stop: true}});
        		return;
        	}
        	
        	clip.loopCount = clip.loopCount - 1;
        	clip.play();
        }

        function closeWindow(id) {
        	api.send({window: {id: id, eventType: 'close'}});
    		closedWindows[id] = true;
        }
        
        function closeWindowInternal(id) {
        	var canvasWindow = windowImageHolders[id];
        	
        	if (canvasWindow) {
        		var winCloseEvent = new WindowCloseEvent(canvasWindow.id);
        		
        		if (compositingWM) {
        			windowClosing(canvasWindow, winCloseEvent);
        			try {
        				canvasWindow.windowClosing(winCloseEvent);
                    } catch (e) {
                        console.error(e);
                    }
                    
                    if (canvasWindow.htmlWindow && winCloseEvent.isDefaultPrevented()) {
            			$(canvasWindow.element).addClass("close-prevented");
            			
            			if (canvasWindow.element.ownerDocument.defaultView != window) {
            				// dock undocked close-prevented HtmlWindows to main window
            				api.cfg.rootElement.append($(canvasWindow.element).detach());
            			}
            		} else {
            			if (canvasWindow.dockState == 'undocked' && canvasWindow.dockOwner) {
            				canvasWindow.preventDockClose = true;
            				canvasWindow.dockOwner.close();
            			}
            			$(canvasWindow.element).remove();
            			delete windowImageHolders[id];
            			checkUndockedModalBlocked(); // this must be called after windowImageHolders is deleted
            		}
                    
                    windowClosed(canvasWindow);
                    try {
                    	canvasWindow.windowClosed();
                    } catch (e) {
                    	console.error(e);
                    }
        		} else {
        			$(canvasWindow.element).remove();
        			delete windowImageHolders[id];
        		}
        	}
        	
        	delete closedWindows[id];
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
        	if ((win.type == 'basic' || win.type == 'html') && windowImageHolders[win.id] == null) {
        		var canvas;
        		
        		if (win.type == 'basic') {
        			canvas = document.createElement("canvas");
        			canvas.classList.add("webswing-canvas");
        		}
        		
				openNewComposedWindow(win, canvas);
			}
			
			var htmlOrCanvasWin = windowImageHolders[win.id];
			
			handleWindowDockState(win.dockState, htmlOrCanvasWin);
			handleWindowModalityAndBounds(win, htmlOrCanvasWin, index, true);
			handleWindowState(win.state, htmlOrCanvasWin);
			drawInternalWindows(win.internalWindows);
        	
			if (win.type == 'html') {
				return Promise.resolve();
			} else if (win.type == 'basic') {
				var canvas = windowImageHolders[win.id].element;
				return renderPngDrawWindowInternal(win, canvas.getContext("2d"));
			}
        }
        
        function renderPngDrawWindowInternal(win, context) {
        	if (!win.content) {
        		drawInternalWindows(win.internalWindows);
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
								
								imageObj.onload = null;
								imageObj.src = '';
								if (imageObj.clearAttributes != null) {
									imageObj.clearAttributes();
								}
								imageObj = null;
								
								drawInternalWindows(win.internalWindows);
								
								resolved();
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
			        windowImageHolders[win.id] = new CanvasWindow(win.id, win.ownerId, getOwnerParents(win.ownerId), canvas, false, win.name, win.title, win.classType);
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
        		
        		if (win.type != 'basic') {
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
        			// new canvas window or html window is opening
        			if ((canvas != null || win.type == 'html') && windowImageHolders[win.id] == null) {
        				openNewComposedWindow(win, canvas);
        			}
        			
        			var htmlOrCanvasWin = windowImageHolders[win.id];
        			
        			handleWindowDockState(win.dockState, htmlOrCanvasWin);
        			handleWindowModalityAndBounds(win, htmlOrCanvasWin, index, false);
    				handleWindowState(win.state, htmlOrCanvasWin);
    				drawInternalWindows(win.internalWindows);
        			
        			resolved();
        		}, function (error) {
        			rejected(error);
        		});
        	});
        }
        
        function openNewComposedWindow(win, canvas) {
        	if (win.type == 'html') {
				var htmlDiv = document.createElement("div");
				htmlDiv.classList.add("webswing-html-canvas");
				windowImageHolders[win.id] = new HtmlWindow(win.id, win.ownerId, getOwnerParents(win.ownerId), htmlDiv, win.name);
			} else {
				windowImageHolders[win.id] = new CanvasWindow(win.id, win.ownerId, getOwnerParents(win.ownerId), canvas, false, win.name, win.title, win.classType);
				windowImageHolders[win.id].dockMode = win.dockMode;
				windowImageHolders[win.id].dockState = win.dockState;
			}
			
			var element = windowImageHolders[win.id].element;
			var $element = $(element);
			
			$element.attr('data-id', win.id).css("position", "absolute");
			if (win.ownerId) {
				$element.attr('data-ownerid', win.ownerId);
			}
			
			windowOpening(windowImageHolders[win.id]);
			
			if (windowImageHolders[win.id].dockMode == 'autoUndock' || windowImageHolders[win.id].dockState == 'undocked') {
				undockWindow(win.id, win.ownerId, win.title, windowImageHolders[win.id].element, win.posX, win.posY, win.width, win.height, function() {
					windowOpened(windowImageHolders[win.id]);
				});
        	} else if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
				windowImageHolders[win.ownerId].element.parentNode.append(element);
				windowOpened(windowImageHolders[win.id]);
			} else {
				api.cfg.rootElement.append(element);
				windowOpened(windowImageHolders[win.id]);
			}
			
			if (!api.cfg.mirrorMode) {
				performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
			}
        }
        
        function undockWindow(id, ownerId, title, element, posX, posY, width, height, callback) {
        	var ownerWin;
        	if (element.parentNode) {
        		ownerWin = element.parentNode.ownerDocument.defaultView;
        	} else {
        		ownerWin = window;
        	}
        	var pagePos = ownerWin.pagePosition;
        	if (!pagePos) {
        		pagePos = {x: 0, y: 0};
        	}

			var baseUrl = window.location.origin + window.location.pathname;
			baseUrl = baseUrl.lastIndexOf('/') != baseUrl.length - 1 ? baseUrl + "/" : baseUrl;
        	
			var popup = window.open("", id, "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes," +
					"width=" + width + "," +
					"height=" + height + "," +
					"top=" + (posY + pagePos.y) + "," +
					"left=" + (posX + pagePos.x));

			windowImageHolders[id].dockOwner = popup;
			windowImageHolders[id].preventDockClose = false;
			
			popup.createTime = new Date().getTime();
			popup.ignoreDockResizeTimestamp = new Date().getTime();
			
			popup.document.title = title || "";
			popup.document.body.style.margin = "0";
			popup.document.body.style.width = "100%";
			popup.document.body.style.height = "100%";
			
			var body = popup.document.body;
			var wrapper = popup.document.createElement("div");
			wrapper.classList.add("webswing-element");
			
			var content = popup.document.createElement("div");
			content.classList.add("webswing-element-content");
			content.classList.add("composition");
			content.setAttribute("role", "application");
			
			wrapper.appendChild(content);
			body.appendChild(wrapper);
			
			$(popup.document.head).append("<link rel='stylesheet' href='" + baseUrl + "css/style.css' type='text/css'>");
			
			var fonts = document.querySelectorAll("style[data-dd-ctx]");
			for (var i=0; i<fonts.length; i++) {
				var font = fonts[i].cloneNode();
				font.innerHTML = fonts[i].innerHTML;
				body.appendChild(font);
			}
			
			setTimeout(function() {
				// timeout to avoid race condition, the popup needs to be ready
				var winHeightOffset = popup.outerHeight - popup.innerHeight - 9;
				var winWidthOffset = popup.outerWidth - popup.innerWidth - 8;
				popup.moveBy(0 - winWidthOffset, 0 - winHeightOffset);
				
				var origRect = element.getBoundingClientRect();
				
	        	for (var winId in windowImageHolders) {
	    			var canvasWin = windowImageHolders[winId];
	    			if (winId == id || (canvasWin.isInParentHierarchy(id) && canvasWin.dockState != 'undocked')) {
	    				// undock the canvas window and all of its child windows (with html windows)
	    				var el = canvasWin.element;
	    				var elRect = el.getBoundingClientRect();
	    				var location = {x: elRect.left - origRect.left, y: elRect.top - origRect.top};
	    				
	    				if (el.parentNode) {
	    					el.parentNode.removeChild(el);
	    				}
	    				
	    				content.appendChild(el);
	    				
	    				$(el).css({"left": location.x + 'px', "top": location.y + 'px'});
	    				if (!canvasWin.htmlWindow) {
	    					canvasWin.setLocation(location.x, location.y);
	    					canvasWin.ignoreNextPositioning = true;
	    				}
	    				
	    				// undock also any internal frames wrapper divs that this window owns
	    				for (var ifwId in internalFrameWrapperHolders) {
            				var ifw = internalFrameWrapperHolders[ifwId];
            				if ($(ifw).data("ownerid") == winId) {
            					var intEl = ifw;
            					var intElRect = intEl.getBoundingClientRect();
            					var intLocation = {x: intElRect.left - elRect.left, y: intElRect.top - elRect.top};
            					
            					if (intEl.parentNode) {
            						intEl.parentNode.removeChild(intEl);
            					}
            					
            					content.appendChild(intEl);
            					
            					$(intEl).css({"left": intLocation.x + 'px', "top": intLocation.y + 'px'});
            				}
            			}
	    			}
	        	}
	        	
	        	api.registerUndockedCanvas(popup);
	        	
	        	checkUndockedModalBlocked();
				
				repaint();
				
				if (callback) {
    				(callback)(true);
    			}
			}, 100);
			
			popup.onbeforeunload = function() {
		        var winE = windowImageHolders[id];
		        if (winE) {
		        	if (winE.isModalBlocked()) {
		        		// if winE is modal blocked, then dock this window instead of closing, otherwise it will not close in swing, hang in javascript closed state and not render
		        		winE.dock();
		        	} else if (!winE.preventDockClose) {
		        		winE.close();
		        	}
		        }
		        focusedUndockedWindow = null;
		    };
		    popup.addEventListener("blur", function() {
		    	focusedUndockedWindow = null;
		    });
		    popup.addEventListener("focus", function() {
		    	// try focus canvas window with highest z-index contained inside this popup
		    	var maxZIndex = 0;
		    	var topCanvasId = id;
		    	$("canvas.webswing-canvas", $(popup.document)).each(function() {
		    		var zIndex = parseInt($(this).css("z-index"));
		    		if (zIndex > maxZIndex) {
		    			maxZIndex = zIndex;
		    			topCanvasId = $(this).data("id");
		    		}
		    	});
		    	if (windowImageHolders[topCanvasId]) {
		    		windowImageHolders[topCanvasId].requestFocus();
		    	}
		    	focusedUndockedWindow = popup;
		    });
		    popup.addEventListener("resize", function() {
		    	popup.ignoreDockResizeTimestamp = new Date().getTime();
		    	if (popup.resizeTimer != null) {
		    		clearTimeout(popup.resizeTimer);
		    	}
		    	popup.resizeTimer = setTimeout(function() {
		    		popup.resizeTimer = null;
		    		
		    		// auto resize canvas to parent window
		    		var winE = windowImageHolders[id];
		    		var rectP = winE.element.parentNode.getBoundingClientRect();
					var rectC = winE.element.getBoundingClientRect();
					if (rectC.width != rectP.width || rectC.height != rectP.height) {
						winE.setBounds(0, 0, rectP.width, rectP.height);
					}
		    	}, 100);
		    });
        }
        
        function dockWindow(canvasWindow) {
        	var ownerId = canvasWindow.ownerId;
            var parentWin = api.cfg.rootElement[0].ownerDocument.defaultView;
        	if (ownerId && windowImageHolders[ownerId] != null && windowImageHolders[ownerId].isRelocated()) {
         		var parentElement = windowImageHolders[ownerId].element.parentNode;
         		if (parentElement) {
         			parentWin = parentElement.ownerDocument.defaultView;
         		}
         	}
        	
        	var popup = canvasWindow.dockOwner;
        	var canvasWindowParent = canvasWindow.element.parentNode; // leave this reference here because canvasWindow.element.parentNode changes after it is docked
        	
        	for (var winId in windowImageHolders) {
    			var canvasWin = windowImageHolders[winId];
    			if (canvasWin.element.parentNode == canvasWindowParent) {
    				var element = canvasWin.element;
    	        	
    	        	var left = 0;
    	        	var top = 0;
    	        	var rect = element.getBoundingClientRect();
    	        	
    	        	element.parentNode.removeChild(element);
    	        	
    	        	if (ownerId && windowImageHolders[ownerId] != null && windowImageHolders[ownerId].isRelocated()) {
    	        		// dock back to window's parent if it is relocated (could be detached or undocked in another window)
    	        		left = popup.screenX - parentWin.screenX + rect.left;
    	        		top = popup.screenY - parentWin.screenY + rect.top;
    	        		
    	        		$(canvasWin.element).css({"left": left + 'px', "top": top + 'px'});
    					windowImageHolders[ownerId].element.parentNode.append(element);
    				} else {
    					// dock back to root parent (the base canvas location)
    					var pagePos = popup.pagePosition;
    					if (!pagePos) {
    						pagePos = {x: 0, y: 0};
    					}
    					var parentPagePos = window.pagePosition;
    					if (!parentPagePos) {
    						parentPagePos = {x: 0, y: 0};
    					}
    					
    					var origPos = {x: pagePos.x != 0 ? pagePos.x : popup.screenX, y: pagePos.y != 0 ? pagePos.y : popup.screenY};
    					var rootRect = api.cfg.rootElement[0].getBoundingClientRect();
    					left = origPos.x + rect.left - parentPagePos.x - rootRect.left;
    					top = origPos.y + rect.top - parentPagePos.y - rootRect.top;
    					
    					$(canvasWin.element).css({"left": left + 'px', "top": top + 'px'});
    					api.cfg.rootElement.append(element);
    				}
    	        	
    	        	if (!canvasWin.htmlWindow) {
    	        		canvasWin.setLocation(left, top);
    	        	}
    	        	
    	        	// dock also any internal frames wrapper divs that this window owns
    				for (var ifwId in internalFrameWrapperHolders) {
        				var ifw = internalFrameWrapperHolders[ifwId];
        				if ($(ifw).data("ownerid") == winId) {
        					var left = 0;
            	        	var top = 0;
            	        	var rect = ifw.getBoundingClientRect();
        					
        					if (ownerId && windowImageHolders[ownerId] != null && windowImageHolders[ownerId].isRelocated()) {
            	        		// dock back to window's parent if it is relocated (could be detached or undocked in another window)
            	        		left = popup.screenX - parentWin.screenX + rect.left;
            	        		top = popup.screenY - parentWin.screenY + rect.top;
            	        		
            					windowImageHolders[ownerId].element.parentNode.append(ifw);
            				} else {
            					// dock back to root parent (the base canvas location)
            					var pagePos = popup.pagePosition;
            					if (!pagePos) {
            						pagePos = {x: 0, y: 0};
            					}
            					var parentPagePos = window.pagePosition;
            					if (!parentPagePos) {
            						parentPagePos = {x: 0, y: 0};
            					}
            					
            					var origPos = {x: pagePos.x != 0 ? pagePos.x : popup.screenX, y: pagePos.y != 0 ? pagePos.y : popup.screenY};
            					var rootRect = api.cfg.rootElement[0].getBoundingClientRect();
            					left = origPos.x + rect.left - parentPagePos.x - rootRect.left;
            					top = origPos.y + rect.top - parentPagePos.y - rootRect.top;
            					
            					api.cfg.rootElement.append(ifw);
            				}
        					
        					$(ifw).css({"left": left + 'px', "top": top + 'px'});
        				}
        			}
    			}
    		}
        	
        	canvasWindow.dockOwner = null;
        	
        	canvasWindow.preventDockClose = true;
        	focusedUndockedWindow = null;
        	popup.close();
        	
        	checkUndockedModalBlocked();
        }
        
        function visibilityChangeHandler(evt) {
        	if (!hasUndockedWindows()) {
        		return;
        	}
        	
        	var hidden = document.visibilityState == "hidden";
        	for (var winId in windowImageHolders) {
        		var popup = windowImageHolders[winId].dockOwner;
    			if (popup != null) {
    				if (hidden) {
    					api.showOverlay(popup, api.dockingVisibilityOverlay);
    				} else {
    					api.hideOverlay(popup, api.dockingVisibilityOverlay);
    				}
    			}
        	}
        }
        
        function checkUndockedModalBlocked() {
        	var anyUndocked = false;
        	var blockedWindows = [];
        	var modalWindow = null;
        	
        	for (var winId in windowImageHolders) {
        		var popup = windowImageHolders[winId].dockOwner;
    			if (popup != null) {
    				anyUndocked = true;
    				
    				if ($(popup.document).find(".webswing-canvas:not(.modal-blocked)").length == 0) {
    					api.showOverlay(popup, api.dockingModalityOverlay);
    	        		blockedWindows.push($(popup.document).find(".modality-overlay"));
    	            } else {
    	            	api.hideOverlay(popup, api.dockingModalityOverlay);
    	            	modalWindow = popup;
    	            }
    			}
        	}
        	
        	if (!anyUndocked) {
        		api.hideOverlay(window, api.dockingModalityOverlay);
        		return;
        	}
        	
        	if (api.cfg.rootElement.find(".webswing-canvas:not([data-id=canvas])").length > 0 
        			&& api.cfg.rootElement.find(".webswing-canvas:not(.modal-blocked):not([data-id=canvas])").length == 0) {
        		api.showOverlay(window, api.dockingModalityOverlay);
        		blockedWindows.push($(document).find(".modality-overlay"));
    		} else {
    			api.hideOverlay(window, api.dockingModalityOverlay);
    		}
        	
        	if (blockedWindows.length > 0 && modalWindow != null) {
        		for (var i = 0; i < blockedWindows.length; i++) {
        			blockedWindows[i].find("button").on("click", function() {
        				// this focuses a window, do not use focusManager here
        				modalWindow.focus();
        			});
        		}
        	}
        }
        
        function hasUndockedWindows() {
        	for (var winId in windowImageHolders) {
    			if (windowImageHolders[winId].dockOwner != null) {
    				return true;
    			}
        	}
        	return false;
        }
        
        function getFocusedWindow() {
        	if (focusedUndockedWindow != null) {
        		return focusedUndockedWindow;
        	}
        	return window;
        }
        
        function getAllWindows() {
        	var wins = [];
        	
        	for (var winId in windowImageHolders) {
    			if (windowImageHolders[winId].dockOwner != null) {
    				wins.push(windowImageHolders[winId].dockOwner);
    			}
        	}
        	
        	wins.push(window);
        	
        	return wins;
        }
        
        function getMainWindowVisibilityState() {
        	return document.visibilityState;
        }
        
        function handleWindowModalityAndBounds(win, htmlOrCanvasWin, index, pngDraw) {
        	var $element = $(htmlOrCanvasWin.element);
        	
			if (htmlOrCanvasWin.htmlWindow && $element.is(".close-prevented")) {
				// call window listeners for close-prevented HtmlWindow
				windowOpening(htmlOrCanvasWin);
				$element.show().removeClass("close-prevented");
				windowOpened(htmlOrCanvasWin);
				if (!api.cfg.mirrorMode) {
					performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
				}
			}
        	
        	if (pngDraw && win.type == 'basic') {
				if (Math.abs($element.width() - win.width) >= 1 || Math.abs($element.height() - win.height) >= 1) {
					$element.css({"width": win.width + 'px', "height": win.height + 'px'});
					$element.attr("width", win.width).attr("height", win.height);
				}
        	}
        	
			$element.css({"z-index": (compositionBaseZIndex + index + 1), "width": win.width + 'px', "height": win.height + 'px'}).show();
			if ($element.is(".modal-blocked") != win.modalBlocked) {
				$element.toggleClass("modal-blocked", win.modalBlocked);
				windowModalBlockedChanged(htmlOrCanvasWin);
			}

            if (isVisible(htmlOrCanvasWin.element.parentNode) && !htmlOrCanvasWin.ignoreNextPositioning) {
            	$element.css({"left": win.posX + 'px', "top": win.posY + 'px'});
            	htmlOrCanvasWin.validatePositionAndSize(win.posX, win.posY);
            }
            
            htmlOrCanvasWin.ignoreNextPositioning = false;
        }
        
        function handleWindowState(state, htmlOrCanvasWin) {
        	if (htmlOrCanvasWin.htmlWindow || typeof state === 'undefined') {
        		return;
        	}
        	
        	htmlOrCanvasWin.state = state;
        	
        	if (state != JFRAME_MAXIMIZED_STATE || api.cfg.mirrorMode || !htmlOrCanvasWin.element.parentNode) {
        		return;
        	}
        	
			// window has been maximized, we need to set its bounds according to its parent node (could be detached)
			var rectP = htmlOrCanvasWin.element.parentNode.getBoundingClientRect();
			var rectC = htmlOrCanvasWin.element.getBoundingClientRect();
			if (rectC.width != rectP.width || rectC.height != rectP.height) {
				htmlOrCanvasWin.setBounds(0, 0, rectP.width, rectP.height);
			}
        }
        
        function handleWindowDockState(dockState, htmlOrCanvasWin) {
        	if (htmlOrCanvasWin.htmlWindow || typeof dockState === 'undefined') {
        		return;
        	}
        	
        	if (htmlOrCanvasWin.dockState === dockState) {
        		return;
        	}
        	
        	htmlOrCanvasWin.dockState = dockState;
        	if (dockState === 'undocked') {
        		var rect = htmlOrCanvasWin.element.getBoundingClientRect();
        		undockWindow(htmlOrCanvasWin.id, htmlOrCanvasWin.ownerId, htmlOrCanvasWin.title, htmlOrCanvasWin.element, rect.left, rect.top, rect.width, rect.height);
        	} else {
        		dockWindow(htmlOrCanvasWin);
        	}
        }
        
        function drawInternalWindows(internalWindows) {
        	if (!internalWindows || internalWindows.length == 0) {
        		return;
        	}
        	
			var intWins = [];
        	
			for (var i = internalWindows.length - 1; i >= 0; i--) {
				var intWin = internalWindows[i];
				
				if (intWin.type == 'internalWrapper') {
    				handleInternalWrapperWindow(intWin);
    			} else if (intWin.type == 'internal' || intWin.type == 'internalHtml') {
    				intWins.push(intWin);
    			}
			}
			
			var underlyingHtmlWindows = [];
			for (var i = 0; i < intWins.length; i++) {
				var intWin = intWins[i];
				
				if (intWin.type == 'internal') {
					handleInternalWindow(intWin, i, underlyingHtmlWindows);
				} else if (intWin.type == 'internalHtml') {
					handleInternalHtmlWindow(intWin, i);
					underlyingHtmlWindows.push(intWin);
				}
			}
        }
        
        function handleInternalWrapperWindow(win) {
        	var wrapper = internalFrameWrapperHolders[win.id];
			if (!wrapper || wrapper == null) {
				wrapper = $("<div class='internal-frames-wrapper' id='wrapper-" + win.id + "' />");
				if (win.ownerId && windowImageHolders[win.ownerId] != null && windowImageHolders[win.ownerId].isRelocated()) {
					windowImageHolders[win.ownerId].element.parentNode.append(wrapper[0]);
				} else {
					api.cfg.rootElement.append(wrapper);
				}
				internalFrameWrapperHolders[win.id] = wrapper[0];
			} else {
				wrapper = $(wrapper);
			}
			
			wrapper.attr("data-ownerid", win.ownerId);
			
			if (windowImageHolders[win.ownerId]) {
				var parent = $(windowImageHolders[win.ownerId].element);
				wrapper.css({
					"z-index": parent.css("z-index"),
					"left": win.posX + "px",
					"top": win.posY + "px",
					"width": win.width + "px",
					"height": win.height + "px"
				});
			}
        }
        
        function handleInternalWindow(win, index, underlyingHtmlWindows) {
        	var wrapper = internalFrameWrapperHolders[win.ownerId];
        	if (!wrapper || wrapper == null) {
        		// wait for the parent wrapper to be attached first and render this window in next cycle
        		return;
        	} else {
        		wrapper = $(wrapper);
        	}
        	
        	var canvas;
        	
        	if (windowImageHolders[win.id] == null) {
        		canvas = document.createElement("canvas");
				canvas.classList.add("webswing-canvas");
				canvas.classList.add("internal");
        		
				windowImageHolders[win.id] = new CanvasWindow(win.id, win.ownerId, getOwnerParents(win.ownerId), canvas, true, win.name, win.title, win.classType);
				
				$(canvas).attr('data-id', win.id).css("position", "absolute");
				if (win.ownerId) {
					$(canvas).attr('data-ownerid', win.ownerId);
				}

				wrapper.append(canvas);
			} else {
				canvas = windowImageHolders[win.id].element;
			}
        	
        	var parentPos = wrapper.position();
        	$(canvas).css({
        		"z-index": (compositionBaseZIndex + index + 1),
        		"left": (win.posX - parentPos.left) + "px",
        		"top": (win.posY - parentPos.top) + "px",
        		"width": win.width + "px",
        		"height": win.height + "px"
        	});
        	$(canvas).attr("width", win.width * util.dpr).attr("height", win.height * util.dpr);
        	
			if ($(canvas).is(".modal-blocked") != win.modalBlocked) {
				$(canvas).toggleClass("modal-blocked", win.modalBlocked);
			}
			
			if (underlyingHtmlWindows.length > 0) {
				var ownerCanvasId = wrapper.data("ownerid");
				if (ownerCanvasId && windowImageHolders[ownerCanvasId] && windowImageHolders[ownerCanvasId].element) {
					var src = windowImageHolders[ownerCanvasId].element;
					var ctx = canvas.getContext("2d");
					var pos = $(src).position();
					
					for (var i=0; i<underlyingHtmlWindows.length; i++) {
						var int = findWindowIntersection(win, underlyingHtmlWindows[i]);
						if (int && (int.x2 - int.x1 > 0) && (int.y2 - int.y1 > 0)) {
							var width = (int.x2 - int.x1) * util.dpr;
							var height = (int.y2 - int.y1) * util.dpr;
							ctx.drawImage(src, (int.x1 - pos.left) * util.dpr, (int.y1 - pos.top) * util.dpr, width, height, (win.posX < int.x1 ? (int.x1 - win.posX) : 0) * util.dpr, (win.posY < int.y1 ? (int.y1 - win.posY) : 0) * util.dpr, width, height);
						}
					}
				}
			}
        }
        
        function handleInternalHtmlWindow(win, index) {
        	var wrapper = internalFrameWrapperHolders[win.ownerId];
        	if (!wrapper || wrapper == null) {
        		// wait for the parent wrapper to be attached first and render this window in next cycle
        		return;
        	} else {
        		wrapper = $(wrapper);
        	}
        	
        	var htmlDiv;
        	var newWindowOpened = false;
        	
        	if (windowImageHolders[win.id] == null) {
        		htmlDiv = document.createElement("div");
				htmlDiv.classList.add("webswing-html-canvas");
				
				windowImageHolders[win.id] = new HtmlWindow(win.id, win.ownerId, getOwnerParents(win.ownerId), htmlDiv, win.name);
				newWindowOpened = true;
				$(htmlDiv).attr('data-id', win.id).css("position", "absolute");
				if (win.ownerId) {
					$(htmlDiv).attr('data-ownerid', win.ownerId);
				}

				windowOpening(windowImageHolders[win.id]);
				
        		wrapper.append(htmlDiv);
        	} else {
        		htmlDiv = windowImageHolders[win.id].element;
        		if ($(htmlDiv).is(".close-prevented")) {
        			newWindowOpened = true;
        			windowOpening(windowImageHolders[win.id]);
        			$(htmlDiv).removeClass("close-prevented").show();
        		}
        	}
        	
        	if (newWindowOpened) {
				windowOpened(windowImageHolders[win.id]);
				if (!api.cfg.mirrorMode) {
					performActionInternal({ actionName: "", eventType: "init", data: "", binaryData: null, windowId: win.id });
				}
			}
        	
        	var parentPos = wrapper.position();
        	$(htmlDiv).css({
        		"z-index": (compositionBaseZIndex + index + 1),
        		"left": (win.posX - parentPos.left) + "px",
        		"top": (win.posY - parentPos.top) + "px",
        		"width": win.width + "px",
        		"height": win.height + "px"
        	});
        	$(htmlDiv).attr("width", win.width).attr("height", win.height).show();
        	
        	if ($(htmlDiv).is(".modal-blocked") != win.modalBlocked) {
        		$(htmlDiv).toggleClass("modal-blocked", win.modalBlocked);
        	}
        }
        
        function findWindowIntersection(win1, win2) {
        	var r1 = {x1: win1.posX, y1: win1.posY, x2: win1.posX + win1.width, y2: win1.posY + win1.height};
        	var r2 = {x1: win2.posX, y1: win2.posY, x2: win2.posX + win2.width, y2: win2.posY + win2.height};
        	
        	[r1, r2] = [r1, r2].map(r => {
        		return {x: [r.x1, r.x2].sort(sortNumber), y: [r.y1, r.y2].sort(sortNumber)};
        	});

        	const noIntersect = r2.x[0] > r1.x[1] || r2.x[1] < r1.x[0] ||
        						r2.y[0] > r1.y[1] || r2.y[1] < r1.y[0];

        	return noIntersect ? false : {
        		x1: Math.max(r1.x[0], r2.x[0]), // _[0] is the lesser,
        		y1: Math.max(r1.y[0], r2.y[0]), // _[1] is the greater
        		x2: Math.min(r1.x[1], r2.x[1]),
        		y2: Math.min(r1.y[1], r2.y[1])
        	};
        }
        
        function sortNumber(a, b) {
        	return a - b;
        }
        
        function isVisible(element) {
        	if (!element || element == null) {
        		return false;
        	}
        	
        	return !!(element.offsetWidth || element.offsetHeight || element.getClientRects().length);
        }
        
        function findCanvasWindowsById(winId) {
        	var wins = $();
        	
        	for (var id in windowImageHolders) {
    			var canvasWin = windowImageHolders[id];
    			if (canvasWin.id == winId) {
    				wins = wins.add($(canvasWin.element));
    			}
        	}
        	
        	for (var id in internalFrameWrapperHolders) {
				var ifw = internalFrameWrapperHolders[id];
				if ($(ifw).data("ownerid") == winId) {
					wins = wins.add($(ifw));
				}
			}
        	
        	return wins;
        }
        
        function validateAndPositionWindow(htmlOrCanvasWin, winPosX, winPosY) {
			if (htmlOrCanvasWin.htmlWindow || api.cfg.mirrorMode) {
				return;
			}
			
			var threshold = 40;
			var overrideLocation = false;
			var rect = htmlOrCanvasWin.element.parentNode.getBoundingClientRect();
			var rectE = htmlOrCanvasWin.element.getBoundingClientRect();
			
			if (winPosX > rect.width - threshold) {
				winPosX = Math.max(0, rect.width - threshold);
				overrideLocation = true;
			}
			if (winPosY > rect.height - threshold) {
				winPosY = Math.max(0, rect.height - threshold);
				overrideLocation = true;
			}
			if (winPosX < ((rectE.width - threshold) * (-1))) {
				winPosX = ((rectE.width - threshold) * (-1));
				overrideLocation = true;
			}
			if (winPosY < 0) {
				winPosY = 0;
				overrideLocation = true;
			}
			
			if (overrideLocation) {
				$(htmlOrCanvasWin.element).css({"left": winPosX + 'px', "top": winPosY + 'px'});
				htmlOrCanvasWin.setLocation(winPosX, winPosY);
			}
        }
        
        function getOwnerParents(ownerId) {
        	var ownerWin = windowImageHolders[ownerId];
        	
        	if (!ownerWin || ownerWin == null) {
        		return [];
        	}
        	
        	if (!ownerId) {
        		return [];
        	}
        	
        	var parents = ownerWin.parents.slice();
        	parents.push(ownerId);
        	
        	return parents;
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

        function getHandShake() {
            if(window.name.substr(0,4)!=='tid_'){
                window.name='tid_'+util.GUID();
            }

            var handshake = {
                clientId: api.cfg.clientId,
                browserId: api.cfg.browserId,
                viewId: api.cfg.viewId,
                tabId: window.name,
                connectionId: api.getSocketId(),
                locale: api.getLocale(),
                timeZone: util.getTimeZone(),
                mirrored: api.cfg.mirrorMode,
                directDrawSupported: api.cfg.typedArraysSupported && !(api.cfg.ieVersion && api.cfg.ieVersion <= 10),
                dockingSupported: !api.cfg.ieVersion,
                touchMode: api.cfg.touchMode,
                accessiblityEnabled: api.isAccessiblityEnabled(),
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
        
        function CanvasWindow(id, ownerId, parents, element, internal, name, title, classType) {
        	this.id = id;
        	this.ownerId = ownerId;
        	this.parents = parents;
        	this.element = element;
        	this.name = name;
        	this.title = title;
        	this.classType = classType;
        	this.htmlWindow = false;
        	this.internal = internal;
        	this.state = 0;
        	this.dockMode = 'none';
        	this.dockState = 'docked';
        	this.dockOwner = null;
        	this.webswingInstance = api.external;
            this.validatePositionAndSize = function(x,y){
                validateAndPositionWindow(this,x,y);
            }
        }
        
        CanvasWindow.prototype.isInParentHierarchy = function(winId) {
    		if (!this.parents || this.parents.length == 0) {
    			return false;
    		}
    		for (var i=0; i<this.parents.length; i++) {
    			if (this.parents[i] == winId) {
    				return true;
    			}
    		}
    		return false;
    	};
        
        CanvasWindow.prototype.isModalBlocked = function() {
        	return this.element.classList.contains('modal-blocked');
        };
        
        CanvasWindow.prototype.getDockMode = function() {
        	return this.dockMode;
        };
        
        CanvasWindow.prototype.getDockState = function() {
        	return this.dockState;
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
    	
    	CanvasWindow.prototype.undock = function() {
    		if (this.dockMode == 'none' || this.dockState == 'undocked') {
    			if (callback) {
    				(callback)(false);
    			}
        		return;
        	}
    		
    		api.send({window: {id: this.id, eventType: 'undock'}});
    	};
    	
    	CanvasWindow.prototype.dock = function() {
    		if (this.dockMode == 'none' || this.dockState == 'docked' || this.dockOwner == null) {
    			return;
    		}
    		
    		api.send({window: {id: this.id, eventType: 'dock'}});
    	};
    	
    	CanvasWindow.prototype.toggleDock = function() {
    		if (this.dockState == 'docked') {
    			this.undock();
    		} else {
    			this.dock();
    		}
    	};
    	
    	CanvasWindow.prototype.maximize = function() {
    		api.send({window: {id: this.id, eventType: 'maximize'}});
    	};
    	
    	CanvasWindow.prototype.setUndecorated = function(undecorated) {
    		if (undecorated) {
    			api.send({window: {id: this.id, eventType: 'undecorate'}});
    		} else {
    			api.send({window: {id: this.id, eventType: 'decorate'}});
    		}
    	};
    	
    	CanvasWindow.prototype.isRelocated = function() {
    		return this.element.parentNode != api.cfg.rootElement[0];
    	}
    	
    	CanvasWindow.prototype.close = function() {
    		closeWindow(this.id);
    	};
    	
    	CanvasWindow.prototype.requestFocus = function() {
    		api.send({window: {id: this.id, eventType: 'focus'}});
    	};
    	
    	CanvasWindow.prototype.performAction = function(options) {
    		performAction($.extend({"windowId": this.id}, options));
    	}
    	
    	CanvasWindow.prototype.handleActionEvent = function(actionName, data, binaryData) {
        	// to be customized
        }
    	
    	CanvasWindow.prototype.windowClosing = function(windowCloseEvent) {
    		// to be customized
    	}
    	
    	CanvasWindow.prototype.windowClosed = function() {
    		// to be customized
    	}
    	
    	function HtmlWindow(id, ownerId, parents, element, name) {
        	this.id = id;
        	this.ownerId = ownerId;
        	this.parents = parents;
        	this.element = element;
        	this.name = name;
        	this.htmlWindow = true;
        	this.internal = false;
        	this.dockMode = 'none';
        	this.webswingInstance = api.external;
            this.validatePositionAndSize = function(x,y){
                validateAndPositionWindow(this,x,y);
            }
        }
    	
    	HtmlWindow.prototype.isInParentHierarchy = function(winId) {
    		if (!this.parents || this.parents.length == 0) {
    			return false;
    		}
    		for (var i=0; i<this.parents.length; i++) {
    			if (this.parents[i] == winId) {
    				return true;
    			}
    		}
    		return false;
    	};
    	
    	HtmlWindow.prototype.isModalBlocked = function() {
        	return this.element.classList.contains('modal-blocked');
        };
        
    	HtmlWindow.prototype.performAction = function(options) {
    		performAction($.extend({"windowId": this.id}, options));
    	}
    	
    	HtmlWindow.prototype.dispose = function() {
    		$(this.element).remove();
    		delete windowImageHolders[this.id];
    		repaint();
    	}
    	
    	HtmlWindow.prototype.handleActionEvent = function(actionName, data, binaryData) {
        	// to be customized
        }
    	
    	HtmlWindow.prototype.windowClosing = function(windowCloseEvent) {
    		// to be customized
    	}
    	
    	HtmlWindow.prototype.windowClosed = function() {
    		// to be customized
    	}
    	
    	function WindowCloseEvent(id) {
    		this.id = id;
    		this.defaultPrevented = false;
    	}
    	
    	WindowCloseEvent.prototype.preventDefault = function() {
    		this.defaultPrevented = true;
    	}
    	
    	WindowCloseEvent.prototype.isDefaultPrevented = function() {
    		return this.defaultPrevented;
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
        
        function windowClosing(htmlOrCanvasWindow, windowCloseEvent) {
        	try {
        		if (api.cfg.compositingWindowsListener && api.cfg.compositingWindowsListener.windowClosing) {
        			api.cfg.compositingWindowsListener.windowClosing(htmlOrCanvasWindow, windowCloseEvent);
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
            
            checkUndockedModalBlocked();
        }
        
        function handleActionEvent(actionName, data, binaryData) {
        	// to be customized
        }
        
    }

