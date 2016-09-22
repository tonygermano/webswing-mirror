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
		var dialog, content, header, backdrop,spinner;

		function configuration() {
			return {
				readyDialog : messageDialog('Webswing ready...'),
				initializingDialog : messageDialog('Initializing...'),
				startingDialog : messageDialog('Starting app...'),
				connectingDialog : messageDialog('Connecting...'),
				unauthorizedAccess : messageDialog('Unauthorized access...'),
				applicationAlreadyRunning : retryMessageDialog('Application is already running in other browser window...'),
				sessionStolenNotification : retryMessageDialog('Application was opened in other browser window. Session disconnected...'),
				disconnectedDialog : retryMessageDialog('Disconnected...'),
				connectionErrorDialog : retryMessageDialog('Connection error...'),
				tooManyClientsNotification : retryMessageDialog('Too many connections. Please try again later...'),
				stoppedDialog : finalMessageDialog('Application stopped...'),
				continueOldSessionDialog : {
					content : '<p>Continue existing session?</p>',
					buttons : [ {
						label : 'Yes, continue.',
						action : function() {
							api.continueSession();
						}
					}, {
						label : 'No, start new session.',
						action : function() {
							api.kill();
							api.newSession();
						}
					} ]
				}
			};
		}

		function messageDialog(msg) {
			return {
				content : '<p>' + msg + '</p>'
			};
		}

		function finalMessageDialog(msg) {
			return {
				content : '<p>' + msg + '</p>',
				buttons : [ {
					label : 'Start new session.',
					action : function() {
						api.newSession();
					}
				}, {
					label : 'Logout.',
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
					label : 'Try again.',
					action : function() {
						api.reTrySession();
					}
				}, {
					label : 'Logout.',
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
			spinner = $('<div class="spinner"><div class="dot1"></div> <div class="dot2"></div></div>');
			$(document).ajaxStart(function() {
				if (dialog.is(":visible")){
					$('#ajaxProgress').show();
					$('#ajaxProgress').append(spinner.clone(true));
				}
			}).ajaxComplete(function() {
				$('#ajaxProgress').html('');
				$('#ajaxProgress').hide();
			});
		}

		function show(msg) {
			if (dialog == null) {
				setup();
			}
			currentContent = msg;
			if (dialog.is(":visible")) {
				header.hide();
				content.hide();
			}
			if (msg.header != null) {
				header.html(msg.header);
				if (dialog.is(":visible")) {
					header.fadeIn('fast');
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
				var button = $('<button class="btn btn-primary">' + btn.label + '</button><span> </span>');
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