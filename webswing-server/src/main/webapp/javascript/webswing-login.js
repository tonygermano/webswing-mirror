define([ 'jquery', 'text!templates/login.html' ], function($, html) {
	"use strict";
	var api;
	var user;
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
		$.ajax({
			xhrFields: { withCredentials: true },
			type : 'POST',
			url : api.connectionUrl+'login' + (api.anonym?'?mode=anonym':''),
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
			xhrFields: { withCredentials: true },
			type : 'GET',
			url : api.connectionUrl + 'logout',
			data : '',
			success : function(data) {
				user = null;
				api.dialog.show(loginDialog);
			},
			error : function(data) {
				user = null;
				api.dialog.show(loginDialog);
			}
		});
	}
	function getUser() {
		return user;
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.login = {
				user : getUser,
				login : login,
				logout : logout
			};
		}
	};
});