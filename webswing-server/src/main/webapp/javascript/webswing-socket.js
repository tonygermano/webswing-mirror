define([ 'atmosphere', 'ProtoBuf' ], function(atmosphere,ProtoBuf) {
	"use strict";
	var api;
	var socket = null;
	var proto = ProtoBuf.loadProtoFile("/webswing.proto");
	
	function connect() {
		var request = {
			url : document.location.toString() + 'async/swing',
			contentType : "application/json",
			logLevel : 'debug',
			transport : 'websocket',
			trackMessageLength : true,
			reconnectInterval : 5000,
			fallbackTransport : 'long-polling'
		};

		if (api.typedArraysSupported) {
			request.headers = {
				'X-Atmosphere-Binary' : true
			};
			request.enableProtocol = false;
			request.trackMessageLength = false;
			request.contentType = 'application/octet-stream';
			request.webSocketBinaryType = 'arraybuffer';
		}

		request.onOpen = function(response) {
			api.ws.setUuid(response.request.uuid);
		};

		request.onReopen = function(response) {
			api.dialog.hide();
		};

		request.onMessage = function(response) {
			var message = response.responseBody;
			try {
				var data = atmosphere.util.parseJSON(message);
				api.ws.processJsonMessage(data);
			} catch (e) {
				console.error(e);
				return;
			}
		};

		request.onClose = function(response) {
			if (api.dialog.current() !== api.dialog.content.stoppedDialog) {
				api.dialog.show(api.dialog.content.disconnectedDialog);
			}
		};

		request.onError = function(response) {
			// TODO:handle
		};

		request.onReconnect = function(request, response) {
			api.dialog.show(api.dialog.content.initializingDialog);
		};

		socket = atmosphere.subscribe(request);
	}

	function dispose() {
		atmosphere.unsubscribe(socket);
		socket = null;
	}
	
	function send(message){
		if (socket != null && socket.request.isOpen) {
			if (typeof message == "string") {
				socket.push(message);
			}
			if (typeof message === "object") {
				socket.push(atmosphere.util.stringifyJSON(message));
			}
		}
	}
	
	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.socket = {
				connect : connect,
				send : send,
				dispose : dispose
			};
		}
	};
});