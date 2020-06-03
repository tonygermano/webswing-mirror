"use strict";
    export default function FocusManager(util) {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            getFocusedWindow: 'base.getFocusedWindow',
            touchInputFocusGained: 'touch.inputFocusGained',
            updateFocusedHtmlCanvas: 'input.updateFocusedHtmlCanvas'
        };
        module.provides = {
        	manageFocusEvent: manageFocusEvent,
        	focusInput: focusInput,
        	focusDefault: focusDefault
        };
        
        function manageFocusEvent(focusEvent) {
        	manageFocusEventRetry(focusEvent, 0);
        }
        
        function manageFocusEventRetry(focusEvent, retryCount) {
        	var doc = $(api.getFocusedWindow().document);
        	var input = doc.find(".ws-input-hidden")[0];
        	
        	if (input) {
        		doManageFocusEvent(focusEvent);
        		return;
        	}
        	
    		// in some cases there is a race condition on input being available right after window is undocked
    		if (retryCount < 4) {
	    		setTimeout(function() {
	    			manageFocusEventRetry(focusEvent, retryCount + 1);
	    		}, 50);
    		}
        }
        
        function doManageFocusEvent(focusEvent) {
        	var doc = $(api.getFocusedWindow().document);
        	var input = doc.find(".ws-input-hidden")[0];
        	
        	input.classList.remove('editable');
            input.classList.remove('focused-with-caret');
            
            if ((focusEvent.type === 'focusWithCarretGained' || focusEvent.type === 'focusPasswordGained') && (focusEvent.x + focusEvent.w > 0) && (focusEvent.y + focusEvent.h > 0)) {
                if (focusEvent.type === 'focusPasswordGained') {
                	input.type = 'password';
                } else {
                	input.type = 'text';
                }
                
                updateInputPosition(input, focusEvent);
                input.classList.add('focused-with-caret');
                if (focusEvent.editable) {
                	input.classList.add('editable');
                }
                api.touchInputFocusGained();
            } else {
                updateInputPosition(input, null);
                input.value = '';
            }
            
            focusDefault();
        }
        
        function focusInput(input) {
        	// legacy function from webswing-input
        	// this should not be needed and focus should always be correctly handled by focusDefault()
        	
        	if (api.cfg.touchMode) {
                return;
            }
            
            if (util.detectIE() && util.detectIE() <= 11) {
                input.blur(); //fix issue when compositionend causes focus to be lost in IE
            }
            
            if (isFocusManaged()) {
            	// ignore, do not focus the hidden input
            	// focus is handled by accessibility
            } else {
            	// scrollX , scrollY attributes on IE gives undefined, so changed to compatible pageXOffset,pageYOffset
            	// let sx = window.pageXOffset, sy = window.pageYOffset;
            	// TODO ??? -> In order to ensure that the browser will fire clipboard events, we always need to have something selected
            	input.value = ' ';
            	// set the style attributes as the focus/select cannot work well in IE
            	// input.style.top = sy +'px';
            	// input.style.left = sx +'px';
            	focusWithPreventScroll(input, true);
            	input.select();
            	// window.scrollTo(sx,sy);
            }
        }
        
        function focusDefault() {
        	var doc = api.getFocusedWindow().document;
        	var focusedElement = $(doc.activeElement);
        	
        	if (focusedElement.length && focusedElement.is(".catch-focus") || focusedElement.parents(".catch-focus").length) {
        		// focus managed by a dialog or other element which catches the focus
        		return;
        	}
        	
        	var ariaEl = $(doc).find(".webswing-element-content .aria-element.focusable");
        	if (ariaEl.length) {
        		// focus aria element
        		ariaEl[0].focus({preventScroll: true});
        	} else {
        		// focus input
            	var input = $(doc).find(".ws-input-hidden")[0];
        		
        		api.updateFocusedHtmlCanvas(input);
        		focusWithPreventScroll(input);
        	}
        }
        
        function isFocusManaged() {
        	// if focus is managed, we should not try to change the focus externally
        	
        	var doc = api.getFocusedWindow().document;
        	var focusedElement = $(doc.activeElement);
        	var aria = $(doc).find(".webswing-element-content .aria-element.focusable");
        	
        	return aria.length > 0 || (focusedElement.length && focusedElement.is(".catch-focus") || focusedElement.parents(".catch-focus").length);
        }
        
        function focusWithPreventScroll(focusElement, selectInput) {
            if (focusElement) {
                var temppos = focusElement.style.position;
                var templeft = focusElement.style.left;
                var temptop = focusElement.style.top;
                if (util.detectIE() && util.detectIE() <= 11) {// prevent scroll does not work in IE
                    focusElement.style.position = 'fixed';
                    focusElement.style.left = '0px';
                    focusElement.style.top = '0px'
                }
                focusElement.focus({preventScroll: true});
                if (selectInput) {
                    focusElement.select();
                }
                if (util.detectIE() && util.detectIE() <= 11) {
                    focusElement.style.position = temppos;
                    focusElement.style.left = templeft;
                    focusElement.style.top = temptop;
                }
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
        
    }
