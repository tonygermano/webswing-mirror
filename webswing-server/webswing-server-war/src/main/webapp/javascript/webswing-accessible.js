import switcherTemplate from './templates/windowSwitcher.html'
"use strict";
    export default function Accessible() {
        var module = this;
        var api;
        var switcherHtml;
        module.injects = api = {
            cfg: 'webswing.config',
            send: 'socket.send',
            translate: 'translate.translate',
            sendHandshake: 'base.handshake',
            getFocusedWindow: 'base.getFocusedWindow',
            getAllWindows: 'base.getAllWindows',
            closeWindow: 'base.closeWindow',
            focusDefault: 'focusManager.focusDefault'
        };
        module.provides = {
        	dispose: dispose,
        	toggle: toggle,
        	isEnabled: isEnabled,
            handleAccessible: handleAccessible,
            showWindowSwitcher: showWindowSwitcher
        };
        module.ready = function () {
        	if (window.localStorage) {
        		var previousState = localStorage.getItem('accessibilityEnabled');
        		if (previousState == 'true') {
        			toggle(true);
        		}
        	}
        	switcherHtml = api.translate(switcherTemplate);
        }
        
        var enabled = false;
        var ariaBusyTimeout = null;
        var windowSwitcher = null;

        function toggle(silent) {
        	enabled = !enabled;
        	api.sendHandshake();
        	
        	if (enabled) {
        		if (!silent) {
        			localStorage.setItem('accessibilityEnabled', true);
        			init();
        		}
        	} else {
        		localStorage.setItem('accessibilityEnabled', false);
        		destroy();
        	}
        }
        
        function init() {
        	var rootElement = $(api.getFocusedWindow().document).find(".webswing-element-content");
        	var accessDiv = getLiveLog(rootElement);
        	setTimeout(function() {
        		rootElement.find("#aria-live-log").text(api.translate('accessibility.turnedOn'));
        	}, 1);
        }
        
        function dispose() {
        	removeAllAriaElements();
        }
        
        function destroy() {
        	var rootElement = $(api.getFocusedWindow().document).find(".webswing-element-content");
        	getLiveLog(rootElement).text(api.translate('accessibility.turnedOff'));
        	
        	removeAllAriaElements();
        	
        	api.focusDefault();
        }
        
        function removeAllAriaElements() {
        	var wins = api.getAllWindows();
        	for (var i=0; i<wins.length; i++) {
        		$(wins[i].document).find(".aria-element, .aria-parent").remove();
        	}
        }
        
        function getLiveLog(rootElement) {
        	if (rootElement.find("#aria-live-log").length == 0) {
        		rootElement.append('<div id="aria-live-log" aria-live="assertive" role="log" data-id="access" tabindex="-1" />');
        	}
        	
        	return rootElement.find("#aria-live-log");
        }
        
        function isEnabled() {
        	return enabled;
        }
        
        function handleAccessible(accessible, rootElement) {
        	if (!enabled) {
        		return;
        	}
        	
        	if (ariaBusyTimeout != null) {
        		clearTimeout(ariaBusyTimeout);
        	}
        	
        	var delay = 250;
        	if (accessible.role == "textbox") {
        		delay = 1;
        	}
        	
        	ariaBusyTimeout = setTimeout(function() {
        		ariaBusyTimeout = null;
        		
        		accessible.rootElement = rootElement;
        		
        		switch (accessible.role) {
        			case "button":
        				handleButton(accessible);
        				break;
        			case "decorationbutton":
        				handleDecorationbutton(accessible);
        				break;
        			case "checkbox":
        				handleCheckbox(accessible);
        				break;
        			case "menuitemcheckbox":
        				handleMenuitemcheckbox(accessible);
        				break;
        			case "combobox":
        				handleCombobox(accessible);
        				break;
        			case "progressbar":
        				handleProgressbar(accessible);
        				break;
        			case "radio":
        				handleRadiobutton(accessible);
        				break;
        			case "menuitemradio":
        				handleMenuitemradio(accessible);
        				break;
        			case "scrollbar":
        				handleScrollbar(accessible);
        				break;
        			case "slider":
        				handleSlider(accessible);
        				break;
        			case "label":
        				handleLabel(accessible);
        				break;
        			case "tooltip":
        				handleTooltip(accessible);
        				break;
        			case "tab":
        			case "tablist":
        				handleTab(accessible);
        				break;
        			case "list":
        				handleList(accessible);
        				break;
        			case "listitem":
        				handleListitem(accessible);
        				break;
        			case "treeitem":
        				handleTreeitem(accessible);
        				break;
        				break;
        			case "gridcell":
        				handleGridcell(accessible);
        				break;
        			case "textbox":
        				handleTextbox(accessible);
        				break;
        			case "img":
        				handleImage(accessible);
        				break;
        			case "menuitem":
        				handleMenuitem(accessible);
        				break;
        			case "menu":
        			case "tree":
        			case "menubar":
        			case "table":
        			case "panel":
        			case "alertdialog":
        			case "dialog":
        				handleEmptyElement(accessible);
        				break;
        			default:
        				handleEmptyElement(accessible);
        				break;
        		}
        		
        		if (rootElement.find(".aria-element.aria-id.new").length > 0) {
//        			// remove old aria-element if there is a new one
        			rootElement.find(".aria-element.aria-id:not(.new)").remove();
        		}
        		rootElement.find(".aria-element.aria-id.new").removeClass("new");
        		
        		handleHierarchy(accessible);
        		
        		api.focusDefault();
        	}, delay);
        }
        
        function handleButton(accessible) {
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<button class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
    		el.attr("aria-label", accessible.text).text(accessible.text);
    		if (accessible.toggle) {
				el.attr("aria-pressed", accessible.states != null && accessible.states.indexOf('CHECKED') !== -1);
			} else {
				el.removeAttr("aria-pressed");
			}
    		
    		processAccessibleElement(el, accessible);
        }
        
        function handleDecorationbutton(accessible) {
        	accessible.role = "button";
        	accessible.text = api.translate(accessible.text);
        	
        	handleButton(accessible);
        }
        
        function handleCheckbox(accessible) {
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
			el.attr("aria-label", accessible.text).text(accessible.text);
			el.attr("aria-checked", accessible.states != null && accessible.states.indexOf('CHECKED') !== -1);
			
			processAccessibleElement(el, accessible);
        }
        
        function handleMenuitemcheckbox(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='0' id='aria-element-" + id + "' />");
        	el.attr("role", "menu");
        	el.attr("aria-owns", "aria-menuitemcheckbox");
        	el.removeClass("focusable");
        	
        	var itemEl = $("<div class='aria-element focusable' tabindex='0' id='aria-menuitemcheckbox' />");
        	itemEl.attr("role", "menuitemcheckbox");
        	
        	itemEl.attr("aria-label", accessible.text).text(accessible.text);
        	itemEl.attr("aria-checked", accessible.states != null && accessible.states.indexOf('CHECKED') !== -1);
        	itemEl.attr("aria-haspopup", false);
        	
			itemEl.attr("aria-setsize", accessible.size);
			itemEl.attr("aria-posinset", accessible.position);
        	
			// if enabled state is not present, it is disabled
			itemEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			el.append(itemEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, itemEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleCombobox(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
        	
			el.attr("aria-label", accessible.text).text(accessible.text);
			el.attr("aria-controls", "aria-listbox");
			el.attr("aria-haspopup", "listbox");
			el.attr("aria-expanded", accessible.states != null && accessible.states.indexOf('EXPANDED') !== -1);
			
			var listbox;
			if (el.find("#aria-listbox").length) {
				listbox = el.find("#aria-listbox");
				listbox.empty();
			} else {
				listbox = $("<ul role='listbox' id='aria-listbox' style='list-style-type: none;' />");
				el.append(listbox);
			}
			
			if (accessible.size > 0) {
				var item = $("<li id='aria-option' role='option' />");
				item.text(accessible.value);
				item.attr("aria-selected", true);
				item.attr("aria-setsize", accessible.size);
				item.attr("aria-posinset", accessible.position);
				listbox.append(item);
				
				el.attr("aria-activedescendant", "aria-option");
			}
			
			processAccessibleElement(el, accessible);
        }
        
        function handleTab(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
        	
        	el.attr("role", "tab");
        	el.attr("aria-label", accessible.text).text(accessible.text);
        	el.attr("aria-selected", accessible.states != null && accessible.states.indexOf('SELECTED') !== -1);
        	
			if (accessible.size > 0) {
				el.attr("aria-setsize", accessible.size);
				el.attr("aria-posinset", accessible.position);
			}
        	
			// if enabled state is not present, it is disabled
			el.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, el);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleProgressbar(accessible) {
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
			el.attr("aria-valuemin", accessible.min)
				.attr("aria-valuemax", accessible.max)
				.attr("aria-valuetext", accessible.val)
				.attr("aria-valuenow", accessible.val)
				.attr("aria-label", accessible.text);
			
			processAccessibleElement(el, accessible);
        }
        
        function handleRadiobutton(accessible) {
			var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
    			
			el.attr("aria-setsize", accessible.size);
			el.attr("aria-posinset", accessible.position);
			el.attr("aria-label", accessible.text).text(accessible.text);
			el.attr("aria-checked", accessible.states != null && accessible.states.indexOf('CHECKED') !== -1);
    			
			processAccessibleElement(el, accessible);
        }
        
        function handleMenuitemradio(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='0' id='aria-element-" + id + "' />");
        	el.attr("role", "menu");
        	el.attr("aria-owns", "aria-menuitemradio");
        	el.removeClass("focusable");
        	
        	var itemEl = $("<div class='aria-element focusable' tabindex='0' id='aria-menuitemradio' />");
        	itemEl.attr("role", "menuitemradio");
        	
        	itemEl.attr("aria-label", accessible.text).text(accessible.text);
        	itemEl.attr("aria-checked", accessible.states != null && accessible.states.indexOf('CHECKED') !== -1);
        	itemEl.attr("aria-haspopup", false);
        	
			itemEl.attr("aria-setsize", accessible.size);
			itemEl.attr("aria-posinset", accessible.position);
        	
			// if enabled state is not present, it is disabled
			itemEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			el.append(itemEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, itemEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleScrollbar(accessible) {
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
			el.attr("aria-valuemin", accessible.min)
				.attr("aria-valuemax", accessible.max)
				.attr("aria-valuenow", accessible.val);
			
			if (accessible.states != null && accessible.states.indexOf('VERTICAL') !== -1) {
				el.attr("aria-orientation", "vertical");
			} else if (accessible.states != null && accessible.states.indexOf('HORIZONTAL') !== -1) {
				el.attr("aria-orientation", "horizontal");
			}
			
			processAccessibleElement(el, accessible);
        }
        
        function handleSlider(accessible) {
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
			el.attr("aria-valuemin", accessible.min)
				.attr("aria-valuemax", accessible.max)
				.attr("aria-valuetext", accessible.val)
				.attr("aria-valuenow", accessible.val)
				.attr("aria-label", accessible.text);
			
			processAccessibleElement(el, accessible);
        }
        
        function handleLabel(accessible) {
        	// send text to aria live
        	getLiveLog(accessible.rootElement).text(accessible.text);
        }
        
        function handleTooltip(accessible) {
        	var text = accessible.text;
        	if (!text || text.length == 0) {
        		text = accessible.description;
        	}
        	if (text && text.length > 0) {
        		// send text to aria live
        		getLiveLog(accessible.rootElement).text(text);
        	}
        }
        
        function handleImage(accessible) {
        	var text = accessible.text;
        	if (!text || text.length == 0) {
        		text = accessible.description;
        	}
        	
        	var id = accessible.id;
			var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
			
			el.attr("aria-label", text);
			
			processAccessibleElement(el, accessible);
        }
        
        function handleTextbox(accessible) {
        	var id = accessible.id;
        	var el;
        	
        	if (accessible.states != null && accessible.states.indexOf('SINGLE_LINE') === -1) {
        		// multi line
        		el = findAriaElementById(id, accessible.rootElement) || $("<textarea class='aria-element aria-id new focusable' tabindex='0' spellcheck='false' id='aria-element-" + id + "'></textarea>");
        		el.attr("aria-multiline", true);
        		el[0].value = accessible.text;
        	} else {
        		// single line
       			if (accessible.password) {
       				el = findAriaElementById(id, accessible.rootElement) || $("<input type='password' class='aria-element aria-id new focusable' tabindex='0' spellcheck='false' autocomplete='off' id='aria-element-" + id + "'/>");
       			} else {
       				el = findAriaElementById(id, accessible.rootElement) || $("<input type='text' class='aria-element aria-id new focusable' tabindex='0' spellcheck='false' autocomplete='off' id='aria-element-" + id + "'/>");
       			}
       			
        		el.attr("aria-multiline", false);
        		el[0].value = accessible.text;
        	}
        	
        	if (accessible.rows) {
        		el.attr("rows", accessible.rows);
        		// TODO dpr ?
        		el.css("line-height", accessible.rowheight + 'px');
        	}

        	if (accessible.selstart >= 0) {
        		el[0].setSelectionRange(accessible.selstart, accessible.selend);
        	}
        	el.attr("aria-readonly", accessible.states != null && accessible.states.indexOf('EDITABLE') === -1);
        	
        	processAccessibleElement(el, accessible);
        }
        
        function handleMenuitem(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='0' id='aria-element-" + id + "' />");
        	el.attr("role", "menu");
        	el.attr("aria-owns", "aria-menuitem");
        	el.removeClass("focusable");
        	
        	var itemEl = $("<div class='aria-element focusable' tabindex='0' id='aria-menuitem' />");
        	itemEl.attr("role", "menuitem");
        	
        	itemEl.attr("aria-label", accessible.text).text(accessible.text);
    	
			itemEl.attr("aria-setsize", accessible.size);
			itemEl.attr("aria-posinset", accessible.position);
        	
			// if enabled state is not present, it is disabled
			itemEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			el.append(itemEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, itemEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleList(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
        	
        	el.attr("aria-label", accessible.text).text(accessible.text);
        	el.attr("aria-multiselectable", accessible.states != null && accessible.states.indexOf('MULTISELECTABLE') !== -1);
        	
        	processAccessibleElement(el, accessible);
        }
        
        function handleListitem(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='0' id='aria-element-" + id + "' />");
        	el.attr("role", "listbox");
        	el.attr("aria-owns", "aria-listitem");
        	el.removeClass("focusable");
        	el.attr("aria-multiselectable", accessible.states != null && accessible.states.indexOf('MULTISELECTABLE') !== -1);
        	
        	var itemEl = $("<div class='aria-element focusable' tabindex='0' id='aria-listitem' />");
        	itemEl.attr("role", "option");
        	
        	itemEl.attr("aria-label", accessible.text).text(accessible.text);
        	itemEl.attr("aria-selected", accessible.states != null && accessible.states.indexOf('SELECTED') !== -1);
        	
			itemEl.attr("aria-setsize", accessible.size);
			itemEl.attr("aria-posinset", accessible.position);
        	
			// if enabled state is not present, it is disabled
			itemEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			el.append(itemEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, itemEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleTreeitem(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='0' id='aria-element-" + id + "' />");
        	el.attr("role", "tree");
        	el.attr("aria-owns", "aria-treeitem");
        	el.removeClass("focusable");
        	el.attr("aria-multiselectable", accessible.states != null && accessible.states.indexOf('MULTISELECTABLE') !== -1);
        	
        	var itemEl = $("<div class='aria-element focusable' tabindex='0' id='aria-treeitem' />");
        	itemEl.attr("role", "treeitem");
        	
        	itemEl.attr("aria-label", accessible.text).text(accessible.text);
        	itemEl.attr("aria-selected", accessible.states != null && accessible.states.indexOf('SELECTED') !== -1);
        	itemEl.attr("aria-expanded", accessible.states != null && accessible.states.indexOf('EXPANDED') !== -1);
        	
    		itemEl.attr("aria-level", accessible.level);
			itemEl.attr("aria-setsize", accessible.size);
			itemEl.attr("aria-posinset", accessible.position);
        	
			// if enabled state is not present, it is disabled
			itemEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			el.append(itemEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, itemEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleGridcell(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new' tabindex='-1' id='aria-element-" + id + "' />");
        	el.attr("role", "grid");
        	el.attr("aria-owns", "aria-chrow aria-row");
        	el.removeClass("focusable");
        	
			el.attr("aria-rowcount", accessible.rowcount);
			el.attr("aria-colcount", accessible.colcount);
        	el.attr("aria-multiselectable", accessible.states != null && accessible.states.indexOf('MULTISELECTABLE') !== -1);
        	
        	var chRowEl = $("<div class='aria-element' tabindex='-1' id='aria-chrow' />");
        	chRowEl.attr("role", "row");
        	chRowEl.attr("aria-owns", "aria-columnheader");
        	
        	var chEl = $("<div class='aria-element' tabindex='-1' id='aria-columnheader' />");
        	chEl.attr("role", "columnheader");
        	
        	chEl.attr("aria-label", accessible.columnheader).text(accessible.columnheader);
        	chEl.attr("aria-colindex", accessible.colindex);
        	
        	var rowEl = $("<div class='aria-element' tabindex='-1' id='aria-row' />");
        	rowEl.attr("role", "row");
        	rowEl.attr("aria-owns", "aria-cell");
        	
        	var cellEl = $("<div class='aria-element focusable' tabindex='0' id='aria-cell' />");
        	cellEl.attr("role", "gridcell");
        	
        	cellEl.attr("aria-label", accessible.text).text(accessible.text);
        	cellEl.attr("aria-selected", accessible.states != null && accessible.states.indexOf('SELECTED') !== -1);
        	cellEl.attr("aria-readonly", accessible.states != null && accessible.states.indexOf('EDITABLE') === -1);
        	
    		cellEl.attr("aria-colindex", accessible.colindex);
			cellEl.attr("aria-rowindex", accessible.rowindex);
        	
			// if enabled state is not present, it is disabled
			cellEl.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.empty();
			chRowEl.append(chEl);
			el.append(chRowEl);
			rowEl.append(cellEl);
			el.append(rowEl);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, cellEl);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleEmptyElement(accessible) {
        	var id = accessible.id;
        	var el = findAriaElementById(id, accessible.rootElement) || $("<div class='aria-element aria-id new focusable' tabindex='0' id='aria-element-" + id + "' />");
        	
        	if (accessible.text) {
        		el.attr("aria-label", accessible.text).text(accessible.text);
        	}
        	
        	processAccessibleElement(el, accessible);
        }
        
        function findAriaElementById(id, rootElement) {
        	if (id) {
        		var ariaEl = rootElement.find("#aria-element-" + id);
        		if (ariaEl.length) {
        			return ariaEl;
        		}
        	}
        }
        
        function processAccessibleElement(el, accessible) {
       		el.attr("role", accessible.role);
			
			// if enabled state is not present, it is disabled
			el.attr("aria-disabled", accessible.states != null && accessible.states.indexOf('ENABLED') === -1);
			
			el.css({
				width: accessible.width,
				height: accessible.height,
				top: accessible.screenY + "px",
				left: accessible.screenX + "px"
			});
			
			handleElementDescription(accessible, el);
			
			if (el.is(".new")) {
				appendAriaElement(el, accessible.rootElement);
			}
        }
        
        function handleElementDescription(accessible, el) {
        	var describedBy = getAriaDescribedByElement(accessible.rootElement);
			if (accessible.tooltip || accessible.description) {
				describedBy.text(accessible.description || accessible.tooltip);
				el.attr("aria-describedby", "aria-describedby");
			} else {
				el.removeAttr("aria-describedby");
				describedBy.text("");
			}
        }
        
        function getAriaDescribedByElement(rootElement) {
        	if (rootElement.find("#aria-describedby").length == 0) {
        		rootElement.append($("<span id='aria-describedby' />"));
        	}
        	return rootElement.find("#aria-describedby");
        }
        
        function appendAriaElement(el, rootElement) {
        	var lastParent = rootElement.find(".aria-parent:last");
        	if (lastParent.length) {
        		lastParent.append(el);
        	} else {
        		rootElement.append(el);
        	}
        }
        
        function handleHierarchy(accessible) {
        	var hierarchy = accessible.hierarchy;
        	var ariaId = accessible.rootElement.find(".aria-element.aria-id");
        	
        	if (!hierarchy || hierarchy.length == 0) {
        		while (ariaId.parents(".aria-parent").length > 0) {
        			ariaId.unwrap();
        		}
        		return;
        	}
        	
        	var ariaParents = accessible.rootElement.find(".aria-parent");
        	var i = 0;
        	for (; i<hierarchy.length; i++) {
        		var h = hierarchy[i];
        		var child;
        		
        		if (ariaParents.length <= i) {
        			child = ariaId;
        			// there is no parent in previous hierarchy
        			// next child that will be wrapped is the aria-element
        		} else {
        			var p = jQuery(ariaParents[i]);
        			
        			if (isHierarchyParentSame(p, h)) {
        				// if parent is the same at this level, do not touch the element, otherwise it will be read again
        				continue;
        			}
        			
        			// if the parent changed, unwrap and wrap a new parent
        			// if we just change the attributes, it will not be read
        			child = p.children().first();
        			child.unwrap();
        		}
        		
        		child.wrap("<div class='aria-parent' id='aria-parent-" + h.id + "'></div>");
        		var wrapper = accessible.rootElement.find("#aria-parent-" + h.id);
        		wrapper.attr("role", h.role).attr("aria-label", h.text);
        		if (h.size != null) {
        			wrapper.attr("aria-setsize", h.size);
        			wrapper.attr("aria-posinset", h.position);
        		}
        	}
        	
        	if (ariaParents.length >= i) {
        		for (; i<ariaParents.length; i++) {
            		jQuery(ariaParents[i]).contents().unwrap();
            	}
        	}
        }
        
        function isHierarchyParentSame(p, h) {
        	if (p.attr("id") != ("aria-parent-" + h.id) || p.attr("role") != h.role || p.attr("aria-label") != h.text) {
        		return false;
        	}
        	
        	if (h.size != null) {
        		if (p.attr("aria-setsize") != h.size || p.attr("aria-posinset") != h.position) {
        			return false;
        		}
        	}
        	
        	return true;
        }
        
        function showWindowSwitcher(rootElement, windowSwitchList) {
        	if (windowSwitcher == null) {
        		rootElement.append(switcherHtml);
        		windowSwitcher = rootElement.find('div[data-id="windowSwitcher"]');
        	}
        	
        	var content = windowSwitcher.find(".ws-window-switcher-content");
        	content.empty();
        	
        	var firstItem;
        	
        	for (var i=0; i<windowSwitchList.length; i++) {
        		var win = windowSwitchList[i];
        		
        		var wrapper = jQuery("<div />");
        		
        		var item = jQuery("<input type='radio' name='windowSwitcher' />");
        		item.attr("id", "windowSwitcher-" + i);
        		item.data("winid", win.id);
        		
        		var modalNote;
        		
        		if (win.modalBlocked) {
        			item.attr("aria-describedby", "switcher-modal-blocked-description");
        			modalNote = jQuery("<span aria-hidden='true' />").text(api.translate("accessibility.switcher.modalBlocked"));
        		} else {
        			item.on("keyup", switcherRadioKeyHandler);
        		}
        		
        		if (i == 0) {
        			firstItem = item;
        			item.prop("checked", true);
        		}
        		
        		var label = jQuery("<label />");
        		label.attr("for", "windowSwitcher-" + i);
        		if (win.title) {
        			label.text(win.title);
        		} else {
        			label.text(api.translate("accessibility.switcher.untitled"));
        		}
        		
        		wrapper.append(item);
        		wrapper.append(label);
        		if (modalNote) {
        			wrapper.append(modalNote);
        		}
        		
        		content.append(wrapper);
        	}
        	
        	if (firstItem) {
        		// this focuses first item in the dialog
        		// do not go through focusManager, this is inside a .catch-focus element
        		firstItem[0].focus({preventScroll: true});
        	}
        	
        	var closeBtn = windowSwitcher.find('button[data-id="closeBtn"]');
            closeBtn.on('click', function (event) {
            	hideWindowSwitcher();
            });
        }
        
        function switcherRadioKeyHandler(evt) {
        	if (evt.keyCode == 13) {
        		// enter
        		var winId = windowSwitcher.find("input[type=radio]:checked").data("winid");
        		switchWindow(winId);
        		hideWindowSwitcher();
        	} else if (evt.keyCode == 88) {
        		// X
        		var winId = windowSwitcher.find("input[type=radio]:checked").data("winid");
        		closeWindow(winId);
        		hideWindowSwitcher();
        	}
        }
        
        function hideWindowSwitcher() {
            if (windowSwitcher != null) {
            	windowSwitcher.hide("fast");
            	windowSwitcher.remove();
            	windowSwitcher = null;
            }
            api.focusDefault();
        }
        
        function switchWindow(winId) {
        	api.send({
        		events: [{
        			focus: {
        				windowId: winId
        			}
        		}]
        	});
        }
        
        function closeWindow(winId) {
        	api.closeWindow(winId);
        }

    }
