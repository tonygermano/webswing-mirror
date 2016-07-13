define([ 'jquery' ], function amdFactory($) {
	"use strict";
	return function LoginModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			start : 'webswing.start',
			disconnect : 'webswing.disconnect',
		};
		module.provides = {
			login : login,
			logout : logout,
			user : getUser
		};

		var user;

		function login(successCallback) {
			verify(successCallback, function() {
				window.location.href = api.cfg.connectionUrl + 'login';
			});
		}

		function verify(successCallback, failCallback) {
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'GET',
				url : api.cfg.connectionUrl + 'login?verify',
				success : function(data, textStatus, request) {
					user = request.getResponseHeader('webswingUsername');
					if (successCallback != null) {
						successCallback();
					}
				},
				error : function(data) {
					if (failCallback != null) {
						failCallback();
					}
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