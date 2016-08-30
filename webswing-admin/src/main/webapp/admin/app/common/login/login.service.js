(function(define) {
	define([], function f() {
		function loginService(baseUrl, $rootScope, messageService, $http, $log, permissions) {

			return {
				login : login,
				logout : logout
			};

			function login() {
				return new Promise(function(resolve) {
					$rootScope.$broadcast('wsLoginRequestEvent', doLogin);
					function doLogin(element, loginData, successCallback) {
						webswingLogin(baseUrl + "/", element, loginData, success);
						function success(data) {
							if (successCallback != null) {
								successCallback();
							}
							permissions.reload();
							resolve(data);
							return data;
						}
					}
				});
			}

			function logout(simple) {
				webswingLogout(baseUrl + "/", $('<div></div>'), success);
				function success() {
					if (!simple) {
						login();
					}
				}
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
								if (typeof element === 'function') {
									element = element();
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
				}).done(function(data, status, xhr) {
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
							if (typeof element === 'function') {
								element = element();
							}
							element.html(loginMsg.partialHtml);
						} else {
							doneCallback();
						}
					}
				});
			}
		}
		loginService.$inject = [ 'baseUrl', '$rootScope', 'messageService', '$http', '$log', 'permissions' ];
		return loginService;
	});
})(adminConsole.define);