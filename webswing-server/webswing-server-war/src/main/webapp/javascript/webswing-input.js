
    "use strict";

    export default function InputModule(util) {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            send: 'socket.send',
            getInput: 'canvas.getInput',
            focusInput: 'canvas.focusInput',
            getCanvas: 'canvas.get',
            cut: 'clipboard.cut',
            copy: 'clipboard.copy',
            paste: 'clipboard.paste'
        };
        module.provides = {
            register: register,
            sendInput: sendInput,
            dispose: dispose
        };

        module.ready = function () {
        };

        var registered = false;
        var latestMouseMoveEvent = null;
        var latestMouseWheelEvent = null;
        var latestWindowResizeEvent = null;
        var latestKeyDownEvent = null;
        var mouseDown = 0;
        var mouseDownCanvas = null;
        var inputEvtQueue = [];
        var pixelsScrolled = 0;
        var compositionInput = false;

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
                if (latestKeyDownEvent != null) {
                    inputEvtQueue.push(latestKeyDownEvent);
                    latestKeyDownEvent = null;
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
            mouseDown = 0;
            mouseDownCanvas = null;
            inputEvtQueue = [];
        }

        function dispose() {
            registered = false;
            resetInput();
            document.removeEventListener('mousedown', mouseDownEventHandler);
            document.removeEventListener('mouseout', mouseOutEventHandler);

            document.removeEventListener('mousedown', handleMouseDown, false);
            document.removeEventListener('dblclick', handleDblClick, false);
            document.removeEventListener('mousemove', handleMouseMove, false);
            document.removeEventListener('mouseup', handleMouseUp, false);
            document.removeEventListener("wheel", handleWheel, false);
            document.removeEventListener('contextmenu', handleContextMenu);

            document.removeEventListener("keydown", handleKeyDown, false);
            document.removeEventListener("keypress", handleKeyPress, false);
            document.removeEventListener("keyup", handleKeyUp, false);
        }

        function register() {
            if (registered) {
                return;
            }
            
            var input = api.getInput();
            resetInput();
            focusInput();

            util.bindEvent(document, 'mousedown', handleMouseDown, false);
            util.bindEvent(document, 'dblclick', handleDblClick, false);
            util.bindEvent(document, 'mousemove', handleMouseMove, false);
            util.bindEvent(document, 'mouseup', handleMouseUp, false);
            util.bindEvent(document, "wheel", handleWheel, false);
            util.bindEvent(document, 'contextmenu', handleContextMenu);

            util.bindEvent(input, 'keydown', keyDownHandler, false);
            util.bindEvent(document, 'keydown', handleKeyDown, false);
            util.bindEvent(input, 'keypress', keyPressHandler, false);
            util.bindEvent(document, 'keypress', handleKeyPress, false);
            util.bindEvent(input, 'keyup', keyUpHandler, false);
            util.bindEvent(document, 'keyup', handleKeyUp, false);

            util.bindEvent(document, 'mousedown', mouseDownEventHandler);
            util.bindEvent(document, 'mouseout', mouseOutEventHandler);

            var DEFAULT_FONT = '14px sans-serif';
            
            util.bindEvent(input, 'compositionstart', function(event) {
            	if (api.cfg.touchMode) {
            		return;
            	}
            	
                compositionInput = true;
                var input=api.getInput();
                $(input).addClass('ws-input-ime');
                $(input).removeClass('ws-input-hidden');
                input.style.font = DEFAULT_FONT;
            }, false);
            
            util.bindEvent(input, 'compositionupdate', function(event) {
            	if (api.cfg.touchMode) {
            		return;
            	}
            	
            	var input=api.getInput();
            	var text = event.data;
            	input.style.width = getTextWidth(text, DEFAULT_FONT)+'px';
            }, false);
            
            util.bindEvent(input, 'compositionend', function (event) {
            	if (api.cfg.touchMode) {
            		return;
            	}
            	
                var input = api.getInput();
                $(input).addClass('ws-input-hidden');
                $(input).removeClass('ws-input-ime');
                input.style.width = '1px';
                var isIE = api.cfg.ieVersion && api.cfg.ieVersion <= 11;
                sentWordsUsingKeypressEvent(isIE ? event.target.value : event.data);
                compositionInput = false;
                focusInput();
            }, false);
            
            util.bindEvent(input, 'input', function(event) {
            	if (api.cfg.touchMode) {
            		return;
            	}
            	
                var input = api.getInput();
                if(compositionInput || !input.value){
                    return;
                }
                if (((!event.isComposing && event.inputType =='insertText' && event.data!=null)
                    || (api.cfg.ieVersion && event.type ==="input"))
                    && input.value != " "
                ) {
                   sentWordsUsingKeypressEvent(input.value)
                    focusInput();
                }
            }, false);
            
            util.bindEvent(input, 'cut', function (event) {
                event.preventDefault();
                event.stopPropagation();
                api.cut(event);
                return false;
            }, false);
            util.bindEvent(input, 'copy', function (event) {
                event.preventDefault();
                event.stopPropagation();
                api.copy(event);
                return false;
            }, false);
            util.bindEvent(input, 'paste', function (event) {
                event.preventDefault();
                event.stopPropagation();
                api.paste(event, false);
                return false;
            }, false);

            registered = true;
        }
        
        function handleMouseDown(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
            mouseDownEventHandler(evt);
            var mousePos = getMousePos(evt, 'mousedown', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            focusInput();
            sendInput();
            return false;
        }
        
        function handleDblClick(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
            var mousePos = getMousePos(evt, 'dblclick', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            focusInput();
            sendInput();
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
        
        function handleMouseUp(evt) {
        	// do this for the whole document, not only canvas
            var mousePos = getMousePos(evt, 'mouseup', evt.target);
            latestMouseMoveEvent = null;
            enqueueInputEvent(mousePos);
            
            if (evt.target && evt.target.matches && evt.target.matches("canvas.webswing-canvas") && mouseDownCanvas != null) {
            	// focus input only in case mouse was pressed and released over canvas
            	focusInput();
        	}
            
            sendInput();
            
            mouseDown = mouseDown & ~Math.pow(2, evt.which);
            mouseDownCanvas = null;

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
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
        	keyDownHandler(evt);
        }
        
        function handleKeyPress(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
        	keyPressHandler(evt);
        }
        
        function handleKeyUp(evt) {
        	if (isNotValidCanvasTarget(evt)) {
        		return;
        	}
        	
        	keyUpHandler(evt);
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
                if (!api.cfg.virtualKB) {
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
                if (latestKeyDownEvent != null) {
                    enqueueInputEvent();//so we dont lose the previous keydown
                }
                latestKeyDownEvent = keyevt;

                //generate keypress event for alt+key events
                if (keyevt.key.alt && functionKeys.indexOf(kc) == -1) {
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
                }
                enqueueInputEvent(keyevt);
            }
            return false;
        }
        
        function keyUpHandler(event) {
        	if (api.cfg.touchMode) {
        		return;
        	}
        	
        	var keyevt = getKBKey('keyup', event);
            if (!isClipboardEvent(keyevt)) { // cut copy paste handled separately
                event.preventDefault();
                event.stopPropagation();
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

        function mouseDownEventHandler(evt) {
            mouseDown = mouseDown | Math.pow(2, evt.which);
            mouseDownCanvas = (evt.target && evt.target.matches && evt.target.matches("canvas.webswing-canvas")) ? evt.target : null;
        }

        function mouseOutEventHandler(evt) {
        	if (((evt.target && evt.target.matches && evt.target.matches("canvas.webswing-canvas"))
        			|| (evt.relatedTarget && evt.relatedTarget.matches && evt.relatedTarget.matches("canvas.webswing-canvas"))) || mouseDownCanvas != null) {
        		return;
        	}
        	
        	mouseOutEventHandlerImpl(evt);
        }
        
        function mouseOutEventHandlerImpl(evt) {
            if (mouseDown!==0 && api.cfg.hasControl && api.cfg.canPaint && !api.cfg.mirrorMode && !api.cfg.touchMode && !compositionInput) {
                // TODO is evt.target correct
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
                focusInput();
                sendInput();
            }
            mouseDown = 0;
        }
        
        function isNotValidCanvasTarget(evt) {
        	return !evt.target || !evt.target.matches || !evt.target.matches("canvas.webswing-canvas");
        }

        function focusInput() {
            if (api.cfg.touchMode) {
                return;
            }
            
            var input = api.getInput();

            if(util.detectIE() && util.detectIE() <= 11){
                input.blur(); //fix issue when compositionend causes focus to be lost in IE
            }
            // scrollX , scrollY attributes on IE gives undefined, so changed to compatible pageXOffset,pageYOffset
            let sx = window.pageXOffset, sy = window.pageYOffset;
            // In order to ensure that the browser will fire clipboard events, we always need to have something selected
            input.value = ' ';
            // set the style attributes as the focus/select cannot work well in IE
            util.focusWithPreventScroll(input, true)
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
                rect = targetElement.parentNode.getBoundingClientRect();
            } else {
                rect = api.getCanvas().getBoundingClientRect();
            }
            
            var winId;
            if (targetElement && targetElement.matches("canvas.webswing-canvas") && $(targetElement).data("id") && $(targetElement).data("id") != "canvas") {
            	// for a composition canvas window send winId
                winId = $(targetElement).data("id");
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
            
            if (type == 'mouseup' && targetElement && targetElement.matches && targetElement.closest(".webswing-html-canvas") != null) {
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