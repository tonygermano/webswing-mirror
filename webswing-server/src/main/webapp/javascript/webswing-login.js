define([ 'jquery' ], function amdFactory($) {
	"use strict";
	return function LoginModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			start : 'webswing.start',
			disconnect : 'webswing.disconnect',
			showDialog : 'dialog.show',
			unauthorizedAccessMessage : 'dialog.content.unauthorizedAccess'
		};
		module.provides = {
			login : login,
			logout : logout,
			user : getUser
		};

		var user;

		function login(successCallback) {
			verify('login?verify',successCallback, function() {
				if(api.cfg.oneTimePassword!=null){
					verify('login?otp='+api.cfg.oneTimePassword,successCallback, function() {
						api.showDialog(api.unauthorizedAccessMessage);
					});
				}else{
					window.location.href = api.cfg.connectionUrl + 'login?successUrl='+window.location.href;
				}
			});
		}

		function verify(url,successCallback, failCallback) {
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'GET',
				url : api.cfg.connectionUrl + url,
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
		
		
		function verifyOtp(successCallback) {
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'GET',
				url : api.cfg.connectionUrl + 'login?otp='+api.cfg.oneTimePassword,
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