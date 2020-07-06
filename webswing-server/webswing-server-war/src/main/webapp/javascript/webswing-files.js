import htmlTemplate from './templates/upload.html'
import 'blueimp-file-upload'
"use strict";

    export default function Files(util) {
        var module = this;
        var api;
        var html;
        module.injects = api = {
            cfg: 'webswing.config',
            socketId: 'socket.uuid',
            send: 'socket.send',
            translate: 'translate.translate',
            getFocusedWindow: 'base.getFocusedWindow',
            focusDefault: 'focusManager.focusDefault'
        };
        module.provides = {
            process: process,
            close: close,
            download: download,
            link: link,
            print: print,
            redirect: redirect
        };
        module.ready = function () {
            html = api.translate(htmlTemplate);
        };

        var holderMap = {};
        // <window>: {autoUpload, autoSave, open, close, download, link, print}
        
        function process(event) {
            if (event.eventType === 'AutoUpload') {
                autoUpload(event, api.socketId);
            } else if (event.eventType === 'AutoSave') {
                autoSave(event, api.socketId);
            } else if (event.eventType === 'Open') {
                open(event, api.socketId);
            } else if (event.eventType === 'Close') {
                close();
            }
        }
        
        function getHolder() {
        	var win = api.getFocusedWindow();
        	
        	// try cleanup
        	for (var winName in holderMap) {
        		if (!holderMap[winName].win || holderMap[winName].win.closed) {
        			delete holderMap[winName];
        		}
        	}
        	
        	if (!holderMap[win.name]) {
        		setup(win);
        	}
        	
        	return holderMap[win.name];
        }

        function autoUpload(data, uuid) {
        	getHolder().autoUpload(data, uuid);
        }

        function autoSave(data, uuid) {
        	getHolder().autoSave(data, uuid);
        }

        function open(data, uuid) {
        	getHolder().open(data, uuid);
        }

        function close() {
        	getHolder().close();
        }
        
        function download(url) {
        	getHolder().download(url);
        }

        function link(url) {
        	getHolder().link(url);
        }

        function print(url) {
        	getHolder().print(url);
        }
        
        function redirect(url) {
        	window.location.href = url;
        }

        function setup(win) {
        	holderMap[win.name] = {};
        	
        	var fh = holderMap[win.name];
        	fh.win = win;
        	
        	var doc = $(win.document);
        	var rootElement = doc.find(".webswing-element-content");
            rootElement.append(html);
            
            var jqXHR_fileupload = [];
            var uploadingFiles = [];
            var uploadedFiles = [];
            var timeout;
            var errorTimeout;
            var closeAfterErrorTimeout;
            
            var uploadBar = rootElement.find('div[data-id="uploadBar"]');
            var fileDialogTransferBarClientId = uploadBar.find('input[data-id="fileDialogTransferBarClientId"]');

            var fileActionButtonGroup = uploadBar.find('div[data-id="fileActionButtonGroup"]');
            var deleteSelectedButton = uploadBar.find('button[data-id="deleteSelectedButton"]');
            var downloadSelectedButton = uploadBar.find('button[data-id="downloadSelectedButton"]');
            var uploadBtn = uploadBar.find('div[data-id="fileUploadBtn"]');

            var dropZone = uploadBar.find('div[data-id="fileDropArea"]');
            var fileUpload = uploadBar.find('form[data-id="fileupload"]');
            var cancelBtn = uploadBar.find('button[data-id="cancelBtn"]');
            var getFileInput = ()=>uploadBar.find('input[data-id="fileInput"]');

            var uploadProgressBar = rootElement.find('div[data-id="fileDialogTransferProgressBar"]');
            var uploadProgress = rootElement.find('div[data-id="progress"] .ws-progress-bar');
            var fileDialogErrorMessage = rootElement.find('div[data-id="fileDialogErrorMessage"]');
            var fileDialogErrorMessageContent = rootElement.find('div[data-id="fileDialogErrorMessageContent"]');
            var fileDialogSuccessMessage = rootElement.find('div[data-id="fileDialogSuccessMessage"]');
            var fileDialogSuccessMessageContent = rootElement.find('div[data-id="fileDialogSuccessMessageContent"]');

            var autoUploadBar = rootElement.find('div[data-id="autoUploadBar"]');
            var autoFileupload = autoUploadBar.find('form[data-id="autoFileupload"]');
            var uploadAutoUploadButton = autoUploadBar.find('button[data-id="uploadAutoUploadButton"]');
            var cancelAutoUploadButton = autoUploadBar.find('button[data-id="cancelAutoUploadButton"]');
            var getAutoFileInput = ()=>autoUploadBar.find('input[data-id="autoFileInput"]');
            var autoUploadfileDialogTransferBarClientId = autoUploadBar.find('input[data-id="autoUploadfileDialogTransferBarClientId"]');

            var autoSaveBar = rootElement.find('div[data-id="autoSaveBar"]');
            var autoSaveInput = autoSaveBar.find('input[data-id="autoSaveInput"]');
            var autoSaveButton = autoSaveBar.find('button[data-id="autoSaveButton"]');
            var cancelAutoSaveButton = autoSaveBar.find('button[data-id="cancelAutoSaveButton"]');

            var linkDialog = rootElement.find('div[data-id="downloadLinkDialog"]');

            // hide all
            uploadProgressBar.hide();
            autoUploadBar.hide();
            autoUploadBar.detach();
            autoSaveBar.hide();
            autoSaveBar.detach();
            uploadBar.hide();
            uploadBar.detach();
            linkDialog.hide();
            linkDialog.detach();

            var autoJqUpload = autoFileupload.fileupload({
                xhrFields: {
                    withCredentials: true
                },
                url: api.cfg.connectionUrl + 'file',
                dataType: 'json',
                dropZone: null
            });
            autoJqUpload.bind('fileuploadfail', fileuploadfail);
            autoJqUpload.bind('fileuploadprogressall', fileuploadprogressall);
            autoJqUpload.bind('fileuploadadd', (e, data) => fileuploadadd(e,data,autoJqUpload));
            autoJqUpload.bind('fileuploaddone', fileuploaddone);

            var jqUpload = fileUpload.fileupload({
                xhrFields: {
                    withCredentials: true
                },
                url: api.cfg.connectionUrl + 'file',
                dataType: 'json',
                dropZone: dropZone
            });
            jqUpload.bind('fileuploadfail', fileuploadfail);
            jqUpload.bind('fileuploadprogressall', fileuploadprogressall);
            jqUpload.bind('fileuploadadd', (e,data) => fileuploadadd(e,data,jqUpload));
            jqUpload.bind('fileuploaddone', fileuploaddone);

            function fileuploadadd(e, data, fileuploadelement) {
            	fileDialogSuccessMessageContent.html("");
            	fileDialogSuccessMessage.hide("fast");
            	
                fileuploadelement.url = api.cfg.connectionUrl + 'file?uuid='+api.socketId();
                data.files.forEach(function (file) {
                    uploadingFiles.push(file.name);
                });
                jqXHR_fileupload.push(data);
                setProgressBarVisible(true);
            }

            function fileuploadfail(e, data) {
                setProgressBarVisible(false);
                if (data.jqXHR.statusText != 'abort') {
                	fileDialogErrorMessageContent.append('<p>' + data.jqXHR.responseText + '</p>');
                    if (!errorTimeout) {
                        animateShow(fileDialogErrorMessage);
                    } else {
                        clearTimeout(errorTimeout);
                    }
                    data.files.forEach(function (file) {
                        var index = uploadingFiles.indexOf(file.name);
                        uploadingFiles.splice(index, 1);
                    });
                    checkUploadingFinished();
                    errorTimeout = setTimeout(function () {
                        errorTimeout = null;
                        fileDialogErrorMessageContent.html("");
                        fileDialogErrorMessage.hide("fast");
                        if (closeAfterErrorTimeout) {
                            close();
                        }
                    }, 3000);
                }
            }

            // Changes the looks of the progress bar based on the percentage of
            // data loaded
            function fileuploadprogressall(e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                var $jsProgressBar = rootElement.find(".ws-progress-bar");
                var $jsProgressText = rootElement.find(".ws-progress-text");
                $jsProgressBar.css('width', progress + '%');
                $jsProgressText.find("em").text(progress + "%");
            }

            function fileuploaddone(e, data) {
                var $jsProgressText = rootElement.find(".ws-progress-text");
                $jsProgressText.find("em").text(api.translate("files.progComplete"));
                var finishedFile = data.result.files[0].name;
                uploadedFiles.push(finishedFile);
                uploadingFiles.splice(uploadingFiles.indexOf(finishedFile),1);
                checkUploadingFinished();
            }

            function checkUploadingFinished() {
            	if (uploadingFiles.length == 0) {
            		if (uploadedFiles.length > 0) {
            			fileDialogSuccessMessageContent.html(api.translate('<p>${files.uploadComplete}</p>'));
                    	animateShow(fileDialogSuccessMessage);
            		}
            		
                    filesSelected(uploadedFiles);
                    uploadedFiles = [];
                    setTimeout(function() {
                    	setProgressBarVisible(false);
                    }, 5000);
                    jqXHR_fileupload = [];
                }
            }
            
            function isEqual(array1,array2){
                return array1.length === array2.length && array1.sort().every(function(value, index) { return value === array2.sort()[index]});
            }

            function cancelFileSelection(e) {
                sendMessageEvent('cancelFileSelection');
                cancelUpload();
            }

            function cancelUpload() {
                filesSelected([]);
                jqXHR_fileupload.forEach(function (el) {
                    el.abort();
                });
                setProgressBarVisible(false);
            }

            cancelAutoSaveButton.bind('click', cancelFileSelection);
            cancelAutoUploadButton.bind('click', cancelFileSelection);
            cancelBtn.bind('click', cancelFileSelection);

            deleteSelectedButton.bind('click', function (e) {
                sendMessageEvent('deleteFile');
            });

            downloadSelectedButton.bind('click', function (e) {
                sendMessageEvent('downloadFile');
            });

            rootElement.bind('drop', function (e) {
                e.preventDefault();
            });

            uploadAutoUploadButton.bind('click', function(e) {
            	getAutoFileInput().click();
            });
            
            uploadBtn.bind('click', function(e) {
            	getFileInput().click();
            });
            
            autoSaveInput.bind('input', validateFilename);

            autoSaveButton.bind('click', function (e) {
                var fileString = autoSaveInput.val();
                if (validateFilename() && fileString.length > 0) {
                    filesSelected([fileString]);
                }
            });

            function validateFilename() {
                var fileString = autoSaveInput.val();
                if (fileString.match(/^[a-zA-Z0-9. _-]*$/)) {
                	fileDialogErrorMessageContent.html("");
                	fileDialogErrorMessage.hide("fast");
                    return true;
                } else {
                	fileDialogErrorMessageContent.html(api.translate('<p>${files.saveInvlidFilename}</p>'));
                    animateShow(fileDialogErrorMessage);
                    return false;
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
            
            function displayDownloadLinkDialog(msg, url) {
                if (linkDialog.closest(rootElement).length === 0) {
                    rootElement.append(linkDialog);
                }

                var title = linkDialog.find('div[data-id="title"]');
                title.html(api.translate(msg.title));

                var message = linkDialog.find('div[data-id="message"]');
                message.html(api.translate(msg.message));

                var linkBtn = linkDialog.find('button[data-id="downloadLinkButton"]');
                linkBtn.html(api.translate(msg.buttonMessage));
                linkBtn.off('click').on('click', function (event) {
                	window.open(url, '_blank');
                	closeDownloadLinkDialog(linkDialog, rootElement);
                }).blur();

                var closeBtn = linkDialog.find('button[data-id="cancelDownloadLinkButton"]');
                closeBtn.off('click').on('click', function (event) {
                    closeDownloadLinkDialog(linkDialog, rootElement);
                }).blur();

                linkDialog.show();
                if (doc.activeElement) {
                	doc.activeElement.blur();
                }
               	linkBtn[0].focus();
            }

            function closeDownloadLinkDialog() {
                if (linkDialog != null && linkDialog.closest(rootElement).length !== 0) {
                	linkDialog.hide("fast");
                    linkDialog.detach();
                }
            }

            rootElement.bind('dragover', function (e) {
                if (!timeout) {
                	dropZone.addClass('ws-filebar-dropArea--ondrag');
                } else {
                    clearTimeout(timeout);
                }

                timeout = setTimeout(function () {
                	timeout = null;
                	dropZone.removeClass('ws-filebar-dropArea--ondrag');
                }, 100);
            });
            
            fh.autoUpload = function(data, uuid) {
            	if (autoUploadBar.closest(rootElement).length === 0) {
            		rootElement.append(autoUploadBar);
            	}
            	uploadingFiles = [];
            	uploadedFiles = [];
            	autoUploadfileDialogTransferBarClientId.val(uuid);
            	getAutoFileInput().prop("multiple", data.isMultiSelection);
            	getAutoFileInput().attr("accept", data.filter);
            	setProgressBarVisible(false);
                fileDialogErrorMessageContent.html("");
                fileDialogErrorMessage.hide("fast");
            	animateShow(autoUploadBar);
            };
            
            fh.autoSave = function(data, uuid) {
                if (autoSaveBar.closest(rootElement).length === 0) {
                    rootElement.append(autoSaveBar);
                }
                autoSaveInput.val(data.selection);
                fileDialogErrorMessageContent.html("");
                fileDialogErrorMessage.hide("fast");
                animateShow(autoSaveBar);
            };

            fh.open = function(data, uuid) {
            	closeAfterErrorTimeout = false;
                if (uploadBar.closest(rootElement).length === 0) {
                    rootElement.append(uploadBar);
                }
                fileDialogTransferBarClientId.val(uuid);

                appendOrDetach(downloadSelectedButton, fileActionButtonGroup, data.allowDownload);
                appendOrDetach(uploadBtn, fileActionButtonGroup, data.allowUpload);
                appendOrDetach(deleteSelectedButton, fileActionButtonGroup, data.allowDelete);

                showOrHide(dropZone, data.allowUpload);
                showOrHide(cancelBtn, (data.allowDownload || data.allowUpload || data.allowDelete) && !data.customDialog );
                getFileInput().prop("multiple", data.isMultiSelection);
                getFileInput().attr("accept", data.filter);
                setProgressBarVisible(false);
                
                fileDialogSuccessMessageContent.html("");
            	fileDialogSuccessMessage.hide("fast");
                
                showOrHide(uploadBar, data.allowDownload || data.allowUpload || data.allowDelete);
            };

            fh.close = function() {
                if (uploadBar != null && uploadBar.closest(rootElement).length !== 0) {
                    if (!errorTimeout) {
                    	uploadBar.hide("fast", function() {
                    		api.focusDefault();
                    	});
                    	uploadBar.detach();
                    } else {
                    	closeAfterErrorTimeout = true;
                    }
                }
                if (autoUploadBar != null && autoUploadBar.closest(rootElement).length !== 0) {
                	autoUploadBar.hide("fast", function() {
                		api.focusDefault();
                	});
                	autoUploadBar.detach();
                }
                if (autoSaveBar != null && autoSaveBar.closest(rootElement).length !== 0) {
                	autoSaveBar.hide("fast", function() {
                		api.focusDefault();
                	});
                	autoSaveBar.detach();
                }
            };
            
            fh.download = function(url) {
            	if (util.isIOS()) {
            		var msg = {
                		"title": "${files.downloadLink.download.title}",
            			"message": "${files.downloadLink.download.message}",
            			"buttonMessage": "${files.downloadLink.download.button}"
            		};
            		displayDownloadLinkDialog(msg, url);
            	} else {
            		var hiddenIFrameID = 'hiddenDownloader' + url, iframe = doc[0].getElementById(hiddenIFrameID);
            		if (iframe != null) {
            			iframe.parentNode.removeChild(iframe);
            		}
            		iframe = doc[0].createElement('iframe');
            		iframe.id = hiddenIFrameID;
            		iframe.style.display = 'none';
            		doc[0].body.appendChild(iframe);
            		iframe.src = api.cfg.connectionUrl + url;
            	}
            }

            fh.link = function(url) {
            	if (util.isIOS()) {
            		var msg = {
                		"title": "${files.downloadLink.link.title}",
            			"message": "${files.downloadLink.link.message}",
            			"buttonMessage": "${files.downloadLink.link.button}"
            		};
            		displayDownloadLinkDialog(msg, url);
            	} else {
            		window.open(url, '_blank');
            	}
            };

            fh.print = function(url) {
            	var finalUrl = api.cfg.connectionUrl + 'print/viewer.html?file=' + encodeURIComponent(api.cfg.connectionUrl + url);

            	if (util.isIOS()) {
            		var msg = {
            			"title": "${files.downloadLink.print.title}",
            			"message": "${files.downloadLink.print.message}",
            			"buttonMessage": "${files.downloadLink.print.button}"
            		};
            		displayDownloadLinkDialog(msg, finalUrl);
            	} else {
            		window.open(finalUrl, '_blank');
            	}
            };
        }

        function animateShow(element) {
            element.show('fast', function() {
            	var initFocus = element.find(".init-focus");
            	if (initFocus.length) {
            		initFocus[0].focus();
            	}
            });
        }

        function animateHide(element) {
            element.hide('fast', function() {
            	api.focusDefault();
            });
        }

        function showOrHide(element, bool) {
            if (bool) {
                animateShow(element);
            } else {
                animateHide(element);
            }
        }

        function appendOrDetach(element, parent, bool) {
            element.detach();
            if (bool) {
                parent.append(element);
            }
        }

        function filesSelected(files) {
            api.send({
                selected: {
                    files: files
                }
            });
        }

        function sendMessageEvent(message) {
            api.send({
                events: [{
                    event: {
                        type: message
                    }
                }]
            });
        }

    }
