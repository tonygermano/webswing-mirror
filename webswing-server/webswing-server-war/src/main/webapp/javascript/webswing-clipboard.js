define([ 'jquery', 'text!templates/clipboard.html', 'text!templates/clipboard.css', 'webswing-util' ], function amdFactory($, html, css, util) {
	"use strict";
	var style = $("<style></style>", {
		type : "text/css"
	});
	style.text(css);
	$("head").prepend(style);

	return function ClipboardModule() {
		var module = this;
		var api;
		module.injects = api = {
			cfg : 'webswing.config',
			send : 'socket.send',
			getInput : 'canvas.getInput'
		};
		module.provides = {
			cut : cut,
			copy : copy,
			paste : paste,
			displayCopyBar : displayCopyBar,
			dispose : close
		};

		var copyBar;

		function cut(event) {
			copy(event, true);
		}

		function copy(event, cut) {
			if (copyBar == null || copyBar.minimized === true) {
				api.send({
					copy : {
						type : cut === true ? 'cut' : 'copy'
					}
				});
			} else {
				var data = copyBar.wsEventData;
				if (api.cfg.ieVersion) {
					// handling of copy events only for IE
					var ieClipboardDiv = copyBar.find('div[data-id="ie-clipboard"]');
					var clipboardData = window.clipboardData;
					if (data.html != null) {
						ieClipboardDiv.html(data.html);
						focusIeClipboardDiv();
						setTimeout(function() {
							close();
						}, 0);
					} else {
						event.preventDefault();
						clipboardData.setData('Text', data.text);
						close();
					}

				} else {
					// handling of copy events for rest of browsers
					event = event.originalEvent || event;
					event.preventDefault();
					if (data.text != null) {
						event.clipboardData.setData('text/plain', data.text);
					}
					if (data.html != null) {
						event.clipboardData.setData('text/html', data.html);
					}
					close();
				}
			}
		}

		function focusIeClipboardDiv() {
			ieClipboardDiv.focus();
			var range = document.createRange();
			range.selectNodeContents((ieClipboardDiv.get(0)));
			var selection = window.getSelection();
			selection.removeAllRanges();
			selection.addRange(range);
		}

		function paste(event) {
			if (api.cfg.hasControl) {
				if (useLocalClipboard()) {
					var text = '';
					var html = '';
					if (api.cfg.ieVersion) {
						text = window.clipboardData.getData('Text');
						html = text;
					} else {
						var data = event.clipboardData || event.originalEvent.clipboardData;
						text = data.getData('text/plain');
						html = data.getData('text/html');
						if (data.items != null) {
							for (var i = 0; i < data.items.length; i++) {
								if (data.items[i].type.indexOf('image') === 0) {
									var img = data.items[i];
									var reader = new FileReader();
									reader.onload = function(event) {
										sendPasteEvent(text, html, event.target.result);
									};
									reader.readAsDataURL(img.getAsFile());
									return;
								}
							}
						}
					}
					sendPasteEvent(text, html);
				} else {
					sendPasteEvent();
				}
			}
		}

		function sendPasteEvent(text, html, img) {
			var pasteObj = {};
			if (text != null) {
				pasteObj.text = text;
			}
			if (html != null) {
				pasteObj.html = html;
			}
			if (img != null) {
				pasteObj.img = img;
			}
			api.send({
				paste : pasteObj
			});
		}

		function displayCopyBar(data) { // trigered by swing app
			var onlyOtherData = false;
			if (copyBar != null) {
				close();
			}
			if (data.html == null && data.text == null && data.img == null && (data.files == null || data.files.length === 0)) {
				if (!data.other) {
					return;
				} else {
					onlyOtherData = true;
				}
			}
			api.cfg.rootElement.append(html);
			copyBar = api.cfg.rootElement.find('div[data-id="copyBar"]');
			copyBar.on('click', function(event) {
				clearTimeout(minimizer);
				api.getInput().focus();
			});
			copyBar.wsEventData = data;
			copyBar.minimized = false;
			var closeBtn = copyBar.find('button[data-id="closeBtn"]');
			closeBtn.click(function() {
				close();
			});
			copyBar.show("fast");
			/* OTHER TAB */
			if (data.other) {
				var otherTab = copyBar.find('button[data-id="other"]');
				otherTab.removeClass("c-minimized-tab--is-inactive").addClass("c-minimized-tab--is-active");
				otherTab.on('click', function() {
					showContent(otherTab, 'other');
				});
				showContent(otherTab, 'other');
			}
			/* FILES TAB */
			if (data.files != null && data.files.length !== 0) {
				var fileListElement = copyBar.find('#wsFileList');
				for (var i = 0; i < data.files.length; i++) {
					var fileName = data.files[i];
					var link = $('<a>');
					if (fileName.indexOf("#") === 0) {
						link = $('<span>');
						link.html(data.files[i].substring(1));
					} else {
						link.html(data.files[i]);
						link.on('click', function(event) {
							api.send({
								copy : {
									type : 'getFileFromClipboard',
									file : $(event.currentTarget).html()
								}
							});
						});
					}
					fileListElement.append(link);
					fileListElement.append("<br/>");
				}
				var filesTab = copyBar.find('button[data-id="files"]');
				filesTab.removeClass("c-minimized-tab--is-inactive").addClass("c-minimized-tab--is-active");
				filesTab.on('click', function() {
					showContent(filesTab, 'files');
				});
				showContent(filesTab, 'files');
			}
			/* IMAGE TAB */
			if (data.img != null) {
				copyBar.find('#image>div').append('<img src="' + util.getImageString(data.img) + '" id="wsCopyImage" class="c-tab-content__img-thumb">');
				var imageTab = copyBar.find('button[data-id="image"]');
				imageTab.removeClass("c-minimized-tab--is-inactive").addClass("c-minimized-tab--is-active")
				imageTab.on('click', function() {
					showContent(imageTab, 'image');
				});
				showContent(imageTab, 'image');
			}
			/* HTML TAB */
			if (data.html != null && data.html.length !== 0) {
				var htmlarea = copyBar.find('textarea[data-id="htmlarea"]');
				var htmlTab = copyBar.find('button[data-id="html"]');
				htmlarea.val(data.html);
				htmlTab.removeClass("c-minimized-tab--is-inactive").addClass("c-minimized-tab--is-active");
				htmlTab.on('click', function() {
					showContent(htmlTab, 'html');
				});
				showContent(htmlTab, 'html');
			}
			/* TEXT TAB */
			if (data.text != null && data.text.length !== 0) {
				var textarea = copyBar.find('textarea[data-id="textarea"]');
				var textTab = copyBar.find('button[data-id="plaintext"]');
				textarea.val(data.text);
				textTab.removeClass("c-minimized-tab--is-inactive").addClass("c-minimized-tab--is-active");
				textTab.on('click', function() {
					showContent(textTab, 'text');
				});
				showContent(textTab, 'text');
			}

			function showContent(tab, type) {
				copyBar.find('.c-minimized-tab--is-selected').removeClass('c-minimized-tab--is-selected');
				copyBar.find('.c-tab-content__item').removeClass('c-tab-content__item--is-active');
				$(tab).addClass('c-minimized-tab--is-selected');
				copyBar.find('#' + type).addClass('c-tab-content__item--is-active');
				maximize();
			}

			var minimizeBtn = copyBar.find('.c-tab-labels__minimize-button');
			minimizeBtn.on('click', function(event) {// minimize
				minimize();
			});

			if (onlyOtherData) {
				copyBar.find('div[data-id="contentBar"]').hide();
				copyBar.minimized = true;
			} else {
				var minimizer = setTimeout(function() {
					minimize();
				}, 2000);
			}
		}

		function minimize() {
			if (copyBar != null) {
				copyBar.find('.c-minimized-tab--is-selected').removeClass('c-minimized-tab--is-selected');
				copyBar.find('div[data-id="contentBar"]').slideUp('fast');
				copyBar.minimized = true;
			}
		}

		function maximize() {
			if (copyBar != null) {
				copyBar.find('div[data-id="contentBar"]').slideDown('fast');
				copyBar.minimized = false;
			}
		}

		function close() {
			if (copyBar != null) {
				copyBar.hide("fast");
				copyBar.remove();
				copyBar = null;
			}
		}

		function useLocalClipboard() {
			if (copyBar == null) {
				return true;
			}
		}

	};
});