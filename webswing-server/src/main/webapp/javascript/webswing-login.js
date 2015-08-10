define(['jquery', 'text!templates/login.html'], function amdFactory($, html) {
    "use strict";
    return function LoginModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            start: 'webswing.start',
            loginDialog: loginDialog(),
            currentDialog: 'dialog.current',
            showDialog: 'dialog.show'
        };
        module.provides = {
            login: login,
            logout: logout,
            user: getUser
        };

        var user;

        function loginDialog() {
            return {
                content: html,
                events: {
                    loginBtn_click: function (e) {
                        api.start();
                    },
                    passwordInput_keyup: function (e) {
                        if (e.keyCode == 13) {
                            api.start();
                        }
                    },
                    nameInput_keyup: function (e) {
                        if (e.keyCode == 13) {
                            api.start();
                        }
                    }
                }
            };
        }
        function login(successCallback) {
            $.ajax({
                xhrFields: {
                    withCredentials: true
                },
                type: 'POST',
                url: api.cfg.connectionUrl + 'login' + (api.cfg.anonym ? '?mode=anonym' : ''),
                data: loginDialogVisible() ? api.cfg.rootElement.find('form[data-id="loginForm"]').serialize() : '',
                success: function (data) {
                    if (loginDialogVisible()) {
                        var errorMsg = api.cfg.rootElement.find('*[data-id="loginErrorMsg"]');
                        errorMsg.html('');
                    }
                    user = data;
                    successCallback();
                },
                error: function (data) {
                    if (!loginDialogVisible()) {
                        api.showDialog(api.loginDialog);
                    } else {
                        var errorMsg = api.cfg.rootElement.find('*[data-id="loginErrorMsg"]');
                        errorMsg.html('<div class="alert alert-danger">' + data.responseText + '</div>');
                    }
                }
            });
        }

        function loginDialogVisible() {
            return api.currentDialog() === api.loginDialog;
        }

        function logout() {
            $.ajax({
                xhrFields: {
                    withCredentials: true
                },
                type: 'GET',
                url: api.cfg.connectionUrl + 'logout',
                data: '',
                success: done,
                error: done
            });
            function done(data) {
                user = null;
                api.showDialog(api.loginDialog);
            }
        }

        function getUser() {
            return user;
        }
    };
});