import html from './templates/touch.html';

    export default function Touch(util) {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            send: 'socket.send',
            sendHandshake: 'base.handshake',
            getCanvas: 'canvas.get',
            getRootWidth: 'canvas.width',
            getRootHeight: 'canvas.height',
            displayPasteDialog: 'clipboard.displayPasteDialog',
            translate: 'translate.translate',
            repaint: 'base.repaint',
            showBar: 'dialog.showBar',
            touchSwitchModeMouseDialog: 'dialog.content.touchSwitchModeMouseDialog',
            touchSwitchModeTouchDialog: 'dialog.content.touchSwitchModeTouchDialog',
            hasUndockedWindows: 'base.hasUndockedWindows',
            focusInput: 'focusManager.focusInput',
            focusDefault: 'focusManager.focusDefault'
        };
        module.provides = {
            register: register,
            cursorChanged: cursorChanged,
            inputFocusGained: inputFocusGained,
            touchBarConfig: touchBarConfig(),
            switchMode: switchMode,
            dispose: dispose
        };

        var canvas;
        var input;
        var cursor;
        var touchBar;
        var registered = false;
        var tapStarted = 0;
        var dragStarted = 0;
        var dragging = false;
        var scaling = false;
        var lastScaleDist = 0;
        var touchStartY = 0;
        var touchStartX = 0;
        var inputEvtQueue = [];
        var latestMouseWheelEvent = null;
        var latestMouseMoveEvent = null;
        var sendInterval;
        var canvasResizeInterval;
        var scaleHideTimer = null;
        var toastTimer = null;
        var tapDelayThreshold = 750;
        var doubleTapDelayThreshold = 500;
        var lastTap = 0;
        var tapMoveThreshold = 10;
        var longPressTimeout = null;
        var longPressStartTimeout = null;
        var lastRepaint = 0;
        var repaintTimeout = null;
        var lastDragEffect = 0;
        var isKeyboardProbablyShowing = false;
        var focusCounter = 0;
        var scaleStartScrollLeft = 0;
    	var scaleStartScrollTop = 0;
    	var scaleStartPoints = [];
    	var scaleStartDim = {};
    	var maxScale = 3;
    	var scaleSpeed = 10;
    	var scrollSpeed = 1;
    	var compositionText = "";
    	var composing = false;
    	var backspaceKeyDown = false;
        var scrollMode = 'drag'; // drag, swipe
        var touchpadMode = false;
        var fullscreenButton = null;
        var scalingEnabled = true;
        var switchModeRequested = false;
        var switchModeDontAsk = false;
        var switchModeAlreadySwitched = false;
        var isProbablyTouchBook = false;
        var currentMode = "mouse"; // touch, mouse
        var currentConfig = null;
        var lastRootWidth = 0;
        var lastRootHeight = 0;

        function touchBarConfig() {
        	return {
        		buttons: {
        			defaultButtons: {
        				toggleMode: {
        					label: '<span class="ws-button-toggle-mode ws-icon-mouse-pointer" role="button"></span>',
        					title: '${touch.controlMode}',
        					enabled: true,
        					hidden: false,
        					action: function(button) {
        						buttonToggleMode(button);
        					}
            			},
            			switchToDesktop: {
            				label: '<span class="ws-button-switch-to-desktop ws-icon-desktop" role="button"></span>',
            				title: '${touch.desktopMode}',
            				enabled: true,
            				hidden: true,
            				action: function(button) {
            					buttonSwitchToDesktop(button);
            				}
            			},
            			copy: {
        					label: '<span class="ws-icon-docs" role="button"></span>',
        					title: '${touch.copy}',
        					enabled: true,
        					hidden: false,
        					action: function(button) {
        						buttonCopy(button);
        					}
            			},
            			cut: {
    	        			label: '<span class="ws-icon-scissors" role="button"></span>',
    	        			title: '${touch.cut}',
    	        			enabled: true,
    	        			hidden: false,
    	        			action: function(button) {
    	        				buttonCut(button);
    	        			}
    	        		},
    	        		paste: {
    	        			label: '<span class="ws-icon-paste" role="button"></span>',
    	        			title: '${touch.paste}',
    	        			enabled: true,
    	        			hidden: false,
    	        			action: function(button) {
    	        				buttonPaste(button);
    	        			}
    	        		},
    	        		keyboard: {
    	        			label: '<span class="ws-icon-keyboard" role="button"></span>',
    	        			title: '${touch.keyboard}',
    	        			enabled: true,
    	        			hidden: false,
    	        			action: function(button) {
    	        				buttonOpenKeyboard(button);
    	        			}
    	        		},
    	        		fullscreen: {
    	        			label: '<span class="ws-button-toggle-fullscreen ws-icon-resize-full" role="button"></span>',
    	        			title: '${touch.fullscreen}',
    	        			enabled: !util.isIOS(),
    	        			hidden: false,
    	        			action: function(button) {
    	        				buttonToggleFullscreen(button);
    	        			}
    	        		}
        			},
        			left: [],
        			center: [],
	        		right: []
        		},
        		scalingEnabled: true
        	};
        }
        
        function register(config) {
        	canvas = api.getCanvas();
        	
            doRegister(config);
            
            currentConfig = config;
            
            $(document).on("touchstart", function(event) {
            	if (isNotValidCanvasTarget(event.target) || api.hasUndockedWindows()) {
            		return;
            	}

            	if (!registered && !switchModeDontAsk && !switchModeRequested) {
            		// ask to switch to touch
            		switchModeRequested = true;
            		
                	api.showBar(api.touchSwitchModeTouchDialog);
                	
            		event.preventDefault();
            	}
            });
            
            document.addEventListener('mousemove', function(event) {
            	if (isNotValidCanvasTarget(event.target)) {
            		return;
            	}

            	if (registered && !switchModeDontAsk && !switchModeRequested && !isProbablyTouchBook) {
            		// ask to switch to mouse
            		switchModeRequested = true;
            		
            		api.showBar(api.touchSwitchModeMouseDialog);
            		
            		event.preventDefault();
            		event.stopPropagation();
            	}
            }, false);	
        }
        
        function doRegister(config, force) {
        	if (registered || (!util.isTouchDevice() && !force)) {
        		return;
        	}
        	
            input = $(document.querySelector(".ws-input-hidden"));
            
            document.addEventListener("touchstart", function(evt) {
            	if (isNotValidCanvasTarget(evt.target)) {
            		return;
            	}
            	handleStart(evt);
            }, {passive: false});
            document.addEventListener("touchend", function(evt) {
            	if (isNotValidCanvasTarget(evt.target)) {
            		return;
            	}
            	handleEnd(evt);
            }, {passive: false});
            document.addEventListener("touchcancel", function(evt) {
            	if (isNotValidCanvasTarget(evt.target)) {
            		return;
            	}
            	handleCancel(evt);
            }, {passive: false});
            document.addEventListener("touchleave", function(evt) {
            	if (isNotValidCanvasTarget(evt.target)) {
            		return;
            	}
            	handleEnd(evt);
            }, {passive: false});
            document.addEventListener("touchmove", function(evt) {
            	if (isNotValidCanvasTarget(evt.target)) {
            		if (evt.cancelable) {
                		evt.preventDefault();
                	}
            		return;
            	}
            	handleMove(evt);
            }, {passive: false});
            	
            api.cfg.rootElementWrapper.addClass("touch");
            
            input[0].addEventListener("focus", handleInputFocus);
        	input[0].addEventListener("blur", handleInputBlur);
            
            input[0].addEventListener('keyup', handleKeyup);
            input[0].addEventListener('keydown', handleKeydown);
            input[0].addEventListener('compositionstart', handleCompositionStart);
            input[0].addEventListener('compositionupdate', handleCompositionUpdate);
            input[0].addEventListener('compositionend', handleCompositionEnd);
            input[0].addEventListener('input', handleInputEvent);
        
        	document.addEventListener('webkitfullscreenchange', handleFullScreenChange);
        	document.addEventListener('mozfullscreenchange', handleFullScreenChange);
        	document.addEventListener('fullscreenchange', handleFullScreenChange);
        	document.addEventListener('MSFullscreenChange', handleFullScreenChange);
        
        	if (util.isIOS()) {
        		document.addEventListener('gesturestart', handleGestureStart);
            }
            
            initIntervals();
            
            api.cfg.rootElement[0].onselectstart = function(e) {
            	// prevent browser text selection
            	return false;
            };
            
            input.blur();
            api.focusDefault();
            
            console.log("initialized touch.");
            
            registered = true;
            api.cfg.touchMode = true;
            currentMode = "touch";
            touchpadMode = false;
            
            lastRootWidth = api.cfg.rootElement.width();
    		lastRootHeight = api.cfg.rootElement.height();

            api.sendHandshake();

            initTouchBar(config);

            var initScale = Math.min(document.body.offsetWidth / api.getRootWidth(), (document.body.offsetHeight - touchBar.height()) / api.getRootHeight());
            $(canvas).data("scale", initScale);
            $(canvas).data("minscale", initScale);
            doScaleCanvas(initScale);
        }
        
        function dispose() {
        	if (!registered) {
        		return;
        	}
        	
        	document.removeEventListener("touchstart", handleStart, {passive: false});
            document.removeEventListener("touchend", handleEnd, {passive: false});
            document.removeEventListener("touchcancel", handleCancel, {passive: false});
            document.removeEventListener("touchleave", handleEnd, {passive: false});
            document.removeEventListener("touchmove", handleMove, {passive: false});
        	
            api.cfg.rootElementWrapper.removeClass("touch");
        	
        	input[0].removeEventListener("focus", handleInputFocus);
        	input[0].removeEventListener("blur", handleInputBlur);
            
            input[0].removeEventListener('keyup', handleKeyup);
            input[0].removeEventListener('keydown', handleKeydown);
            input[0].removeEventListener('compositionstart', handleCompositionStart);
            input[0].removeEventListener('compositionupdate', handleCompositionUpdate);
            input[0].removeEventListener('compositionend', handleCompositionEnd);
            input[0].removeEventListener('input', handleInputEvent);
        
        	document.removeEventListener('webkitfullscreenchange', handleFullScreenChange);
        	document.removeEventListener('mozfullscreenchange', handleFullScreenChange);
        	document.removeEventListener('fullscreenchange', handleFullScreenChange);
        	document.removeEventListener('MSFullscreenChange', handleFullScreenChange);
            
        	if (util.isIOS()) {
        		document.removeEventListener('gesturestart', handleGestureStart);
        	}
            
        	api.cfg.rootElement[0].onselectstart = function(e) {
            	return true;
            };
        	
            touchBar = null;
            registered = false;
            api.cfg.touchMode = false;
            currentMode = "mouse";
            
            api.sendHandshake();

        	clearInterval(sendInterval);
        	clearInterval(canvasResizeInterval);
        	
        	$(".touch-control, #ws-cursor").remove();
        }
        
        // init functions
        
        function handleInputFocus(e) {
        	if (input.is(".focused-with-caret.editable")) {
        		$("#fake-input").remove();
        		if (!input.is(':focus')) {
        			if (focusCounter == 0) {
        				api.focusInput(input[0]);
        				focusCounter++;
        			}
        		} else {
        			if (input.val() == "") {
            			input.val(" ");
            			input.select();
        			}
        		}
        		correctInputPosition();
        	} else {
        		// prevent focusing input without caret
        		e.preventDefault();
        		input.blur();
        		return false;
        	}
        }
        
        function handleInputBlur(evt) {
        	focusCounter = 0;
        	input.val('');
        	$("#fake-input").remove();
//        	api.focusDefault();
        	// force canvas repaint
        	repaint();
        }
        
        function handleKeyup(event) {
        	if (event.keyCode == 8 || event.keyCode == 13) {
        		inputEvtQueue.push(getKBKey("keyup", event));
        	}
        	backspaceKeyDown = false;
        }
        
        function handleKeydown(event) {
        	if (event.keyCode == 8 || event.keyCode == 13) {
        		inputEvtQueue.push(getKBKey("keydown", event));
        	}
        	
       		backspaceKeyDown = (event.keyCode == 8);
        }
        
        function handleCompositionStart(event) {
        	compositionText = event.data;
        	composing = true;
        }
        
        function handleCompositionUpdate(event) {
        	for (var i=0; i<compositionText.length; i++) {
    			sendBackspace();
    		}
        	
        	compositionText = event.data;
        }
        
        function handleCompositionEnd(event) {
        	compositionText = event.data;
        	
        	if (compositionText.length == 0) {
            	composing = false;
        	}
        }
        
        function handleInputEvent(event) {
            if (event.inputType == 'deleteContentBackward') {
            	if (backspaceKeyDown) {
            		// handled by keydown-keyup handlers
            		return;
            	}
            	if (composing) {
            		for (var i=0; i<compositionText.length; i++) {
            			sendBackspace();
            		}
            	} else {
            		sendBackspace();
            	}
            	return;
            }
            
            if (event.inputType == 'insertCompositionText') {
        		if (event.data != null && event.data.length) {
        			sendWordsUsingKeypressEvent(event.data);
        			compositionText = event.data;
        		}
            	return;
            }
            
            if (!event.isComposing && event.inputType =='insertText' && event.data != null) {
                sendWordsUsingKeypressEvent(event.data);
                compositionText = "";
                composing = false;
            }
        }
        
        function handleFullScreenChange() {
        	// force canvas repaint
			repaint();
			
			var doc = window.document;
			var docEl = doc.documentElement;
			
			var isFullscreen = doc.fullscreenElement || doc.mozFullScreenElement || doc.webkitFullscreenElement || doc.msFullscreenElement;
			toast("Fullscreen " + (isFullscreen ? "ON" : "OFF"));
			
			if (fullscreenButton != null) {
				fullscreenButton.find(".ws-button-toggle-fullscreen")
        			.toggleClass("ws-icon-resize-small", isFullscreen)
        			.toggleClass("ws-icon-resize-full", !isFullscreen);
			}
        }
        
        function handleGestureStart(event) {
        	// prevent pinch-zoom
        	event = event.originalEvent || event;
        	event.preventDefault();
        }
        
        function initIntervals() {
        	sendInterval = setInterval(sendInput, 100);
            setTimeout(function() {
            	// defer resize check a few seconds to let the app resize successfully after touch bar makes the canvas smaller
            	canvasResizeInterval = setInterval(resizeCheck, 500);
            }, 3000);
        }
        
        function initTouchBar(config) {
        	if (api.cfg.hideTouchBar) {
        		return;
        	}
            if (touchBar != null) {
            	return;
            }
            
            var root = api.cfg.rootElement.parent();
            
            root.append(html);
            
            touchBar = root.find('div[data-id="touchBar"]');
            if (config) {
            	if ('scalingEnabled' in config) {
            		scalingEnabled = config.scalingEnabled;
            	}
            	
            	if (config.buttons) {
            		var buttonContainer = touchBar.find(".ws-toolbar-container");
            		var defButtons = config.buttons.defaultButtons;
            		
            		if (defButtons) {
            			appendButtonsToBar([defButtons.toggleMode], buttonContainer.find(".align-left"));
            			appendButtonsToBar([defButtons.switchToDesktop], buttonContainer.find(".align-left"));
            			appendButtonsToBar([defButtons.copy], buttonContainer.find(".align-center"));
            			appendButtonsToBar([defButtons.cut], buttonContainer.find(".align-center"));
            			appendButtonsToBar([defButtons.paste], buttonContainer.find(".align-center"));
            			appendButtonsToBar([defButtons.keyboard], buttonContainer.find(".align-right"));
            			appendButtonsToBar([defButtons.fullscreen], buttonContainer.find(".align-right"));
            		}
            		
            		appendButtonsToBar(config.buttons.left, buttonContainer.find(".align-left"));
            		appendButtonsToBar(config.buttons.center, buttonContainer.find(".align-center"));
            		appendButtonsToBar(config.buttons.right, buttonContainer.find(".align-right"));
            	}
            }
            
            $("#ws-canvas-scale .scale-restore").click(function() {
            	doScaleCanvas(1.0);
            });
            
    		cursor = $("#ws-cursor");
    		
            touchBar.show();
        }
        
        function appendButtonsToBar(buttons, container) {
        	if (buttons) {
    			for (var i = 0; i < buttons.length; i++) {
    				appendButtonToBar(buttons[i], container);
    			}
    		}
        }
        
        function appendButtonToBar(btn, container) {
        	if (!btn || !btn.enabled) {
        		return;
        	}
			var button = $('<button class="ws-touchbar-btn">' + api.translate(btn.label) + '</button>');
			button.attr("title", api.translate(btn.title));
			button.on("click", function() {
				if (btn.action) {
					btn.action(button);
				}
			});
			button.toggle(!btn.hidden);
			container.append(button);
        }
        
        // api functions 
        
        function cursorChanged(cursorMsg) {
        	if (!registered || !api.cfg.touchMode || !touchpadMode) {
        		return;
        	}
        	
        	cursor.removeClass();
        	cursor.css("content", "");
        	
        	if (cursorMsg.b64img == null) {
        		cursor.addClass(cursorMsg.cursor);
            } else {
            	cursor.addClass("custom");
                if (api.cfg.ieVersion) {
                	cursor.css("content", 'url(\'' + api.cfg.connectionUrl + 'file?id=' + cursorMsg.curFile + '\')');
                } else {
                    var data = util.getImageString(cursorMsg.b64img);
                    cursor.css("content", 'url(' + data + ')');
                }
            }
        }
        
        function inputFocusGained() {
        	if (!registered || !api.cfg.touchMode) {
        		return;
        	}
        	
        	if (!input.is(".focused-with-caret.editable")) {
        		return;
        	}
        	
			var top = input[0].style.top;
			var left = input[0].style.left;
			var height = input[0].style.height;
			
			input.data("topOrig", top);
			input.data("leftOrig", left);
			input.data("heightOrig", height);
			
			correctInputPosition();
        }
        
        function switchMode(doSwitch, dontAsk) {
        	switchModeRequested = false;
        	
        	if (dontAsk) {
        		switchModeDontAsk = true;
        	}
        	
        	if (!doSwitch) {
        		return;
        	}
        	
    		if (currentMode == "mouse") {
    			// switch to touch
    			doRegister(currentConfig, true);
    			
    			// if first time switched => mouse mode was original mode => device is probably a notebook with touch screen
    			if (!switchModeAlreadySwitched || isProbablyTouchBook) {
    				$(".ws-button-switch-to-desktop").parent().show();
    				$(".ws-button-toggle-mode").parent().hide(); // we don't need to switch to pointer mode anymore
    				isProbablyTouchBook = true;
    			}
    		} else {
    			// switch to mouse
    			doScaleCanvas(1.0); // make sure there is no touch scale applied
    			dispose();
    		}
    		
    		switchModeAlreadySwitched = true;
        }
        
        // button actions
        
        function buttonToggleMode(button) {
        	touchpadMode = !touchpadMode;
        	
        	button.find(".ws-button-toggle-mode")
        		.toggleClass("ws-icon-hand-paper-o", touchpadMode)
        		.toggleClass("ws-icon-mouse-pointer", !touchpadMode);
    		
    		cursor.toggle(touchpadMode).css({"top": cursor.parent().height() / 2, "left": cursor.parent().width() / 2});
    		
        	button.blur();
        	
        	toast(touchpadMode ? "Pointer Mode" : "Touch Mode");
        }
        
        function buttonSwitchToDesktop(button) {
        	doScaleCanvas(1.0); // make sure there is no touch scale applied
			dispose();
        }
        
        function buttonToggleScrollMode(button) {
        	// not used
        	
//        	button.find(".ws-scroll-drag").toggle(scrollMode == 'drag');
//        	button.find(".ws-scroll-swipe").toggle(scrollMode == 'swipe');
//        	
//        	if (scrollMode == 'drag') {
//        		scrollMode = 'swipe';
//        		toast("Swipe Scroll");
//        	} else {
//        		scrollMode = 'drag';
//        		toast("Drag Scroll");
//        	}
//        	button.blur();
        }
        
        function buttonCopy(button) {
        	button.blur();
        	api.send({ copy: { type: 'copy' } });
        	toast("Copy");
        }
        
        function buttonCut(button) {
        	button.blur();
        	api.send({ copy: { type: 'cut' } });
        	toast("Cut");
        }
        
        function buttonPaste(button) {
        	button.blur();
        	api.displayPasteDialog({"title": "${clipboard.paste.title.touch}", "message": "${clipboard.paste.message.touch}"})
        	toast("Paste");
        }
        
        function buttonOpenKeyboard(button) {
        	button.blur();
        	showKeyboard();
       		toast("Keyboard ON");
        }
        
        function buttonToggleFullscreen(button) {
        	fullscreenButton = button;
        	
			button.blur();
			
			var doc = window.document;
			var docEl = doc.documentElement;
			
			var requestFullScreen = docEl.requestFullscreen || docEl.mozRequestFullScreen || docEl.webkitRequestFullScreen || docEl.msRequestFullscreen;
			var cancelFullScreen = doc.exitFullscreen || doc.mozCancelFullScreen || doc.webkitExitFullscreen || doc.msExitFullscreen;
			
			if (!doc.fullscreenElement && !doc.mozFullScreenElement && !doc.webkitFullscreenElement && !doc.msFullscreenElement) {
				requestFullScreen.call(docEl)
			} else {
				cancelFullScreen.call(doc);
			}
        }
        
        // touch handling
        
        function handleStart(evt) {
        	if (touchpadMode) {
        		handleStartPointer(evt);
        	} else if (scrollMode == "drag") {
        		handleStartScrollDrag(evt);
        	} else if (scrollMode == "swipe") {
        		handleStartScrollSwipe(evt);
        	}
        }
        
        function handleMove(evt) {
        	if (touchpadMode) {
        		handleMovePointer(evt);
        	} else if (scrollMode == "drag") {
        		handleMoveScrollDrag(evt);
        	} else if (scrollMode == "swipe") {
        		handleMoveScrollSwipe(evt);
        	}
        }
        
        function handleEnd(evt) {
        	if (touchpadMode) {
        		handleEndPointer(evt);
        	} else if (scrollMode == "drag") {
        		handleEndScrollDrag(evt);
        	} else if (scrollMode == "swipe") {
        		handleEndScrollSwipe(evt);
        	}
        }
        
        function handleCancel(evt) {
        	if (touchpadMode) {
        		handleCancelDefault(evt);
        	} else if (scrollMode == "drag") {
        		handleCancelDefault(evt);
        	} else if (scrollMode == "swipe") {
        		handleCancelDefault(evt);
        	}
        }
        
        function handleStartDefault(evt) {
        	if (longPressTimeout != null) {
        		clearTimeout(longPressTimeout);
        		longPressTimeout = null;
        	}
        	if (longPressStartTimeout != null) {
        		clearTimeout(longPressStartTimeout);
        		longPressStartTimeout = null;
        	}
        	
        	var touches = evt.changedTouches;
        	   
        	if (evt.touches.length == 1) {
        		dragStarted = evt.timeStamp;
        		tapStarted = evt.timeStamp;
        		var tx = touches[0].clientX;
        		var ty = touches[0].clientY;
        		touchStartX = tx;
        		touchStartY = ty;
        		
        		animateDrag(touches[0].clientX, touches[0].clientY);
        		longPressStartTimeout = setTimeout(function() {
        			animateLongPress(touches[0].clientX, touches[0].clientY);
        		}, 50);
        		
        		longPressTimeout = setTimeout(function() {
        			// handle long press
        			longPressTimeout = null;
        			tapStarted = 0;
        			
        			var eventMsg = [];
        			eventMsg.push(createMouseEvent(evt.target, 'mousedown', tx, ty, 3));
        			eventMsg.push(createMouseEvent(evt.target, 'mouseup', tx, ty, 3));
        			
        			api.send({events: eventMsg});
        			api.focusDefault();
        			
        			cancelAnimateDrag();
        			animateLongPressOut(tx, ty);
        		}, tapDelayThreshold);
        	} else {
        		// cancel tap and long press
        		tapStarted = 0;
        		dragStarted = 0;
        		touchStartY = 0;
        		touchStartX = 0;
        		cancelAnimateLongPress();
        		cancelAnimateDrag();
        		
        		if (longPressTimeout != null) {
            		clearTimeout(longPressTimeout);
            		longPressTimeout = null;
            	}
        	}
        	
        	handleStartScaling(evt);
        	
        	if (evt.cancelable) {
        		// prevent browser simulating mouse events
        		evt.preventDefault();
        	}
        }
        
        function handleStartScrollDrag(evt) {
        	handleStartDefault(evt);
        	
        	dragging = false;
        }
        
        function handleStartScrollSwipe(evt) {
        	handleStartDefault(evt);
        	
        	cancelAnimateDrag();
        }
        
        function handleStartPointer(evt) {
        	if (longPressTimeout != null) {
        		clearTimeout(longPressTimeout);
        		longPressTimeout = null;
        	}
        	if (longPressStartTimeout != null) {
        		clearTimeout(longPressStartTimeout);
        		longPressStartTimeout = null;
        	}
        	
        	var touches = evt.changedTouches;
     	   
        	if (evt.touches.length == 1) {
        		tapStarted = evt.timeStamp;
        		dragStarted = evt.timeStamp;
        		touchStartX = touches[0].clientX;
        		touchStartY = touches[0].clientY;
        		var cursorX = parseInt(cursor.css("left"), 10);
        		var cursorY = parseInt(cursor.css("top"), 10);
        		cursor.data("originX", cursorX);
        		cursor.data("originY", cursorY);
        		var relatedCanvas = getPointerRelatedCanvas(cursorX, cursorY);

        		longPressStartTimeout = setTimeout(function() {
        			animateLongPress(cursorX, cursorY);
        		}, 50);
        		
        		longPressTimeout = setTimeout(function() {
        			// handle long press
        			longPressTimeout = null;
        			tapStarted = 0;
        			
        			var eventMsg = [];
        			eventMsg.push(createMouseEvent(relatedCanvas, 'mousedown', cursorX, cursorY, 3));
        			eventMsg.push(createMouseEvent(relatedCanvas, 'mouseup', cursorX, cursorY, 3));
        			
        			api.send({events: eventMsg});
        			api.focusDefault();
        			
        			animateLongPressOut(cursorX, cursorY);
        		}, tapDelayThreshold);
        	} else {
        		tapStarted = 0;
        		touchStartY = 0;
        		touchStartX = 0;
        		cancelAnimateLongPress();
        		
        		if (longPressTimeout != null) {
            		clearTimeout(longPressTimeout);
            		longPressTimeout = null;
            	}
        	}
        	
        	handleStartScaling(evt);
        	
        	dragging = false;
        	
        	if (evt.cancelable) {
        		// prevent browser simulating mouse events
        		evt.preventDefault();
        	}
        }
        
        function handleMoveDefault(evt) {
        	var touches = evt.changedTouches;
        	
        	if (scalingEnabled && scaling && evt.touches.length == 2) {
        		var dist = Math.hypot(evt.touches[0].clientX - evt.touches[1].clientX, evt.touches[0].clientY - evt.touches[1].clientY);
        		var diff = (dist - lastScaleDist) * scaleSpeed;
        		
        		lastScaleDist = dist;

        		var canvasDiameter = Math.hypot(canvas.width, canvas.height);
        		var scale = diff / canvasDiameter;
        		var lastScale = $(canvas).data("scale") || 1.0;
        		var minScale = $(canvas).data("minscale") || 1.0

        		scale = Math.min(Math.max(minScale, lastScale + scale), maxScale);
        		
        		var originX = (evt.touches[0].clientX + evt.touches[1].clientX) / 2;
        		var originY = (evt.touches[0].clientY + evt.touches[1].clientY) / 2;
        		
        		doScaleCanvas(scale);
        		
        		var tx1 = evt.touches[0].clientX;
        		var ty1 = evt.touches[0].clientY;
        		var tx2 = evt.touches[1].clientX;
        		var ty2 = evt.touches[1].clientY;
        		
        		var sx1 = scaleStartPoints[0].x;
        		var sy1 = scaleStartPoints[0].y;
        		var sx2 = scaleStartPoints[1].x;
        		var sy2 = scaleStartPoints[1].y;
        		
        		var widthChange = ($(canvas).width() * scale) - scaleStartDim.width;
        		var heightChange = ($(canvas).height() * scale) - scaleStartDim.height;
        		
        		var sCenter = {"x": ((sx1 + sx2) / 2), "y": ((sy1 + sy2) / 2)};
        		var tCenter = {"x": ((tx1 + tx2) / 2), "y": ((ty1 + ty2) / 2)};
        		
        		// scroll change is relative to scale start point
        		// (center = 0,0 => no scroll change)
        		// (center = width,height => full scroll change)
        		var scrollLeft = scaleStartScrollLeft + (widthChange * (sCenter.x / $(canvas).width())); 
        		var scrollTop = scaleStartScrollTop + (heightChange * (sCenter.y / $(canvas).height()));
        		
        		// additional scroll due to center change
        		scrollLeft += sCenter.x - tCenter.x;
        		scrollTop += sCenter.y - tCenter.y;
        		
        		var parent = api.cfg.rootElement;
        		
    			parent[0].scrollLeft = scrollLeft;
    			parent[0].scrollTop = scrollTop;
    			
    			correctInputPosition();
        	} else {
        		scaling = false;
        	}
        }
        
        function handleMoveScrollDrag(evt) {
        	handleMoveDefault(evt);
        	
        	var touches = evt.changedTouches;

        	if (evt.touches.length == 1) {
        		handleDragMove(evt.target, evt.touches[0].clientX, evt.touches[0].clientY, touches[0].clientX, touches[0].clientY, touchStartX, touchStartY, true, function() {return true;});
        	} else {
        		// cancel tap
        		tapStarted = 0;
        		
    			// cancel drag
    			dragStarted = 0;
    			dragging = false;
        	}
        	
        	if (evt.cancelable) {
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
        }
        
        function handleMoveScrollSwipe(evt) {
        	handleMoveDefault(evt);
        	
        	var touches = evt.changedTouches;
        	
        	if (evt.touches.length == 1) {
        		// if moved too much, cancel tap/long press event and handle wheel
        		if (Math.abs(touchStartX - evt.touches[0].clientX) > tapMoveThreshold || Math.abs(touchStartY - evt.touches[0].clientY) > tapMoveThreshold) {
        			// cancel tap
        			tapStarted = 0;
        			// cancel long press
        			clearTimeout(longPressTimeout);
        			longPressTimeout = null;
        			cancelAnimateLongPress();
        			
        			// handle mouse wheel vertical scroll
    				var wheelDelta = parseInt(touchStartY - evt.touches[0].clientY);
    				if (wheelDelta != 0) {
    					var me = createMouseEvent(evt.target, 'mousewheel', touches[0].clientX, touches[0].clientY, 0);
    					me.mouse.wheelDelta = wheelDelta;
    					
    					if (latestMouseWheelEvent != null) {
    						latestMouseWheelEvent.mouse.wheelDelta += wheelDelta;
    					} else {
    						latestMouseWheelEvent = me;
    					}
    					
    					touchStartY = evt.touches[0].clientY;
    				}
        		}
        	} else {
        		// cancel tap
        		tapStarted = 0;
        	}
        	
        	if (evt.cancelable) {
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
        }
        
        function handleMovePointer(evt) {
        	handleMoveDefault(evt);
        	
        	var touches = evt.changedTouches;
        	
        	if (evt.touches.length == 1) {
        		var coords = getPointerCoords(evt.touches[0].clientX, evt.touches[0].clientY);
        		var relatedCanvas = getPointerRelatedCanvas(coords.x, coords.y);
        		cursor.css({"left": coords.x, "top": coords.y});
        		
        		handleDragMove(relatedCanvas, evt.touches[0].clientX, evt.touches[0].clientY, coords.x, coords.y, cursor.data("originX"), cursor.data("originY"), false, function() {
        			return evt.timeStamp - lastTap <= doubleTapDelayThreshold;
        		});
        		
                if (!dragging) {
                	// mouse move only
                	latestMouseMoveEvent = createMouseEvent(relatedCanvas, 'mousemove', coords.x, coords.y, 0);
                	cancelAnimateLongPress();
                }
        	} else {
        		tapStarted = 0;
        		dragStarted = 0;
        		dragging = false;
        		latestMouseMoveEvent = null;
        	}
        	
        	if (evt.cancelable) {
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
        }
        
        function handleEndDefault(evt, relatedCanvas, x, y) {
        	if (longPressTimeout != null) {
        		clearTimeout(longPressTimeout);
        		longPressTimeout = null;
        	}
        	
        	var touches = evt.changedTouches;
        	
        	if (tapStarted > 0) {
        		var duration = evt.timeStamp - tapStarted;
        		
        		if (duration <= tapDelayThreshold) {
        			// tap
        			var eventMsg = [];
        			eventMsg.push(createMouseEvent(relatedCanvas, 'mousedown', x, y, 1));
        			eventMsg.push(createMouseEvent(relatedCanvas, 'mouseup', x, y, 1));

        			if ($(relatedCanvas).closest(".webswing-html-canvas").length) {
        				// simulate browser mouse click on HtmlPanel (does not work for iframe)
        				var mouseClick = new MouseEvent("click", {
        					bubbles: true,
        					cancelable: true,
        					view: window,
        					clientX: x,
							clientY: y,
        				});
        				relatedCanvas.dispatchEvent(mouseClick);
        			}
        			
        			api.send({events: eventMsg});
        			hideKeyboard();
        			
        			// focus fake input
        			// iOS: need to focus this input to be able to focus on the real keyboard input not from user-initiated event
        			if (!$("#fake-input").length) {
        				var fakeInput = $('<input type="text" id="fake-input" style="position: absolute; opacity: 0; height: 0; font-size: 16px;" readonly="true">');
        				$("body").prepend(fakeInput);
        						
        				fakeInput.on("focus", function() {
							setTimeout(function() {
								$("#fake-input").remove();
							}, 1000);
						});
        			}
        			$("#fake-input")[0].focus({preventScroll: true});
        		
        			cancelAnimateDrag();
        			cancelAnimateLongPress();
        			animateTap(x, y);
        			
        			if (tapStarted - lastTap <= doubleTapDelayThreshold) {
        				// double tap
        				var me = createMouseEvent(relatedCanvas, 'dblclick', x, y, 1);
        				
        				api.send({events: [me]});
        				
        				lastTap = 0;
        			}
        			
        			lastTap = tapStarted;
        		} else {
        			// long press, already handled by timer
        		}
        	}
        	
        	scaling = false;
        	lastScaleDist = 0;
        	
        	// try prevent browser simulated mouse events
        	if (evt.cancelable) {
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
        }

        function handleEndScrollDrag(evt) {
        	var touches = evt.changedTouches;
        	
        	handleEndDefault(evt, evt.target, touches[0].clientX, touches[0].clientY);
        	handleDragMoveEnd(evt.target, touches[0].clientX, touches[0].clientY);
        }
        
        function handleEndScrollSwipe(evt) {
        	var touches = evt.changedTouches;
        	
        	handleEndDefault(evt, evt.target, touches[0].clientX, touches[0].clientY);
        }
        
        function handleEndPointer(evt) {
        	var touches = evt.changedTouches;
        	var coords = getPointerCoords(touches[0].clientX, touches[0].clientY);
        	var relatedCanvas = getPointerRelatedCanvas(coords.x, coords.y);

        	handleEndDefault(evt, relatedCanvas, coords.x, coords.y);
        	handleDragMoveEnd(relatedCanvas, coords.x, coords.y);
        }
        
        function handleCancelDefault(evt) {
        	if (longPressTimeout != null) {
        		clearTimeout(longPressTimeout);
        		longPressTimeout = null;
        	}
        	
        	var touches = evt.changedTouches;
        	  
        	if (evt.cancelable) {
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
        }
        
        // [tx, ty] - point from evt.touches, [x, y] - point from evt.changedTouches or new pointer coords, [tsX, tsY] - point of touch/pointer start
        function handleDragMove(relatedCanvas, tx, ty, x, y, tsX, tsY, doAnimateDrag, startDragCondition) {
        	// if moved too much, cancel tap/long press event and handle wheel
    		if (Math.abs(touchStartX - tx) > tapMoveThreshold || Math.abs(touchStartY - ty) > tapMoveThreshold) {
    			// cancel tap
    			tapStarted = 0;
    			// cancel long press
    			if (longPressTimeout != null) {
    				clearTimeout(longPressTimeout);
    				longPressTimeout = null;
    			}
    			cancelAnimateLongPress();
    			
    			// start drag if not started
    			if (!dragging && dragStarted > 0 && startDragCondition()) {
    				dragging = true;
    				api.send({events: [createMouseEvent(relatedCanvas, 'mousedown', tsX, tsY, 1)]});
    			}
    		}
    		
    		// if moved just a little, do not start drag unless already started
    		if (dragging) {
    			api.send({events: [createMouseEvent(relatedCanvas, 'mousemove', x, y, 1)]});
    			if (doAnimateDrag) {
    				animateDrag(x, y);
    			}
    		}
        }
        
        function handleDragMoveEnd(relatedCanvas, x, y) {
        	if (dragging) {
        		var me = createMouseEvent(relatedCanvas, 'mouseup', x, y, 1);
        		
        		api.send({events: [me]});
        		api.focusDefault();
        		
        		dragging = false;
        	}
        	
        	cancelAnimateDrag();
        }
        
        function handleStartScaling(evt) {
        	if (!scalingEnabled) {
        		return;
        	}
        	
        	var parent = api.cfg.rootElement;
        	
        	scaleStartScrollLeft = parent.scrollLeft();
        	scaleStartScrollTop = parent.scrollTop();
        	
        	if (evt.touches.length == 2) {
        		scaling = true;
        		var x1 = evt.touches[0].clientX;
        		var y1 = evt.touches[0].clientY;
        		var x2 = evt.touches[1].clientX;
        		var y2 = evt.touches[1].clientY;
        		lastScaleDist = Math.hypot(x1 - x2, y1 - y2);
        		scaleStartPoints = [{"x": x1, "y": y1}, {"x": x2, "y": y2}];
        		
        		var $canvas = $(canvas);
        		var scale = $canvas.data("scale") || $canvas.data("minscale") || 1.0;
        		scaleStartDim = {"width": $canvas.width() * scale, "height": $canvas.height() * scale};
        	} else {
        		scaling = false;
        		lastScaleDist = 0;
        		scaleStartPoints = [];
        	}
        }
        
        // animate functions
        
        function animateTap(x, y) {
        	var tap = $("#ws-touch-effect");
        	tap.removeClass("animate animateLong animateLongOut").css({top: (y - tap.height()/2) + 'px', left: (x - tap.width()/2) + 'px'}).addClass("animate");
        }
        
        function animateLongPress(x, y) {
        	var tap = $("#ws-touch-effect");
        	tap.removeClass("animate animateLong animateLongOut").css({top: (y - tap.height()/2) + 'px', left: (x - tap.width()/2) + 'px'}).addClass("animateLong");
        }
        
        function animateLongPressOut(x, y) {
        	var tap = $("#ws-touch-effect");
        	tap.removeClass("animate animateLong animateLongOut").css({top: (y - tap.height()/2) + 'px', left: (x - tap.width()/2) + 'px'}).addClass("animateLongOut");
        }
        
        function cancelAnimateLongPress() {
        	if (longPressStartTimeout != null) {
        		clearTimeout(longPressStartTimeout);
        		longPressStartTimeout = null;
        	}
        	$("#ws-touch-effect").removeClass("animate animateLong animateLongOut");
        }
        
        function cancelAnimateDrag() {
        	$('.ws-drag-effect').hide();
        }
        
        function animateDrag(x, y) {
        	var el = $('.ws-drag-effect');
        	el.css({'top': (y - el.height()/2) + "px", 'left': (x - el.width()/2) + "px"}).show();
        }
        
        // utility functions
        
        function isNotValidCanvasTarget(target) {
        	if (!target) {
        		return true;
        	}

        	if (target.matches && (target.matches("canvas.webswing-canvas") || target.matches(".webswing-html-canvas"))) {
        		return false;
        	}

        	if ($(target).closest(".webswing-html-canvas").length) {
        		return false;
        	}

        	return true;
        }

        function getPointerCoords(touchX, touchY) {
        	var parent = api.cfg.rootElement;
    		var pHeight = parent.height();
    		var pWidth = parent.width();
    		var xMove = touchX - touchStartX;
    		var yMove = touchY - touchStartY;
    		var originX = cursor.data("originX");
    		var originY = cursor.data("originY");
    		
    		var left = Math.max(0, Math.min(originX + xMove, pWidth - 1));
    		var top = Math.max(0, Math.min(originY + yMove, pHeight - 1));
    		
    		return {x: left, y: top};
        }
        
        function getPointerRelatedCanvas(px, py) {
        	return document.elementFromPoint(px, py);
        }

        function doScaleCanvas(scale) {
        	if (!scalingEnabled) {
        		return;
        	}
        	
        	if (scaleHideTimer != null) {
    			clearTimeout(scaleHideTimer);
    			scaleHideTimer = null;
    		}

        	if (scale == 1.0) {
        		$(canvas).css({"transform": "scale(" + scale + ")", "transform-origin": ""});
        	} else {
        		$(canvas).css({"transform": "scale(" + scale + ")", "transform-origin": "top left"});
        	}
    		$(canvas).data("scale", scale);
    		
    		correctInputPosition();
    		
    		if (scale != 1.0) {
    			$("#ws-canvas-scale").show();
    			$("#ws-canvas-scale .scale-restore").show();
    		} else {
    			$("#ws-canvas-scale .scale-restore").hide();
    		}
    		$("#ws-canvas-scale .scale-value").text(new Number(scale * 100).toFixed(0) + "%");
    		
    		scaleHideTimer = setTimeout(function() {
    			$("#ws-canvas-scale").fadeOut("slow");
    			scaleHideTimer = null;
    		}, 3000);
    		
    		repaint();
        }
        
        function correctInputPosition() {
        	if (!input.is(".focused-with-caret.editable")) {
        		return;
        	}
        	
        	var top = input.data("topOrig");
        	var left = input.data("leftOrig");
        	var height = input.data("heightOrig");
        	var scale = $(canvas).data("scale") || 1.0;
        	var parent = api.cfg.rootElement[0];
        	
        	input[0].style.top = (parseInt(top) * scale) + "px";
        	input[0].style.left = (parseInt(left) * scale) + "px";
        	input[0].style.height = (parseInt(height) * scale) + "px";
        }
        
        function sendWordsUsingKeypressEvent(data) {
            for (var i = 0, length = data.length; i < length; i++) {
                inputEvtQueue.push(
                	{ key : { type : 'keydown', character : data.charCodeAt(i), keycode : 0 } },
                	{ key : { type : 'keypress', character : data.charCodeAt(i), keycode : 0 } },
                	{ key : { type : 'keyup', character : data.charCodeAt(i), keycode : 0 } }
                );
            }
        }
        
        function sendBackspace() {
        	inputEvtQueue.push({key: {type: 'keydown', character: 8, keycode: 8, "alt": false, "ctrl": false, "shift": false, "meta": false}});
        	inputEvtQueue.push({key: {type: 'keypress', character: 8, keycode: 8, "alt": false, "ctrl": false, "shift": false, "meta": false}});
        	inputEvtQueue.push({key: {type: 'keyup', character: 8, keycode: 8, "alt": false, "ctrl": false, "shift": false, "meta": false}});
        }
        
        function getKBKey(type, evt) {
            var char = evt.which;
            if (char == 0 && evt.key != null) {
                char = evt.key.charCodeAt(0);
            }
            var kk = evt.keyCode;
            if (kk == 0) {
                kk = char;
            }
            return {
                key: {
                    type: type,
                    character: char,
                    keycode: kk,
                    alt: evt.altKey,
                    ctrl: evt.ctrlKey,
                    shift: evt.shiftKey,
                    meta: evt.metaKey
                }
            };
        }
        
        function createMouseEvent(targetElement, type, x, y, button) {
            var rect;
            if (targetElement && targetElement != null && targetElement.parentNode && targetElement.parentNode != null && targetElement.parentNode.getBoundingClientRect) {
                if ($(targetElement).is(".internal")) {
                	rect = targetElement.parentNode.parentNode.getBoundingClientRect();
                } else {
                	rect = targetElement.parentNode.getBoundingClientRect();
                }
            } else {
                rect = api.getCanvas().getBoundingClientRect();
            }

            var winId;
            if (targetElement && targetElement.matches("canvas.webswing-canvas") && $(targetElement).data("id") && $(targetElement).data("id") != "canvas") {
            	// for a composition canvas window send winId
            	if ($(targetElement).is(".internal")) {
            		// internal window must use its parent as mouse events target
            		if ($(targetElement.parentNode).data("ownerid")) {
            			winId = $(targetElement.parentNode).data("ownerid");
            		}
            	} else {
            		winId = $(targetElement).data("id");
            	}
            }

            // return relative mouse position
            var mouseX = Math.round(x - rect.left);
            var mouseY = Math.round(y - rect.top);

            if (type == 'mouseup' && (!targetElement || !targetElement.matches || !targetElement.matches("canvas.webswing-canvas"))) {
            	// fix for detached composition canvas windows that might overlay same coordinates space, when clicked outside a canvas
            	mouseX = -1;
            	mouseY = -1;
            }

            if (type == 'mouseup' && targetElement && targetElement.matches && !targetElement.matches("canvas.webswing-canvas") && targetElement.closest(".webswing-html-canvas") != null) {
            	// fix for mouseup over an HtmlWindow div content
            	rect = targetElement.closest(".webswing-html-canvas").parentNode.getBoundingClientRect();

            	mouseX = Math.round(x - rect.left);
            	mouseY = Math.round(y - rect.top);
            }
            
            var scale = $(canvas).data("scale") || 1.0;
            
            return {
                mouse: {
                    x: Math.round(mouseX / scale),
                    y: Math.round(mouseY / scale),
                    type: type,
                    button: button,
                    buttons: button == 0 ? 0 : Math.pow(2, button),
                    winId: winId || "",
                    timeMilis: Date.now() % 86400000
                }
            }
        }
        
        function sendInput() {
            enqueueInputEvent();
            var evts = inputEvtQueue;
            inputEvtQueue = [];
            if (evts.length > 0) {
                api.send({
                    events: evts
                });
            }
        }
        
        function enqueueInputEvent() {
            if (api.cfg.hasControl) {
                if (latestMouseWheelEvent != null) {
                	var wheelDelta = latestMouseWheelEvent.mouse.wheelDelta;
                	var sign = Math.sign(wheelDelta);
                	
                	if (Math.abs(wheelDelta) < 10) {
                		wheelDelta = sign;
                	} else {
                		wheelDelta = parseInt(wheelDelta / 10);
                	}
                	
            		inputEvtQueue.push(
        				{
        					"mouse": 
        					{
        						"x": latestMouseWheelEvent.mouse.x, "y": latestMouseWheelEvent.mouse.y,
        						"type": "mousewheel",
        						"wheelDelta": wheelDelta,
        						"button": 0, "buttons": 0,
        						"ctrl": false, "alt": false, "shift": false, "meta": false,
        						"timeMilis": latestMouseWheelEvent.mouse.timeMilis
        					}
        				}
            		);
            		
                    latestMouseWheelEvent = null;
                }
                if (latestMouseMoveEvent != null) {
                	inputEvtQueue.push(latestMouseMoveEvent);
                	latestMouseMoveEvent = null;
                }
            }
        }
        
        function isKeyboardShowing() {
        	return input.is(".focused-with-caret.editable");
        }
        
        function showKeyboard() {
			input.addClass('focused-with-caret editable');
			api.focusInput(input[0]);
        }
        
        function hideKeyboard() {
        	if (!isKeyboardShowing()) {
        		return;
        	}
            input.removeClass('focused-with-caret editable');
        	input.blur();
        }
        
        function resizeCheck() {
        	var w = api.getRootWidth();
        	var h = api.getRootHeight();
        	var widthChanged = $(canvas).width() !== w;
        	var heightChanged = $(canvas).height() !== h;
        	var sizeChanged = widthChanged || heightChanged;
        	
            if (!api.cfg.mirror && canvas != null) {
            	if (lastRootWidth != api.cfg.rootElement.width() || lastRootHeight != api.cfg.rootElement.height()) {
            		var minScale = Math.min(document.body.offsetWidth / api.getRootWidth(), (document.body.offsetHeight - touchBar.height()) / api.getRootHeight());
            		$(canvas).data("minscale", minScale);
            		if (minScale > $(canvas).data("scale")) {
            			$(canvas).data("scale", minScale);
            			doScaleCanvas(minScale);
            		}

            		lastRootWidth = api.cfg.rootElement.width();
            		lastRootHeight = api.cfg.rootElement.height();
            	}

            	if (sizeChanged) {
            		if (input.is(".focused-with-caret.editable")) {
            			if (!widthChanged && (h - $(canvas).height()) < 100) {
            				// canvas should resize height to smaller and input is focused = keyboard is probably showing
            				isKeyboardProbablyShowing = true;
            			}
            			return;
            		}
            		
            		doResize();
            	} else if (input.is(".focused-with-caret.editable") && isKeyboardProbablyShowing) {
        			// size has not changed but input is still focused and keyboard is probably showing = user probably closed keyboard with device back button
        			// unfocus input
        			hideKeyboard();
            	}
            	
            	isKeyboardProbablyShowing = false;
            }
        }
        
        function doResize() {
        	var w = api.getRootWidth();
        	var h = api.getRootHeight();
        	var dpr = util.dpr;
            var snapshot = canvas.getContext("2d").getImageData(0, 0, canvas.width, canvas.height);
            
            canvas.width = w * dpr;
            canvas.height = h * dpr;
            canvas.style.width = w + 'px';
            canvas.style.height = h + 'px';
            canvas.getContext("2d").putImageData(snapshot, 0, 0);
            api.sendHandshake();
        }
        
        function repaint() {
        	if (repaintTimeout != null) {
        		clearTimeout(repaintTimeout);
        		repaintTimeout = null;
        	}
        	if (new Date().getTime() - lastRepaint < 500) {
        		repaintTimeout = setTimeout(repaint, 250);
        		return;
        	}
        	api.repaint();
        	lastRepaint = new Date().getTime();
        }
        
	    function toast(msg) {
	    	if (toastTimer != null) {
	    		clearTimeout(toastTimer);
	    		toastTimer = null;
	    	}
	    	
	    	$("#ws-toast").text(msg).show();
	    	toastTimer = setTimeout(function() {
	    		$("#ws-toast").text("").hide();
	    	}, 2000);
	    }
	    
    }
