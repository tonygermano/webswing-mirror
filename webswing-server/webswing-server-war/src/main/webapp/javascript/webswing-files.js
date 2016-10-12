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
		var autoSaveBar, autoSaveInput, cancelAutoSaveButton, autoSaveButton;

		function process(event) {
			if (event.eventType === 'AutoUpload') {
				autoUpload(event, api.cfg.clientId);
			} else if (event.eventType === 'AutoSave') {
				autoSave(event, api.cfg.clientId);
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
			animateShow(autoUploadBar);
		}

		function autoSave(data, clientId) {
			if (autoSaveBar == null) {
				setup(api);
			}
			if (autoSaveBar.closest(api.cfg.rootElement).length === 0) {
				api.cfg.rootElement.append(autoSaveBar);
			}
			autoSaveInput.val(data.selection);
			animateShow(autoSaveBar);
		}

		function open(data, clientId) {
			closeAfterErrorTimeout = false;
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
			animateShow(uploadBar);
		}

		function close() {
			if (uploadBar != null && uploadBar.closest(api.cfg.rootElement).length !== 0) {
				if (!errorTimeout) {
					uploadBar.hide("fast");
					uploadBar.detach();
				} else {
					closeAfterErrorTimeout = true;
				}
			}
			if (autoUploadBar != null && autoUploadBar.closest(api.cfg.rootElement).length !== 0) {
				autoUploadBar.hide("fast");
				autoUploadBar.detach();
			}
			if (autoSaveBar != null && autoSaveBar.closest(api.cfg.rootElement).length !== 0) {
				autoSaveBar.hide("fast");
				autoSaveBar.detach();
			}
		}

		function setup() {
			api.cfg.rootElement.append(html);

			uploadBar = api.cfg.rootElement.find('div[data-id="uploadBar"]');
			fileDialogTransferBarClientId = uploadBar.find('input[data-id="fileDialogTransferBarClientId"]');
			deleteSelectedButton = uploadBar.find('button[data-id="deleteSelectedButton"]');
			downloadSelectedButton = uploadBar.find('button[data-id="downloadSelectedButton"]');
			dropZone = uploadBar.find('div[data-id="fileDropArea"]');
			fileUpload = uploadBar.find('form[data-id="fileupload"]');
			cancelBtn = uploadBar.find('div[data-id="cancelBtn"]');
			downloadBtn = uploadBar.find('div[data-id="fileDownloadBtn"]');
			uploadBtn = uploadBar.find('div[data-id="fileUploadBtn"]');
			deleteBtn = uploadBar.find('div[data-id="fileDeleteBtn"]');
			fileInput = uploadBar.find('input[data-id="fileInput"]');

			uploadProgressBar = api.cfg.rootElement.find('div[data-id="fileDialogTransferProgressBar"]');
			uploadProgress = api.cfg.rootElement.find('div[data-id="progress"] .js-progress__bar');
			fileDialogErrorMessage = api.cfg.rootElement.find('div[data-id="fileDialogErrorMessage"]');
			fileDialogErrorMessageContent = api.cfg.rootElement.find('div[data-id="fileDialogErrorMessageContent"]');

			autoUploadBar = api.cfg.rootElement.find('div[data-id="autoUploadBar"]');
			autoFileupload = autoUploadBar.find('form[data-id="autoFileupload"]');
			cancelAutoUploadButton = autoUploadBar.find('button[data-id="cancelAutoUploadButton"]');
			autoFileInput = autoUploadBar.find('input[data-id="autoFileInput"]');
			autoUploadfileDialogTransferBarClientId = autoUploadBar.find('input[data-id="autoUploadfileDialogTransferBarClientId"]');

			autoSaveBar = api.cfg.rootElement.find('div[data-id="autoSaveBar"]');
			autoSaveInput = autoSaveBar.find('input[data-id="autoSaveInput"]');
			autoSaveButton = autoSaveBar.find('button[data-id="autoSaveButton"]');
			cancelAutoSaveButton = autoSaveBar.find('button[data-id="cancelAutoSaveButton"]');

			// hide all
			uploadProgressBar.hide();
			autoUploadBar.hide();
			autoUploadBar.detach();
			autoSaveBar.hide();
			autoSaveBar.detach();
			uploadBar.hide();
			uploadBar.detach();

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
				setProgressBarVisible(false);
				if (data.jqXHR.statusText != 'abort') {
					if (!errorTimeout) {
						fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
						animateShow(fileDialogErrorMessage);
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
						fileDialogErrorMessage.hide("fast");
						if (closeAfterErrorTimeout) {
							close();
						}
					}, 7000);
				}
			}

			// Changes the looks of the progress bar based on the percentage of
			// data loaded
			function fileuploadprogressall(e, data) {
				var progress = parseInt(data.loaded / data.total * 100, 10);
				var $jsProgressBar = $(".js-progress__bar");
				var $jsProgressText = $(".js-progress__text");
				$jsProgressBar.css('width', progress + '%');
				$jsProgressText.find("em").text(progress + "%");
				if (progress === 100) {
					$jsProgressText.find("em").text("Complete");
					setTimeout(function() {
						if (!errorTimeout) {
							filesSelected(doneFileList);
						}
						doneFileList = [];
						setProgressBarVisible(false);
					}, 1000);
					jqXHR_fileupload = [];
				}
			}

			function cancelFileSelection(e) {
				sendMessageEvent('cancelFileSelection');
				cancelUpload();
			}

			function cancelUpload() {
				filesSelected([]);
				jqXHR_fileupload.forEach(function(el) {
					el.abort();
				});
				setProgressBarVisible(false);
			}

			cancelAutoSaveButton.bind('click', cancelFileSelection);
			cancelAutoUploadButton.bind('click', cancelFileSelection);
			cancelBtn.bind('click', cancelUpload);

			deleteSelectedButton.bind('click', function(e) {
				sendMessageEvent('deleteFile');
			});

			downloadSelectedButton.bind('click', function(e) {
				sendMessageEvent('downloadFile');
			});

			api.cfg.rootElement.bind('drop', function(e) {
				e.preventDefault();
			});

			autoSaveButton.bind('click', function(e) {
				var fileString = autoSaveInput.val();
				filesSelected([ fileString ]);
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

		}

		function animateShow(element) {
			element.show().animate({
				"top" : "0px",
				"opacity" : "1"
			}, 200);
		}
		function animateHide(element) {
			element.animate({
				"top" : "50px",
				"opacity" : "0"
			}, 200).hide();
		}

		function showOrHide(element, bool) {
			if (bool) {
				animateShow(element);
			} else {
				animateHide(element);
			}
		}

		function setProgressBarVisible(bool) {
			if (bool) {
				uploadProgress.css('width', '0%');
				uploadProgressBar.fadeIn(200);
			} else {
				uploadProgressBar.fadeOut(200);
				uploadProgress.css('width', '0%');
			}
		}

		function filesSelected(files) {
			api.send({
				selected : {
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