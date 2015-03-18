define([ 'jquery', 'text!templates/upload.html', 'text!templates/upload.css', 'jquery.iframe-transport', 'jquery.fileupload' ],
		function($, html, css) {
			"use strict";

			var jqXHR_fileupload = [];
			var doneFileList = [];
			var timeout;
			var errorTimeout;
			var api;
			var uploadBar, fileDialogTransferBarClientId, fileDialogErrorMessage;
			var fileDialogErrorMessageContent, deleteSelectedButton, downloadSelectedButton;
			var dropZone, fileUpload, uploadProgressBar, uploadProgress, cancelBtn, downloadBtn, uploadBtn, deleteBtn, fileInput;

			function open(data, clientId) {
				if (uploadBar == null) {
					setup(api);
				}
				fileDialogTransferBarClientId.val(clientId);
				showOrHide(downloadBtn, data.allowDownload);
				showOrHide(uploadBtn, data.allowUpload);
				showOrHide(dropZone, data.allowUpload);
				showOrHide(deleteBtn, data.allowDelete);
				fileInput.prop("multiple", data.isMultiSelection);
				fileInput.attr("accept", data.filter);
				setProgressBarVisible(false);
				uploadBar.show("fast");
			}

			function close() {
				uploadBar.hide("fast");
			}

			function setup(api) {
				api.rootElement.append(html);
				var style = $("<style></style>", {
					type : "text/css"
				});
				style.text(css);
				$("head").prepend(style);

				uploadBar = api.rootElement.find('div[data-id="uploadBar"]');
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

				deleteSelectedButton.bind('click', function(e) {
					sendMessageEvent('deleteFile');
				});

				downloadSelectedButton.bind('click', function(e) {
					sendMessageEvent('downloadFile');
				});

				api.rootElement.bind('drop', function(e) {
					e.preventDefault();
				});

				api.rootElement.bind('dragover', function(e) {
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

				var jqUpload = fileUpload.fileupload({
					dataType : 'json',
					dropZone : dropZone
				});

				jqUpload.on('fileuploadadd', function(e, data) {
					data.files.forEach(function(file) {
						doneFileList.push(file.name);
					});
					jqXHR_fileupload.push(data);
					setProgressBarVisible(true);
				});

				jqUpload.bind('fileuploadfail', function(e, data) {
					if (!errorTimeout) {
						fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
						fileDialogErrorMessage.show("fast");
					} else {
						fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
						clearTimeout(timeout);
					}
					errorTimeout = setTimeout(function() {
						errorTimeout = null;
						fileDialogErrorMessageContent.html("");
						fileDialogErrorMessage.hide("fast");
					}, 5000);
				});

				jqUpload.bind("fileuploadprogressall", function(e, data) {
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
				});

				cancelBtn.click(function() {
					filesUploaded([]);
					jqXHR_fileupload.forEach(function(el) {
						el.abort();
					});
					setProgressBarVisible(false);
				});
			}

			function showOrHide(element, bool) {
				if (bool) {
					element.show();
				} else {
					element.hide();
				}
			}

			function setProgressBarVisible(bool) {
				if (bool) {
					uploadProgress.css('width', '0%');
					uploadProgressBar.show("fast");
				} else {
					uploadProgressBar.hide("fast");
					uploadProgress.css('width', '0%');
				}
			}

			function filesUploaded(files) {
				api.socket.send({
					uploaded : {
						clientId : api.context.clientId,
						files : files
					}
				});
			}

			function sendMessageEvent(message) {
				api.socket.send({
					events : [ {
						event : {
							type : message,
							clientId : api.context.clientId
						}
					} ]
				});
			}

			function download(url) {
				var hiddenIFrameID = 'hiddenDownloader';
				var iframe = api.rootElement.find('iframe[data-id="' + hiddenIFrameID + '"]');
				if (iframe.length === 0) {
					iframe = $(document.createElement('iframe'));
					iframe.data('id', hiddenIFrameID);
					iframe.hide();
					api.rootElement.append(iframe);
				}
				iframe.src = url;
			}

			function link(url) {
				window.open(url, '_blank');
			}

			function print(url) {
				window.open('/print/viewer.html?file=' + url, '_blank');
			}

			return {
				init : function(wsApi) {
					api = wsApi;
					wsApi.files = {
						open : open,
						close : close,
						download : download,
						link : link,
						print : print
					};
				}
			};
		});