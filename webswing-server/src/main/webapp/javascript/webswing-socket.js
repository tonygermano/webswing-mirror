define([ 'atmosphere', 'ProtoBuf', 'text!webswing.proto' ], function WebswingSocket(atmosphere, ProtoBuf, wsProto) {
	"use strict";
	var api;
	var socket = null;
	var uuid = null;
	var responseHandlers = {};
	var binary;
	var proto = ProtoBuf.loadProto(wsProto, "webswing.proto");
	var InputEventsFrameMsgInProto = proto.build("org.webswing.server.model.proto.InputEventsFrameMsgInProto");
	var AppFrameMsgOutProto = proto.build("org.webswing.server.model.proto.AppFrameMsgOutProto");
	function connect() {
		binary = api.typedArraysSupported && api.binarySocket;
		var request = {
			url : api.connectionUrl + 'async/swing',
			contentType : "application/json",
			// logLevel : 'debug',
			transport : 'websocket',
			trackMessageLength : true,
			reconnectInterval : 5000,
			fallbackTransport : 'long-polling',
			enableXDR : true,
			headers : {}
		};

		if (binary) {
			request.url = request.url + '-bin';
			request.headers['X-Atmosphere-Binary'] = true;
			request.enableProtocol = false;
			request.trackMessageLength = false;
			request.contentType = 'application/octet-stream';
			request.webSocketBinaryType = 'arraybuffer';
		}

		if (api.args != null) {
			request.headers['X-webswing-args'] = api.args;
		}
		if (api.recording != null) {
			request.headers['X-webswing-recording'] = api.recording;
		}
		if (api.debugPort != null) {
			request.headers['X-webswing-debugPort'] = api.debugPort;
		}

		request.onReopen = function(response) {
			api.dialog.hide();
		};

		request.onMessage = function(response) {
			try {
				var data = decodeResponse(response);
				if (data.sessionId != null) {
					uuid = data.sessionId;
				}
				// javascript2java response handling
				if (data.javaResponse != null && data.javaResponse.correlationId != null) {
					var correlationId = data.javaResponse.correlationId;
					if (responseHandlers[correlationId] != null) {
						var callback = responseHandlers[correlationId];
						delete responseHandlers[correlationId];
						callback(data.javaResponse);
					}
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

	function decodeResponse(response) {
		var message = response.responseBody;
		var data;
		if (binary) {
			if (message.byteLength == 1) {
				return {};// ignore atmosphere heartbeat
			}
			data = AppFrameMsgOutProto.decode(message);
			explodeEnumNames(data);
		} else {
			data = atmosphere.util.parseJSON(message);
		}
		return data;
	}

	function dispose() {
		atmosphere.unsubscribe(socket);
		socket = null;
		uuid = null;
		binary = null;
	}

	function send(message) {
		if (socket != null && socket.request.isOpen && !socket.request.closed) {
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

	function awaitResponse(callback, request, correlationId, timeout) {
		send(request);
		responseHandlers[correlationId] = callback;
		setTimeout(function() {
			if (responseHandlers[correlationId] != null) {
				delete responseHandlers[correlationId];
				callback(new Error("Java call timed out after " + timeout + " ms."));
			}
		}, timeout);
	}

	function getuuid() {
		return uuid;
	}

	function explodeEnumNames(data) {
		if (data != null) {
			if (Array.isArray(data)) {
				data.forEach(function(d) {
					explodeEnumNames(d);
				})
			} else {
				data.$type._fields.forEach(function(field) {
					if (field.resolvedType != null) {
						if (field.resolvedType.className === "Enum") {
							var enm = field.resolvedType.object;
							for ( var key in enm) {
								if (enm[key] === data[field.name]) {
									data[field.name] = key;
								}
							}
						} else if (field.resolvedType.className === "Message") {
							explodeEnumNames(data[field.name]);
						}
					}
				});
			}
		}
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.socket = {
				connect : connect,
				send : send,
				uuid : getuuid,
				awaitResponse : awaitResponse,
				dispose : dispose
			};
		}
	};
});