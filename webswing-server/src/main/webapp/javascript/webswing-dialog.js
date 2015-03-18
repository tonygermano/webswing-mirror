define(
		[ 'jquery', 'text!templates/dialog.html', 'text!templates/dialog.css', 'text!templates/bootstrap.css' ],
		function($, html, css, cssBootstrap) {
			"use strict";

			var api;
			var currentContent;
			var dialog, content, header;
			var configuration = {
				readyDialog : {
					content : '<p>Webswing ready...</p>'
				},
				initializingDialog : {
					content : '<p>Initializing...</p>'
				},
				startingDialog : {
					content : '<p>Starting app...</p>'
				},
				connectingDialog : {
					content : '<p>Connecting...</p>'
				},
				disconnectedDialog : {
					content : '<p>Disconnected...</p>'
				},
				connectionErrorDialog : {
					content : '<p>Connection error...</p>'
				},
				tooManyClientsNotification : {
					content : '<p>Too many connections. Please try again later...</p>'
				},
				applicationAlreadyRunning : {
					content : '<p>Application is already running in other browser window...</p>'
				},
				stoppedDialog : {
					content : '<p>Application stopped... </p> <button data-id="newsession" class="btn btn-primary">Start new session.</button> <span> </span><button data-id="logout" class="btn btn-default">Logout.</button>',
					events : {
						newsession_click : function() {
							api.newSession();
						},
						logout_click : function() {
							api.login.logout();
						}
					}
				},
				continueOldSessionDialog : {
					content : '<p>Continue old session?</p><button data-id="continue" class="btn btn-primary">Yes,	continue.</button><span> </span><button data-id="newsession" class="btn btn-default" >No, start new session.</button>',
					events : {
						continue_click : function() {
							api.base.kill();
							api.base.continueSession();
						},
						newsession_click : function() {
							api.newSession();
						}
					}
				}
			};

			function setup(api) {
				var style = $("<style></style>", {
					type : "text/css"
				});
				style.text(css);
				var style0 = $("<style></style>", {
					type : "text/css"
				});
				style0.text(cssBootstrap);

				api.rootElement.append(html);
				$("head").prepend(style0);
				$("head").prepend(style);

				backdrop = api.rootElement.find('div[data-id="commonDialogBackDrop"]');
				dialog = api.rootElement.find('div[data-id="commonDialog"]');
				content = dialog.find('div[data-id="content"]');
				header = dialog.find('div[data-id="header"]');
				dialog.hide();
			}

			function show(msg) {
				if (dialog == null) {
					setup(api);
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
				if (dialog.is(":visible")) {
					content.fadeIn('fast');
				}
				backdrop.show();
				dialog.slideDown('fast');
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

			return {
				init : function(wsApi) {
					api = wsApi;
					wsApi.dialog = {
						show : show,
						hide : hide,
						current : current,
						content : configuration
					};
				}
			};
		});