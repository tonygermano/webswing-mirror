define(
		[ 'jquery' ],
		function($) {
			"use strict";
			var api;

			function show(apps) {
				var header = '<span class="pull-right"><a href="javascript:;" data-id="logout">Logout</a></span><h4 class="modal-title" id="myModalLabel">Hello <span>'
						+ api.login.user() + '</span>. Select your application:</h4>';
				var events = {
					logout_click : function() {
						api.login.logout();
					},
					application_click : function() {
						var appName = $(this).attr('data-name');
						api.base.startApplication(appName);
					}
				};
				var content;
				if (apps == null || apps.length == 0) {
					content = '<p>Sorry, there is no application available for you.</p>';
				} else if (api.applicationName != null) {
					apps.forEach(function(app) {
						if (app.name === api.applicationName) {
							api.base.startApplication(app.name);
							return;
						}
					});
					content = '<p>Sorry, application "' + api.applicationName + '" is not available for you.</p>';

				} else {
					content = '<div class="row">';
					for ( var i in apps) {
						var app = apps[i];
						if (app.name == 'adminConsoleApplicationName') {
							content += '<div class="col-xs-4 col-sm-3 col-md-2"><div class="thumbnail" style="max-width: 155px" onclick="window.location.href = \'/admin\';"><img src="/admin/img/admin.png" class="img-thumbnail"/><div class="caption">Admin console</div></div></div>';
						} else {
							content += '<div class="col-xs-4 col-sm-3 col-md-2"><div class="thumbnail" style="max-width: 155px" data-id="application" data-name="'
									+ app.name
									+ '"><img src="'
									+ getImageString(app.base64Icon)
									+ '" class="img-thumbnail"/><div class="caption">'
									+ app.name + '</div></div></div>';
						}
					}
					content += '</div>';
				}
				api.dialog.show({
					header : header,
					content : content,
					events : events
				});
			}

			function getImageString(data) {
				if (typeof data === 'object') {
					var binary = '';
					var bytes = new Uint8Array(data.buffer, data.offset, data.limit - data.offset);
					for ( var i = 0, l = bytes.byteLength; i < l; i++) {
						binary += String.fromCharCode(bytes[i]);
					}
					data = window.btoa(binary);
				}
				return 'data:image/png;base64,' + data
			}

			function hide() {
				api.dialog.hide();
			}

			return {
				init : function(wsApi) {
					api = wsApi;
					wsApi.selector = {
						show : show,
						hide : hide
					};
				}
			};
		});