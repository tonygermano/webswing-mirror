define([ 'jquery', 'text!templates/dialog.html', 'bootstrap' ], function($, html) {
	"use strict";

	var api;
	var currentContent;
	var dialog, content,header;
	var configuration = {
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
					api.base.continueSession();
				},
				newsession_click : function() {
					api.newSession();
				}
			}
		}
	};

	function setup(api) {
		api.rootElement.append(html);
		dialog = api.rootElement.find('div[data-id="commonDialog"]');
		content = dialog.find('div[data-id="content"]');
		header = dialog.find('div[data-id="header"]');
	}

	function show(msg) {
		if(dialog==null){
			setup(api);
		}
		currentContent = msg;
		if(msg.header!=null){
			header.show();
			header.html(msg.header);
		}else{
			header.hide();
			header.html('');
		}
		content.html(msg.content);
		for ( var e in msg.events) {
			var element = dialog.find('*[data-id="' + e.substring(0, e.lastIndexOf('_')) + '"]');
			element.bind(e.substring(e.lastIndexOf('_') + 1), msg.events[e]);
		}
		dialog.modal('show');
	}

	function hide() {
		currentContent = null;
		content.html('');
		dialog.modal('hide');
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