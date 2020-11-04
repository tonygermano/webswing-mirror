
    "use strict";

    export default function InputModule(util) {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            send: 'socket.send',
            getCanvas: 'canvas.get',
            cut: 'clipboard.cut',
            copy: 'clipboard.copy',
            paste: 'clipboard.paste',
            isAccessibilityEnabled: 'accessible.isEnabled',
            toggleAccessibility: 'accessible.toggle',
            focusInput: 'focusManager.focusInput'
        };
        module.provides = {
            register: register,
            sendInput: sendInput,
            dispose: dispose,
            registerUndockedCanvas: registerUndockedCanvas,
            updateFocusedHtmlCanvas: updateFocusedHtmlCanvas
        };

        module.ready = function () {
        };

        var registered = false;
        var registeredListeners = [];
        var latestMouseMoveEvent = null;
        var latestMouseWheelEvent = null;
        var latestWindowResizeEvent = null;
        var latestKeyDownEvent = null;
        var latestKeyDownEventSendTimeout = null;
        var mouseDown = 0;
        var mouseDownButton = 0;
        var mouseDownCanvas = null;
        var inputEvtQueue = [];
        var pixelsScrolled = 0;
        var compositionInput = false;
        var pagePosition = {}; // {<winId>: {x: <x>: y: <y>}}
        var focusedHtmlCanvas = null;
        var DEFAULT_FONT = '14px sans-serif';
        var ctrlCounter = 0;
        var lastCtrlKey = 0;
        var ctrl3Timeout = null;
        
        var caretControlKeys = [33/*pgup*/, 34/*pgdown*/, 35/*end*/, 36/*home*/, 37/*left*/, 38/*up*/, 39/*right*/, 40/*down*/];

        function sendInput(message) {
            enqueueInputEvent();
            var evts = inputEvtQueue;
            inputEvtQueue = [];
            if (message != null) {
                evts.push(message);
            }
            if (evts.length > 0) {
                api.send({
                    events: evts
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
            latestKeyDownEvent = null;
            latestKeyDownEventSendTimeout = null;
            mouseDown = 0;
            mouseDownButton = 0;
            resetMouseDownCanvas();
            inputEvtQueue = [];
        }

        function dispose() {
            registered = false;
            resetInput();
            
            for (var i=0; i<registeredListeners.length; i++) {
            	var l = registeredListeners[i];
            	if (l.useCapture) {
            		l.target.removeEventListener(l.name, l.listener, l.useCapture);
            	} else {
            		l.target.removeEventListener(l.name, l.listener);
            	}
            }
        }
        
        function registerUndockedCanvas(win) {
        	var inputE = win.document.createElement("input");
        	inputE.classList.add("ws-input-hidden");
        	inputE.setAttribute("autocorrect", "off");
        	inputE.setAttribute("autocapitalize", "none");
        	inputE.setAttribute("autocomplete", "off");
        	inputE.setAttribute("data-id", "input-handler");
        	win.document.querySelector('.webswing-element .webswing-element-content').appendChild(inputE);
        	win.inputHandler = inputE;
        	
        	registerInternal(win.inputHandler, win.document, win);
        }
        
        function register() {
            if (registered) {
                return;
            }
            
            registerInternal(document.querySelector(".ws-input-hidden"), document, window);

            registered = true;
        }
        
        function registerInternal(input, doc, win) {
            resetInput();
            focusInput(input);

            registerGlobalListener(doc, 'mousedown', (evt) => { handleMouseDown(evt, input)}, true);
            registerGlobalListener(doc, 'dblclick', (evt) => { handleDblClick(evt, input)} );
            registerGlobalListener(doc, 'mousemove', handleMouseMove);
            registerGlobalListener(doc, 'mouseup', (evt) => { handleMouseUp(evt, input)} );
            registerGlobalListener(doc, 'wheel', handleWheel);
            registerGlobalListener(doc, 'contextmenu', handleContextMenu);
            registerGlobalListener(doc, 'keydown', handleKeyDown);
            registerGlobalListener(doc, 'keypress', handleKeyPress);
            registerGlobalListener(doc, 'keyup', handleKeyUp);
            registerGlobalListener(doc, 'compositionstart', (evt) => { handleCompositionStart(evt, input) });
            registerGlobalListener(doc, 'compositionupdate', (evt) => { handleCompositionUpdate(evt, input) });
            registerGlobalListener(doc, 'compositionend', (evt) => { handleCompositionEnd(evt, input) });
            registerGlobalListener(doc, 'input', (evt) => { handleInput(evt, input) });
            registerGlobalListener(doc, 'cut', handleCut);
            registerGlobalListener(doc, 'copy', handleCopy);
            registerGlobalListener(doc, 'paste', handlePaste);
//            registerGlobalListener(doc, 'mousedown', mouseDownEventHandler); // this should not be needed
            registerGlobalListener(doc, 'mouseout', (evt) => { mouseOutEventHandler(evt, input)} );
            registerGlobalListener(doc, 'mouseover', mouseOverEventHandler);
            registerGlobalListener(win, 'blur', (evt) => { setTimeout(()=>handleWindowBlur(evt.target),0) /* using the 'setTimout' to let the event pass the run loop*/} );
        }
        
        function registerGlobalListener(target, eventName, listener, useCapture) {
            util.bindEvent(target, eventName, listener, useCapture);
            registeredListeners.push({target: target, name: eventName, listener: listener, useCapture: useCapture});
        }

        function updateFocusedHtmlCanvas(focusedElement) {
            var htmlcanvas = isInsideWebswingHtmlCanvas(focusedElement);
            if (htmlcanvas) {
                if (focusedHtmlCanvas !== htmlcanvas) {
                    console.log("send focus to HTMLPanel" + htmlcanvas);
                    sendInput({
                        focus: {
                            htmlPanelId: htmlcanvas.getAttribute("data-id")
                        }
                    });
                }
                focusedHtmlCanvas = htmlcanvas;
            } else {
                focusedHtmlCanvas = null;
            }
        }

        function handleWindowBlur(targetWindow) {
        	if (targetWindow !== null) {
        		updateFocusedHtmlCanvas(targetWindow.document.activeElement)
        	}
        }
        
        function handleMouseDown(evt, input) {
            updateFocusedHtmlCanvas(evt.target);

        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
            mouseDownEventHandler(evt);
            var mousePos = getMousePos(evt, 'mousedown', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            focusInput(input);
            sendInput();
            
            evt.preventDefault();
            evt.stopPropagation();
            
            return false;
        }
        
        function handleDblClick(evt, input) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
            var mousePos = getMousePos(evt, 'dblclick', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            focusInput(input);
            sendInput();
            
            evt.preventDefault();
            evt.stopPropagation();
            
            return false;
        }
        
        function handleMouseMove(evt) {
        	if (isNotValidCanvasTarget(evt) && mouseDownCanvas == null) {
        		return;
        	}
        	
            var mousePos = getMousePos(evt, 'mousemove', mouseDownCanvas != null ? mouseDownCanvas : evt.target);
            latestMouseMoveEvent = mousePos;
            
            if (isNotValidCanvasTarget(evt) && mouseDownCanvas != null) {
            	// prevent firing mouse move events on underlying html components if dragging webswing component and mouse gets out of canvas bounds
            	// this can happen when you quickly move a webswing dialog window over an html element (using compositing window manager)
        		evt.preventDefault();
        		evt.stopPropagation();
        	}
            
            return false;
        }
        
        function handleMouseUp(evt, input) {
        	// do this for the whole document, not only canvas
            var mousePos = getMousePos(evt, 'mouseup', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            
            if (evt.target && evt.target.matches && evt.target.matches("canvas.webswing-canvas") && mouseDownCanvas != null) {
            	// focus input only in case mouse was pressed and released over canvas
            	focusInput(input);
        	}
            
            sendInput();
            
            mouseDown = mouseDown & ~Math.pow(2, evt.which);
            mouseDownButton = 0;
            resetMouseDownCanvas();
            return false;
        }
        
        function handleWheel(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
            pixelsScrolled += util.detectFF() ? evt.deltaY * 100 : evt.deltaY
            if (pixelsScrolled >= 100 || pixelsScrolled <= -100) {
                pixelsScrolled = 0;
                var mousePos = getMousePos(evt, 'mousewheel', evt.target);
                latestMouseMoveEvent = null;
                if (latestMouseWheelEvent != null) {
                    mousePos.mouse.wheelDelta += latestMouseWheelEvent.mouse.wheelDelta;
                }
                latestMouseWheelEvent = mousePos;
            }
            //evt.preventDefault();
            evt.stopPropagation();
            return false;
        }
        
        function handleContextMenu(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
        	evt.preventDefault();
        	evt.stopPropagation();
            return false;
        }
        
        function handleKeyDown(evt) {
        	if (isNotValidCanvasTarget(evt) && isNotValidInputHandlerTarget(evt)) {
        		return;
        	}
        	
        	keyDownHandler(evt);
        }
        
        function handleKeyPress(evt) {
        	if (isNotValidCanvasTarget(evt) && isNotValidInputHandlerTarget(evt)) {
        		return;
        	}
        	
        	keyPressHandler(evt);
        }
        
        function handleKeyUp(evt) {
        	handleAccessibilityAccessKeys(evt);
        	
        	if (isNotValidCanvasTarget(evt) && isNotValidInputHandlerTarget(evt)) {
        		return;
        	}
        	
        	keyUpHandler(evt);
        }
        
        function handleCompositionStart(event, input) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
        	compositionInput = true;
        	$(input).addClass('ws-input-ime');
        	$(input).removeClass('ws-input-hidden');
        	input.style.font = DEFAULT_FONT;
        }
        
        function handleCompositionUpdate(event, input) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
        	var text = event.data;
        	input.style.width = getTextWidth(text, DEFAULT_FONT) + 'px';
        }
        
        function handleCompositionEnd(event, input) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
        	$(input).addClass('ws-input-hidden');
        	$(input).removeClass('ws-input-ime');
        	input.style.width = '1px';
        	var isIE = api.cfg.ieVersion && api.cfg.ieVersion <= 11;
        	sentWordsUsingKeypressEvent(isIE ? event.target.value : event.data);
        	compositionInput = false;
        	focusInput(input);
        }
        
        function handleInput(event, input) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
        	if (compositionInput || !input.value) {
        		return;
        	}
        	if (((!event.isComposing && event.inputType =='insertText' && event.data != null)
        			|| (api.cfg.ieVersion && event.type === "input"))
        			&& input.value != " ") {
        		sentWordsUsingKeypressEvent(input.value);
        		focusInput(input);
        	}
        }
        
        function handleCut(event) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	event.preventDefault();
        	event.stopPropagation();
        	api.cut(event);
        	return false;
        }
        
        function handleCopy(event) {
        	event.preventDefault();
        	event.stopPropagation();
        	api.copy(event);
        	return false;
        }
        
        function handlePaste(event) {
        	if (isNotValidInputHandlerTarget(event)) {
        		return;
        	}
        	
        	event.preventDefault();
        	event.stopPropagation();
        	api.paste(event, false);
        	return false;
        }
        
        function keyDownHandler(event) {
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
            var functionKeys = [9/*tab*/, 12/*Numpad5*/, 16/*Shift*/, 17/*ctrl*/, 18/*alt*/, 19/*pause*/, 20/*CapsLock*/, 27/*esc*/,
                32/*space*/, 33/*pgup*/, 34/*pgdown*/, 35/*end*/, 36/*home*/, 37/*left*/, 38/*up*/, 39/*right*/, 40/*down*/, 44/*prtscr*/,
                45/*insert*/, 46/*Delete*/, 91/*OSLeft*/, 92/*OSRight*/, 93/*Context*/, 145/*scrlck*/, 225/*altGraph(Linux)*/,
                112/*F1*/, 113/*F2*/, 114/*F3*/, 115/*F4*/, 116/*F5*/, 117/*F6*/, 118/*F7*/, 119/*F8*/, 120/*F9*/,
                121/*F10*/, 122/*F11*/, 123/*F12*/, 124/*F13*/, 125/*F14*/, 126/*F15*/, 127/*F16*/, 128/*F17*/, 129/*F18*/, 130/*F19*/, 131/*F20*/,
                132/*F21*/, 133/*F22*/, 134/*F23*/, 135/*F24*/];

            var kc = event.keyCode;
            if (functionKeys.indexOf(kc) != -1) {
                if (!api.cfg.virtualKB && !isInputCaretControl(event.keyCode, event.target)) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
            var keyevt = getKBKey('keydown', event);
            if (!isClipboardEvent(keyevt)) { // cut copy paste handled separately
                // default action prevented
                if (isCtrlCmd(keyevt) && !keyevt.key.alt) {
                    event.preventDefault();
                }

                if(latestKeyDownEvent!=null){
                    enqueueInputEvent(latestKeyDownEvent);
                    latestKeyDownEvent=null;
                    clearTimeout(latestKeyDownEventSendTimeout);
                    latestKeyDownEventSendTimeout=null
                }
                latestKeyDownEvent = keyevt;
                latestKeyDownEventSendTimeout = setTimeout(function(){
                    enqueueInputEvent(latestKeyDownEvent);
                    latestKeyDownEvent=null;
                },100);

                //generate keypress event for alt+key events
                if (!util.detectMac() && !keyevt.key.ctrl && keyevt.key.alt && functionKeys.indexOf(kc) == -1) {
                    event.preventDefault();
                    event.stopPropagation();
                    keyevt = getKBKey('keypress', event);
                    if (event.key.length == 1) {
                        keyevt.key.character = event.key.charCodeAt(0);
                    } else {
                        var key = keyevt.key.keycode;
                        keyevt.key.character = (!keyevt.key.shift && (key >= 65 && key <= 90)) ? key + 32 : key;
                    }
                    enqueueInputEvent(keyevt);
                }
            }
            return false;
        }
        
        function isInputCaretControl(kc, input) {
        	if ($(input).is("[aria-hidden=true]")) {
        		return false;
        	}
        	return caretControlKeys.indexOf(kc) != -1;
        }

        function keyPressHandler(event) {
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
            var keyevt = getKBKey('keypress', event);
            if (!isClipboardEvent(keyevt)) { // cut copy paste handled separately
                    event.preventDefault();
                    event.stopPropagation();
                if (latestKeyDownEvent != null) {
                    latestKeyDownEvent.key.character = keyevt.key.character;
                    enqueueInputEvent(latestKeyDownEvent);
                    latestKeyDownEvent=null;
                    clearTimeout(latestKeyDownEventSendTimeout);
                    latestKeyDownEventSendTimeout=null
                }
                enqueueInputEvent(keyevt);
            }

            if (util.detectMac()){
                keyevt.key.alt = false
            }

            return false;
        }
        
        function keyUpHandler(event) {
        	if (api.cfg.touchMode) {
        		return;
        	}
            var keyevt = getKBKey('keyup', event);

            if(latestKeyDownEvent!=null && latestKeyDownEvent.key.keycode === keyevt.key.keycode){
                enqueueInputEvent(latestKeyDownEvent);
                latestKeyDownEvent=null;
                clearTimeout(latestKeyDownEventSendTimeout);
                latestKeyDownEventSendTimeout=null
            }

            if (!isClipboardEvent(keyevt)) { // cut copy paste handled separately
            	if (!api.cfg.virtualKB && !isInputCaretControl(event.keyCode, event.target)) {
                    event.preventDefault();
                    event.stopPropagation();
            	}
                enqueueInputEvent(keyevt);
                sendInput();
            }
            return false;
        }
        
        function isClipboardEvent(evt) {
            var ctrlCmd = isCtrlCmd(evt);

            var isCutCopyKey = 'keypress' === evt.key.type ? (evt.key.character == 120 || evt.key.character == 24 || evt.key.character == 99) : (evt.key.character == 88 || evt.key.character == 67);
            var isCutCopyEvt = ctrlCmd && !evt.key.alt && !evt.key.altgr && !evt.key.shift && isCutCopyKey;

            var isPasteKey = 'keypress' === evt.key.type ? (evt.key.character == 118 || evt.key.character == 22 || evt.key.character == 86) : (evt.key.character == 86);
            var isPasteEvt = ctrlCmd && !evt.key.alt && !evt.key.altgr && !evt.key.shift && isPasteKey;

            return isPasteEvt || isCutCopyEvt;
        }

        function isCtrlCmd(evt) {
            return api.cfg.isMac ? evt.key.meta : evt.key.ctrl;
        }
        
        function getTextWidth(text, font) {
            var canvas = api.getCanvas();
            var ctx = canvas.getContext("2d");
            ctx.save();
            ctx.font = font;
            var metrics = ctx.measureText(text);
            ctx.restore();
            return Math.ceil(metrics.width) + 5;
        }

        function isWebswingCanvas(e) {
            return e && e.matches && e.matches("canvas.webswing-canvas");
        }
        
        function isInputHandler(e) {
        	return e && e.matches && (e.matches("input.ws-input-hidden") || e.matches("input.ws-input-ime") || e.matches("input.aria-element") || e.matches("textarea.aria-element"));
        }
        
        function isInsideWebswingHtmlCanvas(e) {
            while (e && e.matches && e !== window) {
                if (e.matches("div.webswing-html-canvas")) {
                   return e;
                }
                e = e.parentNode;
            }
            return false;
        }
        
        function isAriaElement(e) {
        	return e && e.matches && e.matches(".aria-element");
        }
        
        function handleAccessibilityAccessKeys(event) {
        	if (ctrl3Timeout != null) {
        		clearTimeout(ctrl3Timeout);
        		ctrl3Timeout = null;
        	}
        	
        	var now = new Date().getTime();
        	if (event.keyCode == 17) {
        		// accessibility CTRL access key
        		if (now - lastCtrlKey < 1000) {
        			ctrlCounter++;
        		} else {
        			ctrlCounter = 0;
        		}
        		
                lastCtrlKey = now;
        	} else {
        		ctrlCounter = 0;
        	}
        	
        	if ((ctrlCounter + 1) == 3 && api.isAccessibilityEnabled()) {
        		ctrl3Timeout = setTimeout(function() {
        			ctrlCounter = 0;
        			sendInput({
        				event: {
        					type: "requestWindowSwitchList"
        				}
        			});
        		}, 1000);
        	}
        	
        	if ((ctrlCounter + 1) == 5) {
        		api.toggleAccessibility();
        		ctrlCounter = 0;
        	}
        }

        function setMouseDownCanvas(evt) {
            mouseDownCanvas = isWebswingCanvas(evt.target) ? evt.target : null;
            if (isWebswingCanvas(evt.target)) {
                $('.webswing-html-canvas iframe').addClass('webswing-iframe-muted-while-dragging');
            }
        }

        function resetMouseDownCanvas() {
            mouseDownCanvas = null;
            $('.webswing-html-canvas iframe').removeClass('webswing-iframe-muted-while-dragging');
        }


        function mouseDownEventHandler(evt) {
            mouseDown = mouseDown | Math.pow(2, evt.which);
            mouseDownButton = evt.which;
            setMouseDownCanvas(evt);
        }

        function mouseOutEventHandler(evt, input) {
        	if (isWebswingCanvas(evt.target) || isWebswingCanvas(evt.relatedTarget) || mouseDownCanvas != null) {
        		return;
        	}
        	
        	mouseOutEventHandlerImpl(evt, input);
        }
        
        function mouseOutEventHandlerImpl(evt, input) {
            if (mouseDown!==0 && api.cfg.hasControl && api.cfg.canPaint && !api.cfg.mirrorMode && !api.cfg.touchMode && !compositionInput) {
                var mousePos = getMousePos(evt, 'mouseup', evt.target);
                // when a new web page pops after user click, mouseup will send twice
                var canvasSize=api.getCanvas().getBoundingClientRect();

                if(mousePos.mouse.x<0 || mousePos.mouse.x>canvasSize.width ){
                    mousePos.mouse.x = -1;
                }
                if(mousePos.mouse.y<0 || mousePos.mouse.y>canvasSize.height ){
                    mousePos.mouse.y = -1;
                }
                latestMouseMoveEvent = null;
                enqueueInputEvent(mousePos);
                focusInput(input);
                sendInput();
            }
            mouseDown = 0;
            mouseDownButton = 0;
            resetMouseDownCanvas();
        }
        
        function mouseOverEventHandler(evt) {
        	var newMouseDown = Math.pow(2, evt.which);
        	if (mouseDownButton !== 0 && mouseDownButton != evt.which && mouseDown != newMouseDown) {
        		// mouse has been released outside window (iframe)
        		let mousePos = getMousePos(evt, 'mouseup', evt.target);
        		// simulate release of previously pressed mouse button
        		mousePos.mouse.buttons = mouseDown;
        		mousePos.mouse.button = mouseDownButton;
        		
        		latestMouseMoveEvent = null;
        		enqueueInputEvent(mousePos);
        		
        		sendInput();
        		
        		mouseDown = 0;
        		mouseDownButton = 0;
        		resetMouseDownCanvas();
        	}
        }
        
        function isNotValidCanvasTarget(evt) {
        	return !(isWebswingCanvas(evt.target) || isAriaElement(evt.target));
        }
        
        function isNotValidInputHandlerTarget(evt) {
        	return !isInputHandler(evt.target);
        }

        function focusInput(input) {
            api.focusInput(input);
        }
        
        function sentWordsUsingKeypressEvent(data) {
            for (var i = 0, length = data.length; i < length ;i++) {
                inputEvtQueue.push({
                    key : {
                        type : 'keypress',
                        character : data.charCodeAt(i),
                        keycode : 0,
                    }
                });
            }
        }

        function getMousePos(evt, type, targetElement) {
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
            if (targetElement && targetElement.matches && targetElement.matches("canvas.webswing-canvas") && $(targetElement).data("id") && $(targetElement).data("id") != "canvas") {
            	// for a composition canvas window send winId
            	if ($(targetElement).is(".internal")) {
            		// internal window must use its parent as mouse events target
            		if ($(targetElement.parentNode).data("ownerid")) {
            			winId = $(targetElement.parentNode).data("ownerid");
            		}
            	} else {
            		winId = $(targetElement).data("id");
            		
            		targetElement.ownerDocument.defaultView.pagePosition = {x: evt.screenX - evt.clientX, y: evt.screenY - evt.clientY};
            	}
            }

            // return relative mouse position
            var mouseX = Math.round(evt.clientX - rect.left);
            var mouseY = Math.round(evt.clientY - rect.top);
            
            var delta = 0;
            if (type == 'mousewheel') {
                if (util.detectFF()) {
                    delta = -Math.max(-1, Math.min(1, (-evt.deltaY * 100)));
                } else if (util.detectIE() <= 11) {
                    delta = -Math.max(-1, Math.min(1, (-evt.deltaY)));
                } else {
                    delta = -Math.max(-1, Math.min(1, (evt.wheelDelta || -evt.detail)));
                }
            }
            
            if (type == 'mouseup' && (!targetElement || !targetElement.matches || !targetElement.matches("canvas.webswing-canvas"))) {
            	// fix for detached composition canvas windows that might overlay same coordinates space, when clicked outside a canvas
            	mouseX = -1;
            	mouseY = -1;
            }
            
            if (type == 'mouseup' && targetElement && targetElement.matches && !targetElement.matches("canvas.webswing-canvas") && targetElement.closest(".webswing-html-canvas") != null) {
            	// fix for mouseup over an HtmlWindow div content
            	rect = targetElement.closest(".webswing-html-canvas").parentNode.getBoundingClientRect();
            	
            	mouseX = Math.round(evt.clientX - rect.left);
            	mouseY = Math.round(evt.clientY - rect.top);
            }
            
            return {
                mouse: {
                    x: mouseX,
                    y: mouseY,
                    type: type,
                    wheelDelta: delta,
                    button: type == 'mousemove' ? 0 : evt.which,
                    buttons: mouseDown,
                    ctrl: evt.ctrlKey,
                    alt: evt.altKey,
                    shift: evt.shiftKey,
                    meta: evt.metaKey,
                    winId: winId || "",
                    timeMilis: Date.now() % 86400000 //86400000 day in milis
                }
            };
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

    }