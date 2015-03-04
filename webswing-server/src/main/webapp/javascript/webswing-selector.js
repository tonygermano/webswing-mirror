define( [ 'jquery', 'bootstrap' ], function($) {
	"use strict";
	var api;

	function show(apps) {
		var header = '<span class="pull-right"><a href="javascript:;" data-id="logout">Logout</a></span><h4 class="modal-title" id="myModalLabel">Hello <span>' + api.ws.getUser() + '</span>. Select your application:</h4>';
		var events = {
			logout_click : function() {
				api.login.logout();
			},
			application_click: function(){
				var appName=$(this).attr('data-name');
				hide();
				api.startApplication(appName);
			}
		};
		var content;
		if (apps == null || apps.length == 0) {
			content = '<p>Sorry, there is no application available for you.</p>';
		} else if (apps.length == 1 && api.autoStart) {
			api.startApplication(apps[0].name);
			return;
		} else {
			content='<div class="row">';
			for ( var i in apps) {
				var app = apps[i];
				if (app.name == 'adminConsoleApplicationName') {
					content+='<div class="col-sm-6 col-md-4"><div class="thumbnail" onclick="window.location.href = \'/admin\';"><img src="/admin/img/admin.png" class="img-thumbnail"/><div class="caption">Admin console</div></div></div>';
				} else {
					content+='<div class="col-sm-6 col-md-4"><div class="thumbnail" data-id="application" data-name="' + app.name + '"><img src="data:image/png;base64,' + app.base64Icon + '" class="img-thumbnail"/><div class="caption">' + app.name + '</div></div></div>';
				}
			}
			content+='</div>';
		}
		api.dialog.show({
			header: header,
			content:content,
			events:events
		});
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