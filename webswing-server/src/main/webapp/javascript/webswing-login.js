define([ 'jquery', 'text!templates/login.html', 'bootstrap' ], function($, html) {
	"use strict";
	var api;
	var loginDialog = {
		content : html,
		events : {
			loginBtn_click : function(e) {
				login();
			},
			passwordInput_keyup : function(e) {
				if (e.keyCode == 13) {
					login();
				}
			},
			nameInput_keyup : function(e) {
				if (e.keyCode == 13) {
					login();
				}
			}
		}
	};

	function login() {
		$.ajax({
			type : 'POST',
			url : '/login?mode=swing',
			data : loginDialogVisible() ? api.dialog.dialog.find('form[data-id="loginForm"]').serialize() : '',
			success : function(data) {
				if (loginDialogVisible()) {
					var errorMsg = api.dialog.dialog.find('*[data-id="loginErrorMsg"]');
					errorMsg.html('');
				}
				api.start();
			},
			error : function(data) {
				if (!loginDialogVisible()) {
					api.dialog.show(loginDialog);
				} else {
					var errorMsg = api.dialog.dialog.find('*[data-id="loginErrorMsg"]');
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
				api.dialog.show(loginDialog);
			}
		});
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.login = {
				login : login,
				logout : logout
			};
		}
	};
});