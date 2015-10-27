define(['jquery','webswing-util'], function amdFactory($, util) {
    "use strict";
    return function SelectorModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            getUser: 'login.user',
            logout: 'login.logout',
            startApplication: 'base.startApplication',
            startMirrorView: 'base.startMirrorView',
            showDialog: 'dialog.show',
            hideDialog: 'dialog.hide'
        };
        module.provides = {
            show: show,
            hide: hide
        };

        function show(apps) {
            var header = '';
            if (!api.cfg.anonym) {
                header = '<span class="pull-right"><a href="javascript:;" data-id="logout">Logout</a></span>';
                header = header + '<h4 class="modal-title" id="myModalLabel">Hello <span>' + api.getUser() + '</span>. ';
            }
            header = header + 'Select your application:</h4>';
            var events = {
                logout_click: function () {
                    api.logout();
                },
                application_click: function () {
                    var appName = $(this).attr('data-name');
                    var applet = $(this).attr('data-applet');
                    var restart = $(this).attr('data-always-restart');
                    api.startApplication(appName, 'true' === applet, 'true' === restart);
                }
            };
            var content;
            if (apps == null || apps.length === 0) {
                header = null;
                content = '<p>Sorry, there is no application available for you.</p>';
            } else if (api.cfg.applicationName != null) {
                var exists = false;
                var isApplet = false;
                var alwaysRestart = false;
                apps.forEach(function (app) {
                    if (app.name === api.cfg.applicationName) {
                        exists = true;
                        isApplet = app.applet;
                        alwaysRestart = app.alwaysRestart;
                    }
                });
                if (exists) {
                    if (!api.cfg.mirror) {
                        api.startApplication(api.cfg.applicationName, isApplet, alwaysRestart);
                    } else {
                        api.startMirrorView(api.cfg.clientId, api.cfg.applicationName);
                    }
                    return;
                }
                header = null;
                content = '<p>Sorry, application "' + api.cfg.applicationName + '" is not available for you.</p>';
            } else {
                content = '<div class="row">';
                for (var i in apps) {
                    var app = apps[i];
                    if (app.name === 'adminConsoleApplicationName') {
                        content += '<div class="col-xs-4 col-sm-3 col-md-2"><div class="thumbnail" style="max-width: 155px" onclick="window.location.href = \''
                                + api.cfg.connectionUrl
                                + 'admin\';"><img src="'
                                + api.cfg.connectionUrl
                                + 'admin/img/admin.png" class="img-thumbnail"/><div class="caption">Admin console</div></div></div>';
                    } else {
                        content += '<div class="col-xs-4 col-sm-3 col-md-2"><div class="thumbnail" style="max-width: 155px" data-id="application" data-name="'
                                + app.name
                                + '" data-applet="'
                                + app.applet
                                + '" data-always-restart="'
                                + app.alwaysRestart
                                + '"><img src="'
                                + util.getImageString(app.base64Icon)
                                + '" class="img-thumbnail"/><div class="caption">' + app.name + '</div></div></div>';
                    }
                }
                content += '</div>';
            }
            api.showDialog({
                header: header,
                content: content,
                events: events
            });
        }

        function hide() {
            api.hideDialog();
        }
    };
});