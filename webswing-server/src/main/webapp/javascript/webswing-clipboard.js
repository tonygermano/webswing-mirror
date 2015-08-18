define([ 'jquery', 'text!templates/clipboard.html', 'text!templates/clipboard.css' ], function amdFactory($, html, css) {
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
            send : 'socket.send'
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
            copy(event, true)
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

                    function focusIeClipboardDiv() {
                        ieClipboardDiv.focus();
                        var range = document.createRange();
                        range.selectNodeContents((ieClipboardDiv.get(0)));
                        var selection = window.getSelection();
                        selection.removeAllRanges();
                        selection.addRange(range);
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
                        for ( var i = 0; i < data.items.length; i++) {
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
                    sendPasteEvent(text, html);
                } else {
                    sendPasteEvent()
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
                pasteObj.img = img
            }
            api.send({
                paste : pasteObj
            });
        }

        function displayCopyBar(data) { // trigered by swing app
            if (copyBar != null) {
                close();
            }
            if (!data.other && data.html == null && data.text == null && data.img == null && (data.files == null || data.files.length === 0)) {
                return;
            }
            api.cfg.rootElement.append(html);
            copyBar = api.cfg.rootElement.find('div[data-id="copyBar"]');
            copyBar.wsEventData = data;
            copyBar.minimized = false;
            var closeBtn = copyBar.find('button[data-id="closeBtn"]');
            closeBtn.click(function() {
                close();
            });
            copyBar.show("fast");
            /* TEXT TAB */
            if (data.text == null || data.text.length === 0) {
                copyBar.find('#text').remove();
                copyBar.find('#textTab').remove();
            } else {
                var textarea = copyBar.find('textarea[data-id="textarea"]');
                textarea.val(data.text);
                copyBar.find('span[data-id="plaintext"]').removeClass("webswing-copy-content-inactive").addClass("webswing-copy-content-active");
            }
            /* HTML TAB */
            if (data.html == null || data.html.length === 0) {
                copyBar.find('#html').remove();
                copyBar.find('#htmlTab').remove();
            } else {
                var htmlarea = copyBar.find('textarea[data-id="htmlarea"]');
                htmlarea.val(data.html);
                copyBar.find('span[data-id="html"]').removeClass("webswing-copy-content-inactive").addClass("webswing-copy-content-active");
            }
            /* IMAGE TAB */
            if (data.img == null) {
                copyBar.find('#image').remove();
                copyBar.find('#imageTab').remove();
            } else {
                copyBar.find('#image>div').append('<img src="' + getImageString(data.img) + '" id="wsCopyImage" class="img-thumbnail">');
                copyBar.find('span[data-id="image"]').removeClass("webswing-copy-content-inactive").addClass("webswing-copy-content-active");
            }
            /* FILES TAB */
            if (data.files == null || data.files.length === 0) {
                copyBar.find('#files').remove();
                copyBar.find('#filesTab').remove();
            } else {
                var fileListElement = copyBar.find('#wsFileList');
                for ( var i = 0; i < data.files.length; i++) {
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
                copyBar.find('span[data-id="files"]').removeClass("webswing-copy-content-inactive").addClass("webswing-copy-content-active");
            }
            /* OTHER TAB */
            if (!data.other) {
                copyBar.find('#other').remove();
                copyBar.find('#otherTab').remove();
            } else {
                copyBar.find('span[data-id="other"]').removeClass("webswing-copy-content-inactive").addClass("webswing-copy-content-active");
            }

            /* TAB Activation */
            var tabs = copyBar.find('.nav-tabs>li');
            tabs.first().addClass('active');
            copyBar.find('.tab-pane').first().addClass('active');
            tabs.on('click', function(event) {
                tabs.removeClass('active');
                copyBar.find('.tab-pane').removeClass('active');
                $(event.currentTarget).addClass('active');
                copyBar.find('#' + $(event.currentTarget).data('tab')).addClass('active');
            });

            var infoBar = copyBar.find('div[data-id="minimizedInfoBar"]');
            infoBar.on('click', function(event) {// maximize
                maximize();
            });

            var minimizeBtn = copyBar.find('.webswing-minimize-symbol');
            minimizeBtn.on('click', function(event) {// minimize
                minimize();
            });

            setTimeout(function() {
                minimize();
            }, 2000);

        }

        function minimize() {
            if (copyBar != null) {
                copyBar.find('div[data-id="contentBar"]').slideUp('fast');
                copyBar.find('div[data-id="minimizedInfoBar"]').fadeIn('fast');
                copyBar.minimized = true;
            }
        }

        function maximize() {
            if (copyBar != null) {
                copyBar.find('div[data-id="contentBar"]').slideDown('fast');
                copyBar.find('div[data-id="minimizedInfoBar"]').fadeOut('fast');
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

        function getImageString(data) {
            if (typeof data === 'object') {
                var binary = '';
                var bytes = new Uint8Array(data.buffer, data.offset, data.limit - data.offset);
                for ( var i = 0, l = bytes.byteLength; i < l; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }
                data = window.btoa(binary);
            }
            return 'data:image/png;base64,' + data;
        }

    };
});