define([ 'jquery', 'text!templates/upload.html', 'text!templates/upload.css', 'jquery.iframe-transport', 'jquery.fileupload' ], function amdFactory($, html, css) {
	"use strict";
	var style = $("<style></style>", {
		type : "text/css"
	});
	style.text(css);
	$("head").prepend(style);

	return function FilesModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			send : 'socket.send'
		};
		module.provides = {
			process : process,
			close : close,
			download : download,
			link : link,
			print : print
		};

		var jqXHR_fileupload = [];
		var doneFileList = [];
		var timeout;
		var errorTimeout;
		var closeAfterErrorTimeout;
		var uploadBar, fileDialogTransferBarClientId, fileDialogErrorMessage;
		var fileDialogErrorMessageContent, deleteSelectedButton, downloadSelectedButton;
		var dropZone, fileUpload, uploadProgressBar, uploadProgress, cancelBtn, downloadBtn, uploadBtn, deleteBtn, fileInput;
		var autoUploadBar, autoFileupload, autoFileInput, cancelAutoUploadButton, autoUploadfileDialogTransferBarClientId;

		function process(event) {
			if (event.eventType === 'AutoUpload') {
				autoUpload(event, api.cfg.clientId);
			} else if (event.eventType === 'Open') {
				open(event, api.cfg.clientId);
			} else if (event.eventType === 'Close') {
				close();
			}
		}

		function autoUpload(data, clientId) {
			if (autoUploadBar == null) {
				setup(api);
			}
			if (autoUploadBar.closest(api.cfg.rootElement).length === 0) {
				api.cfg.rootElement.append(autoUploadBar);
			}
			autoUploadfileDialogTransferBarClientId.val(clientId);
			autoFileInput.prop("multiple", data.isMultiSelection);
			autoFileInput.attr("accept", data.filter);
setup
			open(data, clientId);
		}

		function open(data, clientId) {
			closeAfterErrorTimeout=false;
			if (uploadBar == null) {
				setup(api);
			}
			if (uploadBar.closest(api.cfg.rootElement).length === 0) {
				api.cfg.rootElement.append(uploadBar);
			}
			fileDialogTransferBarClientId.val(clientId);
			showOrHide(downloadBtn, data.allowDownload);
			showOrHide(uploadBtn, data.allowUpload);
			showOrHide(dropZone, data.allowUpload);
			showOrHide(deleteBtn, data.allowDelete);
			fileInput.prop("multiple", data.isMultiSelection);
			fileInput.attr("accept", data.filter);
			setProgressBarVisible(false);
			autoUploadBar.show().animate({
				"top":"0px",
				"opacity":"1"
			}, 200);
		}

		function close() {
				if (uploadBar != null && uploadBar.closest(api.cfg.rootElement).length !== 0) {
					if (!errorTimeout) {
						autoUploadBar.animate({
							"top":"50px",
							"opacity":"0"
						}, 200);
						uploadBar.detach();
					}else{
						closeAfterErrorTimeout=true;
					}
				}
				if (autoUploadBar != null && autoUploadBar.closest(api.cfg.rootElement).length !== 0) {
					autoUploadBar.animate({
						"top":"50px",
						"opacity":"0"
					}, 200).hide( 0, function(){
						autoUploadBar.detach();
					} );
				}
			
		}

		function setup() {
			api.cfg.rootElement.append(html);
			uploadBar = api.cfg.rootElement.find('div[data-id="uploadBar"]');
			fileDialogTransferBarClientId = uploadBar.find('input[data-id="fileDialogTransferBarClientId"]');
			fileDialogErrorMessage = uploadBar.find('div[data-id="fileDialogErrorMessage"]');
			fileDialogErrorMessageContent = uploadBar.find('div[data-id="fileDialogErrorMessageContent"]');
			deleteSelectedButton = uploadBar.find('button[data-id="deleteSelectedButton"]');
			downloadSelectedButton = uploadBar.find('button[data-id="downloadSelectedButton"]');
			dropZone = uploadBar.find('div[data-id="fileDropArea"]');
			fileUpload = uploadBar.find('form[data-id="fileupload"]');
			uploadProgressBar = uploadBar.find('div[data-id="fileDialogTransferProgressBar"]');
			uploadProgress = uploadBar.find('div[data-id="progress"] .progress-bar');
			cancelBtn = uploadBar.find('div[data-id="cancelBtn"]');
			downloadBtn = uploadBar.find('div[data-id="fileDownloadBtn"]');
			uploadBtn = uploadBar.find('div[data-id="fileUploadBtn"]');
			deleteBtn = uploadBar.find('div[data-id="fileDeleteBtn"]');
			fileInput = uploadBar.find('input[data-id="fileInput"]');

			autoUploadBar = api.cfg.rootElement.find('div[data-id="autoUploadBar"]');
			autoFileupload = autoUploadBar.find('form[data-id="autoFileupload"]');
			cancelAutoUploadButton = autoUploadBar.find('button[data-id="cancelAutoUploadButton"]');
			autoFileInput = autoUploadBar.find('input[data-id="autoFileInput"]');
			autoUploadfileDialogTransferBarClientId = autoUploadBar.find('input[data-id="autoUploadfileDialogTransferBarClientId"]');

			var autoJqUpload = autoFileupload.fileupload({
				xhrFields : {
					withCredentials : true
				},
				url : api.cfg.connectionUrl + 'file',
				dataType : 'json'
			});
			autoJqUpload.bind('fileuploadfail', fileuploadfail);
			autoJqUpload.bind("fileuploadprogressall", fileuploadprogressall);
			autoJqUpload.bind('fileuploadadd', fileuploadadd);

			var jqUpload = fileUpload.fileupload({
				xhrFields : {
					withCredentials : true
				},
				url : api.cfg.connectionUrl + 'file',
				dataType : 'json',
				dropZone : dropZone
			});
			jqUpload.bind('fileuploadfail', fileuploadfail);
			jqUpload.bind("fileuploadprogressall", fileuploadprogressall);
			jqUpload.bind('fileuploadadd', fileuploadadd);

			function fileuploadadd(e, data) {
				data.files.forEach(function(file) {
					doneFileList.push(file.name);
				});
				jqXHR_fileupload.push(data);
				setProgressBarVisible(true);
			}

			function fileuploadfail(e, data) {
				if (!errorTimeout) {
					fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
					fileDialogErrorMessage.show().animate({
						"top":"0px",
						"opacity":"1"
					}, 200);
				} else {
					fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
					clearTimeout(errorTimeout);
				}
				data.files.forEach(function(file) {
					var index = doneFileList.indexOf(file.name);
					doneFileList.splice(index, 1);
				});
				errorTimeout = setTimeout(function() {
					errorTimeout = null;
					fileDialogErrorMessageContent.html("");
					fileDialogErrorMessage.fadeOut( 200 );
					if(closeAfterErrorTimeout){
						close();
					}
				}, 5000);
			}

			function fileuploadprogressall(e, data) {
				var progress = parseInt(data.loaded / data.total * 100, 10);
				uploadProgress.css('width', progress + '%');
				if (progress === 100) {
					setTimeout(function() {
						filesUploaded(doneFileList);
						doneFileList = [];
					}, 1000);
					setProgressBarVisible(false);
					jqXHR_fileupload = [];
				}
			}

			cancelAutoUploadButton.bind('click', function(e) {
				sendMessageEvent('cancelAutoUpload');
			});

			deleteSelectedButton.bind('click', function(e) {
				sendMessageEvent('deleteFile');
			});

			downloadSelectedButton.bind('click', function(e) {
				sendMessageEvent('downloadFile');
			});

			api.cfg.rootElement.bind('drop', function(e) {
				e.preventDefault();
			});

			api.cfg.rootElement.bind('dragover', function(e) {
				if (!timeout) {
					dropZone.addClass('in');
				} else {
					clearTimeout(timeout);
				}

				timeout = setTimeout(function() {
					timeout = null;
					dropZone.removeClass('in');
				}, 100);
			});

			cancelBtn.click(function() {
				filesUploaded([]);
				jqXHR_fileupload.forEach(function(el) {
					el.abort();
				});
				setProgressBarVisible(false);
			});

			uploadBar.detach();
			autoUploadBar.detach();
		}

		function showOrHide(element, bool) {
			if (bool) {
				element.show().animate({
					"top":"0px",
					"opacity":"1"
				}, 200);
			} else {
				element.animate({
					"top":"50px",
					"opacity":"0"
				}, 200).hide();
			}
		}

		function setProgressBarVisible(bool) {
			if (bool) {
				uploadProgress.css('width', '0%');
				uploadProgressBar.fadeIn( 200 );
			} else {
				uploadProgressBar.fadeOut( 200 );
				uploadProgress.css('width', '0%');
			}
		}

		function filesUploaded(files) {
			api.send({
				uploaded : {
					files : files
				}
			});
		}

		function sendMessageEvent(message) {
			api.send({
				events : [ {
					event : {
						type : message
					}
				} ]
			});
		}

		function download(url) {
			var hiddenIFrameID = 'hiddenDownloader', iframe = document.getElementById(hiddenIFrameID);
			if (iframe === null) {
				iframe = document.createElement('iframe');
				iframe.id = hiddenIFrameID;
				iframe.style.display = 'none';
				document.body.appendChild(iframe);
			}
			iframe.src = api.cfg.connectionUrl + url;
		}

		function link(url) {
			window.open(url, '_blank');
		}

		function print(url) {
			window.open(api.cfg.connectionUrl + 'print/viewer.html?file=' + url, '_blank');
		}
	};
});