import Translate from "./webswing-translate";
import {loadTranslations} from "./webswing-tools";
import $ from "jquery";

var baseUrl = (window as any).webswingRequestBaseUrl ? ((window as any).webswingRequestBaseUrl + '/') : '';
loadTranslations(baseUrl).then( function (t) {
    var translateModule = new (Translate as any)(t.msg, t.langs);
    translateModule.ready();
    var translate = translateModule.provides.translate;

    showPartial(window.location.href, $('#webswing-content'), (window as any).webswingPartialHtml);

    function showPartial(url:string, element:any, html:any) {
        element.html(translate(html));
        var form = element.find('form').first();
        if (form != null) {
            form.submit(function (event:any) {
                loadPartial(url, element, form.serialize());
                event.preventDefault();
            });
        }
    }

    function loadPartial(url:string, element:any, data:any) {
        if (typeof element === 'function') {
            element = element();
        }
        $.ajax({
            xhrFields: {
                withCredentials: true
            },
            type: 'POST',
            url: url,
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: data,
            complete: function (xhr:any) {
                var response = xhr.responseText;
                var loginMsg:any = {};
                if (response != null) {
                    try {
                        loginMsg = JSON.parse(response);
                    } catch (error) {
                        if (baseUrl) {
                            window.top.location.href = baseUrl;
                            return;
                        } else {
                            element.html(translate("<p>Invalid request</p>"))
                        }
                    }
                    if (loginMsg.redirectUrl != null) {
                        window.top.location.href = loginMsg.redirectUrl;
                    } else if (loginMsg.partialHtml != null) {
                        showPartial(url, element, loginMsg.partialHtml);
                    } else {
                        element.html(translate("<p>${login.unexpectedError}</p>"));
                    }
                } else {
                    element.html(translate("<p>${login.serverNotAvailable}</p>"));
                }
            }
        });
    }

}).catch(function (e){
    console.error("Failed to load Translations",e);
})
;