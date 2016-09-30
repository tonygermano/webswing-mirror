define([ 'jquery', 'text!templates/dialog.html', 'text!templates/dialog.css', 'text!templates/bootstrap.css' ], function amdFactory($, html, css, cssBootstrap) {
	"use strict";
	var style = $("<style></style>", {
		type : "text/css"
	});
	style.text(css);
	var style0 = $("<style></style>", {
		type : "text/css"
	});
	style0.text(cssBootstrap);
	$("head").prepend(style0);
	$("head").prepend(style);

	return function DialogModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			continueSession : 'base.continueSession',
			kill : 'base.kill',
			newSession : 'webswing.newSession',
			reTrySession : 'webswing.reTrySession',
			logout : 'login.logout'
		};
		module.provides = {
			show : show,
			hide : hide,
			current : current,
			content : configuration()
		};

		var currentContent;
		var dialog, content, header, backdrop, spinnerTimer;

		function configuration() {
			return {
				emptyMessage : messageDialog('',true),
				readyDialog : messageDialog('Ready to start your session'),
				initializingDialog : messageDialog('Your session is being initialized', true),
				startingDialog : messageDialog('Starting your application', true),
				connectingDialog : messageDialog('Connecting to the server', true),
				unauthorizedAccess : messageDialog('Unable to authorize your request'),
				applicationAlreadyRunning : retryMessageDialog('There is already a session in progress in another window.'),
				sessionStolenNotification : retryMessageDialog('A new session was started in another window. This session has been closed.'),
				disconnectedDialog : retryMessageDialog('You have been disconnected from the server. Please reconnect to continue.'),
				connectionErrorDialog : retryMessageDialog('A connection error has occurred. Please reconnect to continue.'),
				tooManyClientsNotification : retryMessageDialog('There are too many active connections right now, please try again later'),
				stoppedDialog : finalMessageDialog('The application has been closed.'),
				continueOldSessionDialog : {
					content : '<p>Continue existing session?</p>',
					buttons : [ {
						label : 'Continue',
						action : function() {
							api.continueSession();
						}
					}, {
						label : 'New session',
						action : function() {
							api.kill();
							api.newSession();
						}
					} ]
				}
			};
		}

		function messageDialog(msg, withSpinner) {
			var content = '<p>' + msg + '</p>';
			if (withSpinner) {
				content = '<div class="c-spinner"><div class="c-spinner__dot-1"></div> <div class="c-spinner__dot-2"></div></div>' + content;
			}
			return {
				content : content
			};
		}

		function finalMessageDialog(msg) {
			return {
				content : '<p>' + msg + '</p>',
				buttons : [ {
					label : 'New session',
					action : function() {
						api.newSession();
					}
				}, {
					label : 'Sign out',
					action : function() {
						api.logout();
					}
				} ]
			};
		}

		function retryMessageDialog(msg) {
			return {
				content : '<p>' + msg + '</p>',
				buttons : [ {
					label : 'Reconnect',
					action : function() {
						api.reTrySession();
					}
				}, {
					label : 'Sign out',
					action : function() {
						api.logout();
					}
				} ]
			};
		}

		function setup() {
			api.cfg.rootElement.append(html);
			backdrop = api.cfg.rootElement.find('div[data-id="commonDialogBackDrop"]');
			dialog = api.cfg.rootElement.find('div[data-id="commonDialog"]');
			content = dialog.find('div[data-id="content"]');
			header = dialog.find('div[data-id="header"]');
			$(document).ajaxStart(function() {
				spinnerTimer = setTimeout(function() {
					if (dialog.is(":visible")) {
						$('#ajaxProgress').slideDown('fast');
					}
				}, 200);
			}).ajaxComplete(function() {
				clearTimeout(spinnerTimer);
				$('#ajaxProgress').hide();
			});
		}

		function show(msg) {
			if (dialog == null) {
				setup();
			}
			currentContent = msg;
			if (msg.header != null) {
				header.html(msg.header);
				if (dialog.is(":visible")) {
					header.fadeIn(200);
				} else {
					header.show();
				}
			} else {
				header.hide();
				header.html('');
			}

			content.html(msg.content);

			for ( var e in msg.events) {
				var element = dialog.find('*[data-id="' + e.substring(0, e.lastIndexOf('_')) + '"]');
				element.bind(e.substring(e.lastIndexOf('_') + 1), msg.events[e]);
			}

			for ( var b in msg.buttons) {
				var btn = msg.buttons[b];
				var button = $('<button class="c-button c-button--dialog">' + btn.label + '</button><span> </span>');
				button.on('click', btn.action);
				content.append(button);
			}

			if (dialog.is(":visible")) {
				content.fadeIn('fast');
			}
			backdrop.show();
			dialog.slideDown('fast');
			return content;
		}

		function hide() {
			currentContent = null;
			content.html('');
			dialog.fadeOut('fast');
			backdrop.fadeOut('fast');
		}

		function current() {
			return currentContent;
		}

	};
});
