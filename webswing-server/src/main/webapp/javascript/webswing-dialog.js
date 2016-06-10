define(['jquery', 'text!templates/dialog.html', 'text!templates/dialog.css', 'text!templates/bootstrap.css'], function amdFactory($, html, css, cssBootstrap) {
    "use strict";
    var style = $("<style></style>", {
        type: "text/css"
    });
    style.text(css);
    var style0 = $("<style></style>", {
        type: "text/css"
    });
    style0.text(cssBootstrap);
    $("head").prepend(style0);
    $("head").prepend(style);

    return function DialogModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            continueSession: 'base.continueSession',
            kill: 'base.kill',
            newSession: 'webswing.newSession',
            reTrySession: 'webswing.reTrySession',
            logout: 'login.logout',
        };
        module.provides = {
            show: show,
            hide: hide,
            current: current,
            content: configuration()
        };

        var currentContent;
        var dialog, content, header, backdrop;

        function configuration() {
            return {
                readyDialog: {
                    content: '<p>Webswing ready...</p>'
                },
                initializingDialog: {
                    content: '<p>Initializing...</p>'
                },
                startingDialog: {
                    content: '<p>Starting app...</p>'
                },
                connectingDialog: {
                    content: '<p>Connecting...</p>'
                },
                applicationAlreadyRunning: retryMessageDialog('Application is already running in other browser window...'),
                sessionStolenNotification: retryMessageDialog('Application was opened in other browser window. Session disconnected...'),
                disconnectedDialog: finalMessageDialog('Disconnected...'),
                connectionErrorDialog: finalMessageDialog('Connection error...'),
                tooManyClientsNotification: finalMessageDialog('Too many connections. Please try again later...'),
                stoppedDialog: finalMessageDialog('Application stopped...'),
                continueOldSessionDialog: {
                    content: '<p>Continue existing session?</p><button data-id="continue" class="btn btn-primary">Yes,	continue.</button><span> </span><button data-id="newsession" class="btn btn-default" >No, start new session.</button>',
                    events: {
                        continue_click: function () {
                            api.continueSession();
                        },
                        newsession_click: function () {
                            api.kill();
                            api.newSession();
                        }
                    }
                }
            };
        }

        function finalMessageDialog(msg) {
            return {
                content: '<p>'
                        + msg
                        + '</p><button data-id="newsession" class="btn btn-primary">Start new session.</button> <span> </span><button data-id="logout" class="btn btn-default">Logout.</button>',
                events: {
                    newsession_click: function () {
                        api.newSession();
                    },
                    logout_click: function () {
                        api.logout();
                    }
                }
            };
        }
        
        function retryMessageDialog(msg) {
            return {
                content: '<p>'
                        + msg
                        + '</p><button data-id="retrysession" class="btn btn-primary">Try again.</button> <span> </span><button data-id="logout" class="btn btn-default">Logout.</button>',
                events: {
                    retrysession_click: function () {
                        api.reTrySession();
                    },
                    logout_click: function () {
                        api.logout();
                    }
                }
            };
        }

        function setup() {
            api.cfg.rootElement.append(html);
            backdrop = api.cfg.rootElement.find('div[data-id="commonDialogBackDrop"]');
            dialog = api.cfg.rootElement.find('div[data-id="commonDialog"]');
            content = dialog.find('div[data-id="content"]');
            header = dialog.find('div[data-id="header"]');
        }

        function show(msg) {
            if (dialog == null) {
                setup();
            }
            currentContent = msg;
            if (dialog.is(":visible")) {
                header.hide();
                content.hide();
            }
            if (msg.header != null) {
                header.html(msg.header);
                if (dialog.is(":visible")) {
                    header.fadeIn('fast');
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
            if (dialog.is(":visible")) {
                content.fadeIn('fast');
            }
            backdrop.show();
            dialog.slideDown('fast');
        }

        function hide() {
            currentContent = null;
            content.html('');
            dialog.fadeOut('fast');
            backdrop.fadeOut('fast');
        }

        function current() {
            return currentContent;
        }
    };
});