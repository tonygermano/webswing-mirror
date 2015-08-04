define(['jquery', 'text!templates/clipboard.html', 'text!templates/clipboard.css'], function amdFactory($, html, css) {
    "use strict";
    var style = $("<style></style>", {
        type: "text/css"
    });
    style.text(css);
    $("head").prepend(style);

    return function ClipboardModule() {
        var module = this;
        var api;
        module.injects = api = {
            cfg: 'webswing.config',
            send: 'socket.send'
        };
        module.provides = {
            copy: copy,
            paste: paste
        };

        var copyBar;

        function copy(data) {
            if (copyBar != null) {
                close();
            }
            api.cfg.rootElement.append(html);
            copyBar = api.cfg.rootElement.find('div[data-id="copyBar"]');
            var closeBtn = copyBar.find('button[data-id="closeBtn"]');
            closeBtn.click(function () {
                close();
            });
            copyBar.show("fast");
            var input = copyBar.find('input[data-id="input"]');
            var textarea = copyBar.find('textarea[data-id="textarea"]');
            var currentFocus = document.activeElement;
            textarea.val(data.content);
            focusInput(input);

            if (api.cfg.ieVersion) {
                // handling of copy events only for IE
                var ieClipboardDiv = copyBar.find('div[data-id="ie-clipboard"]');
                var clipboardData = window.clipboardData;
                var focusIeClipboardDiv = function () {
                    ieClipboardDiv.focus();
                    var range = document.createRange();
                    range.selectNodeContents((ieClipboardDiv.get(0)));
                    var selection = window.getSelection();
                    selection.removeAllRanges();
                    selection.addRange(range);
                };
                input.on('copy', function (event) {
                    if (data.htmlContent != null) {
                        ieClipboardDiv.html(data.htmlContent);
                        focusIeClipboardDiv();
                        setTimeout(function () {
                            focusInput(currentFocus);
                            close();
                        }, 0);
                    } else {
                        event.preventDefault();
                        clipboardData.setData('Text', data.content);
                        focusInput(currentFocus);
                        close();
                    }
                });
                input.on('blur', function (event) {
                    focusInput(currentFocus);
                    close();
                });
            } else {
                // handling of copy events for rest of browsers
                input.on('copy', function (event) {
                    event = event.originalEvent;
                    event.preventDefault();
                    if (data.content != null) {
                        event.clipboardData.setData('text/plain', data.content);
                    }
                    if (data.htmlContent != null) {
                        event.clipboardData.setData('text/html', data.htmlContent);
                    }
                    focusInput(currentFocus);
                    close();
                });
                input.on('blur', function (event) {
                    focusInput(currentFocus);
                    close();
                });
            }
        }

        function paste(data) {
            var text = '';
            if (api.cfg.ieVersion) {
                text = window.clipboardData.getData('Text');
            } else {
                text = data.getData('text/plain');
            }

            if (api.cfg.hasControl) {
                api.send({
                    paste: {
                        content: text
                    }
                });
            }
        }

        function close() {
            if (copyBar != null) {
                copyBar.hide("fast");
                copyBar.remove();
                copyBar = null;
            }
        }

        function focusInput(input) {
            input.focus();
            input.select();
        }

    };
});