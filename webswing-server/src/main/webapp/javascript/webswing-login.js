define([ 'jquery', 'text!templates/login.html', 'text!templates/login.css' ], function($, html, css) {
	"use strict";
	var api;
	var user;
	var started = false;
	var loginDialog = {
		content : html,
		events : {
			loginBtn_click : function(e) {
				api.start();
			},
			passwordInput_keyup : function(e) {
				if (e.keyCode == 13) {
					api.start();
				}
			},
			nameInput_keyup : function(e) {
				if (e.keyCode == 13) {
					api.start();
				}
			}
		}
	};

	function login(successCallback) {
		if (!started) {
			var style = $("<style></style>", {
				type : "text/css"
			});
			style.text(css);
			$("head").append(style);
			started = true;
		}
		$.ajax({
			type : 'POST',
			url : '/login?mode=swing',
			data : loginDialogVisible() ? api.rootElement.find('form[data-id="loginForm"]').serialize() : '',
			success : function(data) {
				if (loginDialogVisible()) {
					var errorMsg = api.rootElement.find('*[data-id="loginErrorMsg"]');
					errorMsg.html('');
				}
				user = data;
				successCallback();
			},
			error : function(data) {
				if (!loginDialogVisible()) {
					api.dialog.show(loginDialog);
				} else {
					var errorMsg = api.rootElement.find('*[data-id="loginErrorMsg"]');
					errorMsg.html('<div class="alert alert-danger">' + data.responseText + '</div>');
				}
			}
		});
	}

	function loginDialogVisible() {
		return api.dialog.current() === loginDialog
	}

	function logout() {
		$.ajax({
			type : 'GET',
			url : '/logout',
			data : '',
			success : function(data) {
				user = null;
				api.dialog.show(loginDialog);
			}
		});
	}
	function user() {
		return user;
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.login = {
				user : user,
				login : login,
				logout : logout
			};
		}
	};
});