define([ 'atmosphere', 'ProtoBuf' ], function(atmosphere, ProtoBuf) {
	"use strict";
	var api;
	var socket = null;
	var uuid = null;
	var binary;
	var proto = ProtoBuf.loadProtoFile("/webswing.proto");
	var InputEventsFrameMsgInProto = proto.build("org.webswing.server.model.proto.InputEventsFrameMsgInProto");
	var AppFrameMsgOutProto = proto.build("org.webswing.server.model.proto.AppFrameMsgOutProto");
	function connect() {
		binary = api.typedArraysSupported;
		var request = {
			url : document.location.toString() + 'async/swing',
			contentType : "application/json",
			logLevel : 'debug',
			transport : 'websocket',
			trackMessageLength : true,
			reconnectInterval : 5000,
			fallbackTransport : 'long-polling'
		};

		if (binary) {
			request.headers = {
				'X-Atmosphere-Binary' : true
			};
			request.enableProtocol = false;
			request.trackMessageLength = false;
			request.contentType = 'application/octet-stream';
			request.webSocketBinaryType = 'arraybuffer';

		}

		request.onOpen = function(response) {
			uuid = response.request.uuid + '';
		};

		request.onReopen = function(response) {
			api.dialog.hide();
		};

		request.onMessage = function(response) {
			var message = response.responseBody;
			try {
				var data
				if (binary) {
					data = AppFrameMsgOutProto.decode(message);
				} else {
					data = atmosphere.util.parseJSON(message);
				}
				api.base.processMessage(data);
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
			api.dialog.show(api.dialog.content.connectionErrorDialog);
		};

		request.onReconnect = function(request, response) {
			api.dialog.show(api.dialog.content.initializingDialog);
		};

		socket = atmosphere.subscribe(request);
	}

	function dispose() {
		atmosphere.unsubscribe(socket);
		socket = null;
		uuid = null;
		binary = null;
	}

	function send(message) {
		if (socket != null && socket.request.isOpen) {
			if (typeof message === "object") {
				if (binary) {
					var msg = new InputEventsFrameMsgInProto(message);
					socket.push(msg.encode().toArrayBuffer());
				} else {
					socket.push(atmosphere.util.stringifyJSON(message));
				}
			} else {
				console.log("message is not an object " + message);
			}
		}
	}

	function getuuid() {
		return uuid;
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.socket = {
				connect : connect,
				send : send,
				uuid : getuuid,
				dispose : dispose
			};
		}
	};
});