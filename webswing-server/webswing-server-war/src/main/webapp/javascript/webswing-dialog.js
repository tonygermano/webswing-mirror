import html from "./templates/dialog.html"
import networkHtml  from './templates/network.html'
"use strict";

    export default function Dialog() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            continueSession: 'base.continueSession',
            kill: 'base.kill',
            newSession: 'webswing.newSession',
            reTrySession: 'webswing.reTrySession',
            logout: 'login.logout',
            translate: 'translate.translate',
            mutePingWarning: 'ping.mutePingWarning',
            switchMode: 'touch.switchMode',
            getFocusedWindow: 'base.getFocusedWindow',
            getMainWindowVisibilityState: 'base.getMainWindowVisibilityState',
            focusDefault: 'focusManager.focusDefault'
        };
        module.provides = {
            show: show,
            hide: hide,
            current: current,
            content: configuration(),
            currentBar: currentBar,
            showBar: showBar,
            hideBar: hideBar,
            showNetworkBar: showNetworkBar,
            hideNetworkBar: hideNetworkBar,
            showOverlay: showOverlay,
            hideOverlay: hideOverlay
        };

        var dialogMap = {}; // <window>: {dialog, content, header, currentContent}
        var barMap = {}; // <window>: {bar, barContent, barHeader, currentContentBar}

        var networkBar;

        function configuration() {
            return {
                emptyMessage: messageDialog('dialog.emptyMessage', true),
                logingOut: messageDialog('dialog.logingOut'),
                readyDialog: messageDialog('dialog.readyDialog'),
                initializingDialog: messageDialog('dialog.initializingDialog', true),
                startingDialog: messageDialog('dialog.startingDialog', true),
                connectingDialog: messageDialog('dialog.connectingDialog', true),
                applicationBusyDialog: messageDialog('dialog.applicationBusyDialog', true),
                unauthorizedAccess: retryMessageDialog('dialog.unauthorizedAccess'),
                applicationAlreadyRunning: retryMessageDialog('dialog.applicationAlreadyRunning'),
                sessionStolenNotification: retryMessageDialog('dialog.sessionStolenNotification'),
                disconnectedDialog: retryMessageDialog('dialog.disconnectedDialog'),
                connectionErrorDialog: retryMessageDialog('dialog.connectionErrorDialog'),
                tooManyClientsNotification: retryMessageDialog('dialog.tooManyClientsNotification'),
                stoppedDialog: finalMessageDialog('dialog.stoppedDialog'),
                timedoutDialog: finalMessageDialog('dialog.timedoutDialog'),
                cookiesDisabledDialog: messageDialog('dialog.cookiesDisabledDialog'),
                continueOldSessionDialog: {
                    header: '<span class="ws-message--neutral">${dialog.continueH} <span data-id="dialogHide" class="ws-icon-cancel-circled ws-right"></span></span>',
                    content: '<p data-id="continueMsg">${dialog.continueC1}<a href="javascript:void(0)" data-id="restartLink">${dialog.continueC2}</a></p>',
                    events: {
                        'restartLink_click':  function () {
                            api.kill();
                            api.newSession();
                        },
                        'dialogHide_click':hideBar,
                        'continueMsg_mouseenter': function(){
                        	var winBar = getBar();
                        	if (winBar && winBar.currentContentBar) {
                        		winBar.currentContentBar.focused = true;
                        	}
                        },
                        'continueMsg_mouseleave': function(){
                        	var winBar = getBar();
                        	if (winBar && winBar.currentContentBar) {
                        		winBar.currentContentBar.focused = false;
                        	}
                        }
                    }
                },
                longPollingWarningDialog: {
                    header: '<span class="ws-message--warning"><span class="ws-icon-warn"></span> ${dialog.longPollingH}</span>',
                    content: '<p>${dialog.longPollingC} </p>',
                    buttons: [{
                        label: '<span class="ws-icon-cancel-circled"></span>  ${dialog.longPollingB}',
                        action: function () {
                            hideBar();
                        }
                    }]
                },
                inactivityTimeoutWarningDialog: {
                    header: '<span class="ws-message--warning"><span class="ws-icon-warn"></span> ${dialog.inactivityTimeoutH}</span>',
                    content: '<p>${dialog.inactivityTimeoutC}  </p>',
                    buttons: [{
                        label: '<span class="ws-icon-cancel-circled"></span>  ${dialog.inactivityTimeoutB}',
                        action: function () {
                            hideBar();
                        }
                    }]
                },
                networkOfflineWarningDialog: {
                    content: '<span class="ws-message--error"><span class="ws-icon-warn"></span>${dialog.networkMonitor.offline}</span>',
                    severity: 2
                },
                networkSlowWarningDialog: {
                    content: '<span class="ws-message--warning"><span class="ws-icon-warn"></span>${dialog.networkMonitor.warn}</span>',
                    severity: 1
                },
                touchSwitchModeMouseDialog: {
                	content: '<p><span class="ws-message--neutral">${dialog.touchSwitchMode.mouse}</span> <span class="ws-right"><a href="javascript:void(0)" data-id="barHide">${dialog.touchSwitchMode.cancelButton}</a></span></p>',
                	buttons: [{
                		label: '${dialog.touchSwitchMode.switchButton}',
                		action: function() {
                			hideBar();
                			api.switchMode(true, false);
                		}
                	}],
                	events: {
                        'barHide_click': function() {
                			hideBar();
                			api.switchMode(false, true);
                		}
                    }
                },
                touchSwitchModeTouchDialog: {
                	content: '<p><span class="ws-message--neutral">${dialog.touchSwitchMode.touch}</span> <span class="ws-right"><a href="javascript:void(0)" data-id="barHide">${dialog.touchSwitchMode.cancelButton}</a></span></p>',
                	buttons: [{
                		label: '${dialog.touchSwitchMode.switchButton}',
                		action: function() {
                			hideBar();
                			api.switchMode(true, false);
                		}
                	}],
                	events: {
                        'barHide_click': function() {
                			hideBar();
                			api.switchMode(false, true);
                		}
                    }
                },
                dockingVisibilityOverlay: {
                	type: 'visibility-overlay',
                	content: '<div class="visibility-overlay"><div class="visibility-message">${dialog.overlay.docking.visibility}</div></div>'
                },
                dockingModalityOverlay: {
                	type: 'modality-overlay',
                	content: '<div class="modality-overlay"><div class="modality-message">${dialog.overlay.docking.modality}<div class="modality-button"><button>${dialog.overlay.docking.focusWindow}</button></div></div></div>'
                }
            };
        }

        function messageDialog(msg, withSpinner) {
            var content = '<p id="commonDialog-description">${' + msg + '}</p>';
            if (withSpinner) {
                content = '<div class="ws-spinner"><div class="ws-spinner-dot-1"></div> <div class="ws-spinner-dot-2"></div></div>' + content;
            }
            return {
                content: content
            };
        }

        function finalMessageDialog(msg) {
            return {
                content: '<p id="commonDialog-description">${' + msg + '}</p>',
                buttons: [{
                    label: '<span class="ws-icon-certificate"></class> ${dialog.finalB1}',
                    action: function () {
                        api.newSession();
                    }
                }, {
                    label: '<span class="ws-icon-logout"></class> ${dialog.finalB2}',
                    action: function () {
                        api.logout();
                    }
                }]
            };
        }

        function retryMessageDialog(msg) {
            return {
                content: '<p id="commonDialog-description">${' + msg + '}</p>',
                buttons: [{
                    label: '<span class="ws-icon-arrows-cw"></class> ${dialog.retryB1}',
                    action: function () {
                        api.reTrySession();
                    }
                }, {
                    label: '<span class="ws-icon-logout"></class> ${dialog.retryB2}',
                    action: function () {
                        api.logout();
                    }
                }]
            };
        }

        function setup() {
        	// try cleanup
        	for (var winName in dialogMap) {
        		if (!dialogMap[winName].win || dialogMap[winName].win.closed) {
        			delete dialogMap[winName];
        		}
        	}
        	for (var winName in barMap) {
        		if (!barMap[winName].win || barMap[winName].win.closed) {
        			delete barMap[winName];
        		}
        	}
        	
        	var win = api.getFocusedWindow();
        	
        	if (dialogMap[win.name]) {
        		return;
        	}
        	
        	dialogMap[win.name] = {win: win};
        	barMap[win.name] = {win: win};
        	
        	var winDlg = dialogMap[win.name];
        	var winBar = barMap[win.name];
        	
        	var rootElement = $(win.document).find(".webswing-element-content");
        	
            rootElement.append(html);
            winDlg.dialog = rootElement.find('div[data-id="commonDialog"]');
            winDlg.content = winDlg.dialog.find('div[data-id="content"]');
            winDlg.header = winDlg.dialog.find('div[data-id="header"]');

            winBar.bar = rootElement.find('div[data-id="commonBar"]');
            winBar.barContent = winBar.bar.find('div[data-id="content"]');
            winBar.barHeader = winBar.bar.find('div[data-id="header"]');
            winBar.bar.hide();

            var spinnerTimer;
            
            $(document).ajaxStart(function () {
                spinnerTimer = setTimeout(function () {
                    if (winDlg.dialog.is(":visible")) {
                        rootElement.find('#ajaxProgress').slideDown('fast');
                    }
                }, 200);
            }).ajaxComplete(function () {
            	if (spinnerTimer) {
            		clearTimeout(spinnerTimer);
            	}
                rootElement.find('#ajaxProgress').hide();
            });
        }

        function show(msg) {
            if (msg == null) {
                return;
            }
            
            var winDlg = getDialog();
            
            winDlg.currentContent = msg;
            generateContent(msg, winDlg.dialog, winDlg.header, winDlg.content);
            winDlg.dialog.slideDown('fast');
            
            var initFocus = winDlg.dialog.find(".init-focus");
            if (initFocus.length) {
            	initFocus[0].focus();
            }
            
            return winDlg.content;
        }

        function showBar(msg) {
            if (msg == null) {
                return;
            }
            
            var winBar = getBar();
            
            winBar.currentContentBar = msg;
            generateContent(msg, winBar.bar, winBar.barHeader, winBar.barContent);
            winBar.bar.slideDown('fast');
            return winBar.barContent;
        }

        function generateContent(msg, dialog, header, content) {
            if (msg.header != null) {
                header.html(api.translate(msg.header));
                if (dialog.is(":visible")) {
                    header.fadeIn(200);
                } else {
                    header.show();
                }
            } else {
                header.hide();
                header.html('');
            }

            content.html(api.translate(msg.content));

            for (var e in msg.events) {
                var element = dialog.find('*[data-id="' + e.substring(0, e.lastIndexOf('_')) + '"]');
                element.bind(e.substring(e.lastIndexOf('_') + 1), msg.events[e]);
            }

            if (msg.buttons) {
            	for (var i=0; i<msg.buttons.length; i++) {
            		var btn = msg.buttons[i];
            		var button = $('<button class="ws-btn">' + api.translate(btn.label) + '</button><span> </span>');
            		if (i == 0) {
            			button.addClass("init-focus");
            		}
            		button.on('click', btn.action);
            		content.append(button);
            	}
            }

            if (dialog.is(":visible")) {
                content.fadeIn('fast');
            }
        }

        function hide() {
        	var winDlg = getDialog();
        	
        	if (!winDlg) {
        		return;
        	}
        	
        	winDlg.currentContent = null;
            winDlg.content.html('');
            winDlg.dialog.fadeOut('fast', function() {
            	api.focusDefault();
            });
        }

        function hideBar() {
        	var winBar = getBar();
        	
        	if (!winBar) {
        		return;
        	}
        	
        	winBar.currentContentBar = null;
        	winBar.barContent.html('');
        	winBar.bar.fadeOut('fast');
        }

        function current() {
        	var winDlg = getDialog();
        	
        	if (!winDlg) {
        		return null;
        	}
        	
            return winDlg.currentContent;
        }

        function currentBar() {
        	var winBar = getBar();
        	
        	if (!winBar) {
        		return null;
        	}
        	
            return winBar.currentContentBar;
        }
        
        function getDialog() {
        	var win = api.getFocusedWindow();
        	
        	if (!dialogMap[win.name]) {
        		setup();
        	}
        	
        	return dialogMap[win.name];
        }
        
        function getBar() {
        	var win = api.getFocusedWindow();
        	
        	if (!barMap[win.name]) {
        		setup();
        	}
        	
        	return barMap[win.name];
        }

        function showNetworkBar(msg) {
            var doc = api.getFocusedWindow().document;
            var rootElement = $(doc).find(".webswing-element-content");
            
            if (networkBar != null && !$.contains(doc, networkBar[0])) {
           		hideNetworkBar();
            }
            	
            if (networkBar == null) {
                rootElement.append(api.translate(networkHtml));
                networkBar = rootElement.find('div[data-id="networkBar"]');
                networkBar.find('a[data-id="hide"]').on('click', function (evt) {
                    api.mutePingWarning(msg.severity);
                    hideNetworkBar();
                });
            }
            
            var translatedMsg = api.translate(msg.content);
            var msgElement = networkBar.find('span[data-id="message"]');
            if (msgElement.html() != translatedMsg) {
            	// if we replace the same message again and again, it will trigger screen reader to read it again
            	msgElement.html(translatedMsg);
            }
           	networkBar.show("fast");
        }

        function hideNetworkBar() {
            if (networkBar != null) {
                networkBar.hide("fast");
                networkBar.remove();
                networkBar = null;
            }
        }
        
        function showOverlay(win, msg) {
        	var overlay = $(win.document).find(".webswing-element-content ." + msg.type);
        	
        	if (overlay.length == 0) {
        		overlay = $(api.translate(msg.content));
        		$(win.document).find('.webswing-element-content').append(overlay);
        	}
        	
        	overlay.addClass("active");
        	
        	if (msg.type == 'visibility-overlay') {
        		$(win.document).find(".webswing-element-content").addClass("webswing-disabled");
        		$(win.document).find(".modality-overlay").addClass("suppressed");
        	} else if (msg.type == 'modality-overlay') {
        		overlay.toggleClass("suppressed", api.getMainWindowVisibilityState() == 'hidden');
				overlay.find("button").off("click");
        	}
        }
        
        function hideOverlay(win, msg) {
        	var overlay = $(win.document).find(".webswing-element-content ." + msg.type);
        	
        	if (overlay.length == 0) {
        		return;
        	}
        	
        	overlay.removeClass("active");
        	
        	if (msg.type == 'visibility-overlay') {
        		$(win.document).find(".webswing-element-content").removeClass("webswing-disabled");
        		$(win.document).find(".modality-overlay").removeClass("suppressed");
        	} else if (msg.type == 'modality-overlay') {
        		overlay.removeClass("suppressed", api.getMainWindowVisibilityState() == 'hidden');
        		overlay.find("button").off("click");
        	}
        }
        
    }
