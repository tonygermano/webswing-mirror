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
			var loginData = {
				securityToken : api.cfg.securityToken,
				successUrl : window.top.location.href
			};
			var dialogContent = function(){
				return api.showDialog(api.unauthorizedAccessMessage);
			}
			webswingLogin(api.cfg.connectionUrl, dialogContent, loginData, function(data, request) {
				user = request.getResponseHeader('webswingUsername');
				if (successCallback != null) {
					successCallback();
				}
			});
		}

		function logout() {
			var dialogContent = api.showDialog(api.unauthorizedAccessMessage);
			webswingLogout(api.cfg.connectionUrl, dialogContent, function done(data) {
				api.disconnect();
				api.start();
			});
		}

		function webswingLogin(baseUrl, element, loginData, successCallback) {
			$.ajax({
				xhrFields : {
					withCredentials : true
				},
				type : 'POST',
				url : baseUrl + 'login',
				contentType : typeof loginData === 'object' ? 'application/json' : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : typeof loginData === 'object' ? JSON.stringify(loginData) : loginData,
				success : function(data, textStatus, request) {
					if (successCallback != null) {
						successCallback(data, request);
					}
				},
				error : function(xhr) {
					var response = xhr.responseText;
					if (response != null) {
						var loginMsg = {};
						try {
							loginMsg = JSON.parse(response);
						} catch (error) {
							loginMsg.partialHtml = "<p>Login Failed.</p>";
						}
						if (loginMsg.redirectUrl != null) {
							window.top.location.href = loginMsg.redirectUrl;
						} else if (loginMsg.partialHtml != null) {
							if(typeof element === 'function'){
								element=element();
							}
							element.html(loginMsg.partialHtml);
							var form = element.find('form').first();
							form.submit(function(event) {
								webswingLogin(baseUrl, element, form.serialize(), successCallback);
								event.preventDefault();
							});
						} else {
							loginMsg.partialHtml = "<p>Oops, something's not right.</p>";
						}
					}
				}
			});
		}

		function webswingLogout(baseUrl, element, doneCallback) {
			$.ajax({
				type : 'GET',
				url : baseUrl + 'logout',
			}).done(function(data,status, xhr) {
				var response = xhr.responseText;
				if (response != null) {
					var loginMsg = {};
					try {
						loginMsg = JSON.parse(response);
					} catch (error) {
						doneCallback();
					}
					if (loginMsg.redirectUrl != null) {
						window.top.location.href = loginMsg.redirectUrl;
					} else if (loginMsg.partialHtml != null) {
						element.html(loginMsg.partialHtml);
					} else {
						doneCallback();
					}
				}
			});
		}

		function getUser() {
			return user;
		}
	};
});