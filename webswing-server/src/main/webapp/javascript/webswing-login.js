define([ 'jquery' ], function amdFactory($) {
	"use strict";
	return function LoginModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			start : 'webswing.start',
			disconnect : 'webswing.disconnect',
			showDialog : 'dialog.show'
		};
		module.provides = {
			login : login,
			logout : logout,
			user : getUser
		};

		var user;

		function loginDialog(html) {
			return {
				content : html,
				events : {
					loginBtn_click : function(e) {
						api.start();
					}
				}
			};
		}
		function login(successCallback) {
			var loginForm = api.cfg.rootElement.find('form[data-id="loginForm"]');
			var loginFormData = loginForm.length !== 0 ? loginForm.serialize(): '';
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'POST',
				url : api.cfg.connectionUrl + 'login',
				data : loginFormData,
				success : function(data) {
					user = data;
					if (successCallback != null) {
						successCallback();
					}
				},
				error : function(data) {
					api.showDialog(loginDialog(data.responseText));
				}
			});
		}

		function logout() {
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'GET',
				url : api.cfg.connectionUrl + 'logout',
				data : '',
				success : done,
				error : done
			});
			function done(data) {
				api.disconnect();
				api.start();
			}
		}

		function getUser() {
			return user;
		}
	};
});