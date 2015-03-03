define(
		'webswing',
		[ 'require', 'jquery', 'webswing-base', 'atmosphere', 'ProtoBuf', 'bootstrap' ],
		function(require, $, webswingBase, atmosphere, ProtoBuf) {
			"use strict";
			var wsPlugins = {
				upload : 'webswing-upload'
			};

			require(
					[ wsPlugins.upload ],
					function(upload) {
						var config = {
							send : function(message) {
								if (socket != null && socket.request.isOpen) {
									if (typeof message == "string") {
										socket.push(message);
									}
									if (typeof message === "object") {
										socket.push(atmosphere.util.stringifyJSON(message));
									}
								}
							},
							onErrorMessage : function(text) {
								showDialog(messageDialog, text);
								atmosphere.unsubscribe();
							},
							onContinueOldSession : function() {
								ws.canPaint(false);
								showDialog(continueOldSessionDialog);
							},
							onApplicationSelection : function(apps) {
								$('#userName').append(ws.getUser());
								if (apps.length == 0) {
									$('#applicationsList').append('Sorry, there is no application available for you.');
									showDialog(applicationSelectorDialog);
								} else if (apps.length == 1) {
									webswing.startApplication(apps[0].name);
								} else {
									for ( var i in apps) {
										var app = apps[i];
										if (app.name == 'adminConsoleApplicationName') {
											$('#applicationsList')
													.append(
															'<div class="col-sm-6 col-md-4"><div class="thumbnail" onclick="window.location.href = \'/admin\';"><img src="/admin/img/admin.png" class="img-thumbnail"/><div class="caption">Admin console</div></div></div>');
										} else {
											$('#applicationsList').append(
													'<div class="col-sm-6 col-md-4"><div class="thumbnail" onclick="webswing.startApplication(\''
															+ app.name + '\')"><img src="data:image/png;base64,' + app.base64Icon
															+ '" class="img-thumbnail"/><div class="caption">' + app.name + '</div></div></div>');
										}
									}
									showDialog(applicationSelectorDialog);
								}
							},
							onApplicationShutdown : function() {
								showDialog(
										messageDialog,
										'Application stopped... <br\> <button class="btn btn-primary" onclick="webswing.continueSession(false)">Start new session.</button>'
												+ '<span> </span><button class="btn btn-default" onclick="window.location.href = \'/logout\'">Logout.</button>');
								atmosphere.unsubscribe();
							},
							onBeforePaint : function() {
								showDialog(null);
							},
							onLinkOpenAction : function(url) {
								window.open(url, '_blank');
							},
							onPrintAction : function(url) {
								window.open('/print/viewer.html?file=' + encodeURIComponent('/file?id=' + url), '_blank');
							},
							onFileDownloadAction : function(url) {
								downloadURL('/file?id=' + url);
							},
							onFileDialogAction : function(data) {
								if (data.eventType === 'Open') {
									wsApi.upload.open(data, ws.getClientId());
								} else if (data.eventType === 'Close') {
									wsApi.upload.close();
								}
							},
							clientId : setupClientID(),
							hasControl : true,
							mirrorMode : false
						};
						var ws = webswingBase(config);

						var wsApi = {
							rootElement : $('#body')
						};
						wsApi.upload = upload.init(wsApi);
						wsApi.ws = ws;

						var proto = ProtoBuf.loadProtoFile("/webswing.proto");
						var loginDialog = $('#loginDialog');
						var initializingDialog = $('#initializingDialog');
						var connectingDialog = $('#connectingDialog');
						var applicationSelectorDialog = $('#applicationSelectorDialog');
						var continueOldSessionDialog = $('#continueOldSessionDialog');
						var startingDialog = $('#startingDialog');
						var disconnectedDialog = $('#disconnectedDialog');
						var messageDialog = $('#messageDialog');
						var messageDialogText = $('#messageDialogText');
						var typedArraysSupported = false;
						var canvas = null;
						var socket = null;

						login(); // check if already logged in

						$("#passwordInput").keyup(function(event) {
							if (event.keyCode == 13) {
								login();
							}
						});

						function start() {
							createCanvas();
							showDialog(initializingDialog);
							connect();
							$(window).bind("beforeunload", function() {
								ws.dispose();
							});
						}

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

							if (typedArraysSupported) {
								request.headers = {
									'X-Atmosphere-Binary' : true
								};
								request.enableProtocol = false;
								request.trackMessageLength = false;
								request.contentType = 'application/octet-stream';
								request.webSocketBinaryType = 'arraybuffer';
							}

							request.onOpen = function(response) {
								ws.setUuid(response.request.uuid);
							};

							request.onReopen = function(response) {
								showDialog(null);
							};

							request.onMessage = function(response) {
								var message = response.responseBody;
								try {
									var data = atmosphere.util.parseJSON(message);
									ws.processJsonMessage(data);
								} catch (e) {
									// ws.processTxtMessage(response.responseBody);
									return;
								}
							};

							request.onClose = function(response) {
								// need to wait until animated transition finish
								setTimeout(function() {
									if (!messageDialog.hasClass('in')) {
										showDialog(disconnectedDialog);
									}
								}, 1000);
							};

							request.onError = function(response) {
								// TODO:handle
							};

							request.onReconnect = function(request, response) {
								showDialog(initializingDialog);
							};

							socket = atmosphere.subscribe(request);
						}

						function login() {
							var errorMsg = $('#loginErrorMsg');
							$.ajax({
								type : 'POST',
								url : '/login?mode=swing',
								data : $("#loginForm").serialize(),
								success : function(data) {
									errorMsg.html('');
									start();
								},
								error : function(data) {
									if (!loginDialog.hasClass('in')) {
										showDialog(loginDialog);
									} else {
										errorMsg.html('<div class="alert alert-danger">' + data.responseText + '</div>');
									}
								}
							});
						}

						function setupClientID() {
							var cookieName = 'webswingID';
							var id = readCookie(cookieName);
							if (id != null) {
								eraseCookie(cookieName);
							} else {
								id = GUID();
							}
							createCookie(cookieName, id, 1);
							return id;
						}

						function GUID() {
							var S4 = function() {
								return Math.floor(Math.random() * 0x10000).toString(16);
							};
							return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
						}

						function createCookie(name, value, days) {
							var expires;

							if (days) {
								var date = new Date();
								date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
								expires = "; expires=" + date.toGMTString();
							} else {
								expires = "";
							}
							document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
						}

						function readCookie(name) {
							var nameEQ = escape(name) + "=";
							var ca = document.cookie.split(';');
							for ( var i = 0; i < ca.length; i++) {
								var c = ca[i];
								while (c.charAt(0) === ' ')
									c = c.substring(1, c.length);
								if (c.indexOf(nameEQ) === 0)
									return unescape(c.substring(nameEQ.length, c.length));
							}
							return null;
						}

						function eraseCookie(name) {
							createCookie(name, "", -1);
						}

						function createCanvas() {
							$("#body").append('<canvas id="canvas" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
							canvas = document.getElementById("canvas");
							ws.setCanvas(canvas);
							window.onresize = function() {
								canvas.width = width();
								canvas.height = height();
								ws.resizedWindow();
							};
						}

						function showDialog(dialog, text) {
							loginDialog.modal('hide');
							connectingDialog.modal('hide');
							startingDialog.modal('hide');
							initializingDialog.modal('hide');
							disconnectedDialog.modal('hide');
							continueOldSessionDialog.modal('hide');
							applicationSelectorDialog.modal('hide');
							messageDialog.modal('hide');
							if (dialog != null) {
								if (dialog == messageDialog) {
									messageDialogText.html(text);
								}
								dialog.modal('show');
							}
						}

						function downloadURL(url) {
							var hiddenIFrameID = 'hiddenDownloader', iframe = document.getElementById(hiddenIFrameID);
							if (iframe === null) {
								iframe = document.createElement('iframe');
								iframe.id = hiddenIFrameID;
								iframe.style.display = 'none';
								document.body.appendChild(iframe);
							}
							iframe.src = url;
						}

						function width() {
							return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 0;
						}

						function height() {
							return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 0;
						}

						window.webswing = {
							continueSession : function(toContinue) {
								if (toContinue) {
									showDialog(null);
									ws.canPaint(true);
									ws.handshake();
									ws.repaint();
									ws.ack();
								} else {
									ws.kill();
									eraseCookie('webswingID');
									location.reload();
								}
							},
							startApplication : function(name) {
								ws.setClientId(ws.getUser() + ws.getClientId() + name);
								ws.setApplication(name);
								ws.canPaint(true);
								ws.handshake();
								showDialog(startingDialog);
							},
							login : function() {
								login();
							},
							setTypedArraysSupported : function(supported) {
								ws.setDirectDrawSupported(supported);
								typedArraysSupported = supported;
							}
						};

					});

		});

define('webswing-dialog', [ 'jquery', 'microtemplate', 'bootstrap', 'text!../templates/dialog.html' ], function($, tmpl) {
	"use strict";

	var api;

	function setup(api) {
		api.rootElement.append(html);

	}

	function show() {

	}

	function hide() {

	}

	return {
		init : function(wsApi) {
			api = wsApi;
			setup(api);
			return {
				show : show,
				hide : hide
			}
		}
	}
});
