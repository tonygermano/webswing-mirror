define(['jquery', 'text!templates/dialog.html'], function amdFactory($, html, cssBootstrap) {
    "use strict";

    return function DialogModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            continueSession: 'base.continueSession',
            kill: 'base.kill',
            newSession: 'webswing.newSession',
            reTrySession: 'webswing.reTrySession',
            logout: 'login.logout'
        };
        module.provides = {
            show: show,
            hide: hide,
            current: current,
            content: configuration(),
            currentBar: currentBar,
            showBar: showBar,
            hideBar: hideBar
        };

        var currentContent;
        var dialog, content, header, backdrop, spinnerTimer;

        var currentContentBar;
        var bar, barContent, barHeader;

        function configuration() {
            return {
                emptyMessage: messageDialog('', true),
                logingOut: messageDialog('Signing out'),
                readyDialog: messageDialog('Ready to start your session'),
                initializingDialog: messageDialog('Your session is being initialized', true),
                startingDialog: messageDialog('Starting your application', true),
                connectingDialog: messageDialog('Connecting to the server', true),
                unauthorizedAccess: messageDialog('Unable to authorize your request'),
                applicationAlreadyRunning: retryMessageDialog('There is already a session in progress in another window.'),
                sessionStolenNotification: retryMessageDialog('A new session was started in another window. This session has been closed.'),
                disconnectedDialog: retryMessageDialog('You have been disconnected from the server. Please reconnect to continue.'),
                connectionErrorDialog: retryMessageDialog('A connection error has occurred. Please reconnect to continue.'),
                tooManyClientsNotification: retryMessageDialog('There are too many active connections right now, please try again later'),
                stoppedDialog: finalMessageDialog('The application has been closed.'),
                continueOldSessionDialog: {
                    content: '<p>Welcome back! <br>You can continue working in your previous session.</p>',
                    buttons: [{
                        label: '<span class="ws-icon-certificate"></class> Restart session',
                        action: function () {
                            api.kill();
                            api.newSession();
                        }
                    },{
                        label: '<span class="ws-icon-cancel-circled"></span>  Dismiss',
                        action: function () {
                            hideBar();
                        }
                    }]
                },
                longPollingWarningDialog: {
                    header: '<span class="ws-message--warning"><span class="ws-icon-warn"></span> Warning</span>',
                    content: '<p>Your current network blocks WebSockets. You may experience performance problems while working on this connection. Please contact your network administrator. </p>',
                    buttons: [{
                        label: '<span class="ws-icon-cancel-circled"></span>  Dismiss',
                        action: function () {
                            hideBar();
                        }
                    }]
                }
            };
        }

        function messageDialog(msg, withSpinner) {
            var content = '<p>' + msg + '</p>';
            if (withSpinner) {
                content = '<div class="ws-spinner"><div class="ws-spinner-dot-1"></div> <div class="ws-spinner-dot-2"></div></div>' + content;
            }
            return {
                content: content
            };
        }

        function finalMessageDialog(msg) {
            return {
                content: '<p>' + msg + '</p>',
                buttons: [{
                    label: '<span class="ws-icon-certificate"></class> New session',
                    action: function () {
                        api.newSession();
                    }
                }, {
                    label: '<span class="ws-icon-logout"></class> Sign out',
                    action: function () {
                        api.logout();
                    }
                }]
            };
        }

        function retryMessageDialog(msg) {
            return {
                content: '<p>' + msg + '</p>',
                buttons: [{
                    label: '<span class="ws-icon-arrows-cw"></class> Reconnect',
                    action: function () {
                        api.reTrySession();
                    }
                }, {
                    label: '<span class="ws-icon-logout"></class> Sign out',
                    action: function () {
                        api.logout();
                    }
                }]
            };
        }

        function setup() {
            api.cfg.rootElement.append(html);
            backdrop = api.cfg.rootElement.find('div[data-id="commonDialogBackDrop"]');
            dialog = api.cfg.rootElement.find('div[data-id="commonDialog"]');
            content = dialog.find('div[data-id="content"]');
            header = dialog.find('div[data-id="header"]');

            bar = api.cfg.rootElement.find('div[data-id="commonBar"]');
            barContent = bar.find('div[data-id="content"]');
            barHeader = bar.find('div[data-id="header"]');
            bar.hide();

            $(document).ajaxStart(function () {
                spinnerTimer = setTimeout(function () {
                    if (dialog.is(":visible")) {
                        $('#ajaxProgress').slideDown('fast');
                    }
                }, 200);
            }).ajaxComplete(function () {
                clearTimeout(spinnerTimer);
                $('#ajaxProgress').hide();
            });
        }

        function show(msg) {
            if(msg==null){
                return;
            }
            if (dialog == null) {
                setup();
            }
            currentContent = msg;
            generateContent(msg, dialog, header, content);
            backdrop.show();
            dialog.slideDown('fast');
            return content;
        }

        function showBar(msg) {
            if(msg==null){
                return;
            }
            if (bar == null) {
                setup();
            }
            currentContentBar = msg;
            generateContent(msg, bar, barHeader, barContent);
            bar.slideDown('fast');
            return barContent;
        }

        function generateContent(msg, dialog, header, content) {
            if (msg.header != null) {
                header.html(msg.header);
                if (dialog.is(":visible")) {
                    header.fadeIn(200);
                } else {
                    header.show();
                }
            } else {
                header.hide();
                header.html('');
            }

            content.html(msg.content);

            for (var e in msg.events) {
                var element = dialog.find('*[data-id="' + e.substring(0, e.lastIndexOf('_')) + '"]');
                element.bind(e.substring(e.lastIndexOf('_') + 1), msg.events[e]);
            }

            for (var b in msg.buttons) {
                var btn = msg.buttons[b];
                var button = $('<button class="ws-btn">' + btn.label + '</button><span> </span>');
                button.on('click', btn.action);
                content.append(button);
            }

            if (dialog.is(":visible")) {
                content.fadeIn('fast');
            }
        }


        function hide() {
            currentContent = null;
            content.html('');
            dialog.fadeOut('fast');
            backdrop.fadeOut('fast');
        }

        function hideBar() {
            currentContentBar = null;
            barContent.html('');
            bar.fadeOut('fast');
        }

        function current() {
            return currentContent;
        }

        function currentBar() {
            return currentContentBar;
        }

    };
});
