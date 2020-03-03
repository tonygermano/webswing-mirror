import Translate from "./webswing-translate";

export default function Util(i18n){

    var translateModule = new Translate(i18n.msg, i18n.langs);
    translateModule.ready();
    var translate = translateModule.provides.translate;

    return {
        webswingLogin: webswingLogin,
        webswingLogout: webswingLogout,
        isTouchDevice: isTouchDevice,
        isIOS: isIOS,
        getImageString: getImageString,
        bindEvent: bindEvent,
        detectIE: detectIE,
        detectFF: detectFF,
        detectChrome: detectChrome,
        preventGhosts: preventGhosts,
        GUID: GUID,
        detectMac: detectMac,
        createCookie: createCookie,
        readCookie: readCookie,
        eraseCookie: eraseCookie,
        checkCookie: checkCookie,
        dpr: Math.ceil(window.devicePixelRatio) || 1,
        getTimeZone: getTimeZone,
        focusWithPreventScroll:focusWithPreventScroll
    }

    function webswingLogin(baseUrl, element, loginData, successCallback) {
        $.ajax({
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            xhrFields: {
                withCredentials: true
            },
            type: 'POST',
            url: baseUrl + 'login',
            contentType: typeof loginData === 'object' ? 'application/json' : 'application/x-www-form-urlencoded; charset=UTF-8',
            data: typeof loginData === 'object' ? JSON.stringify(loginData) : loginData,
            success: function (data, textStatus, request) {
                if (successCallback != null) {
                    successCallback(data, request);
                }
            },
            error: function (xhr) {
                var response = xhr.responseText;
                if (response != null) {
                    var loginMsg = {};
                    try {
                        loginMsg = JSON.parse(response);
                    } catch (error) {
                        loginMsg.partialHtml = translate('<p>${login.failedToAuthenticate}</p>');
                    }
                    if (loginMsg.redirectUrl != null) {
                        window.top.location.href = loginMsg.redirectUrl;
                    } else if (loginMsg.partialHtml != null) {
                        if (typeof element === 'function') {
                            element = element();
                        }
                        element.html(translate(loginMsg.partialHtml));
                        var form = element.find('form').first();
                        form.submit(function (event) {
                            element.find('#progress').show();
                            webswingLogin(baseUrl, element, form.serialize(), successCallback);
                            event.preventDefault();
                        });
                    } else {
                        element.html(translate("<p>${login.unexpectedError}</p>"));
                    }
                } else {
                    element.html(translate("<p>${login.serverNotAvailable}</p>"));
                }
            }
        });

    }

    function webswingLogout(baseUrl, element, doneCallback) {
        $.ajax({
            type: 'GET',
            url: baseUrl + 'logout',
            xhrFields: {
                withCredentials: true
            },
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        }).done(function (data, status, xhr) {
            var response = xhr.responseText;
            if (response != null) {
                var loginMsg = {};
                try {
                    loginMsg = JSON.parse(response);
                } catch (error) {
                    doneCallback();
                }
                if (loginMsg.redirectUrl != null) {
                    window.top.location.href = loginMsg.redirectUrl;
                } else if (loginMsg.partialHtml != null) {
                    if (typeof element === 'function') {
                        element = element();
                    }
                    element.html(translate(loginMsg.partialHtml));
                } else {
                    doneCallback();
                }
            } else {
                element.html(translate("<p>${login.serverNotAvailable}</p>"));
            }
        });
    }

    function isTouchDevice() {
        // this check below works for touch devices AND notebooks with touch screen
    //    	var result = false;
    //    	if (window.PointerEvent && ('maxTouchPoints' in navigator)) {
    //    		// if Pointer Events are supported, just check maxTouchPoints
    //    		if (navigator.maxTouchPoints > 0) {
    //    			result = true;
    //    		}
    //    	} else {
    //    		// no Pointer Events...
    //    		if (window.matchMedia && window.matchMedia("(any-pointer:coarse)").matches) {
    //    			// check for any-pointer:coarse which mostly means touchscreen
    //    			result = true;
    //    		} else if (window.TouchEvent || ('ontouchstart' in window)) {
    //    			// last resort - check for exposed touch events API / event handler
    //    			result = true;
    //    		}
    //    	}
    //    	return result;

        // let's consider only mobile device as a touch device
        return 'ontouchstart' in window;
    }

    function isIOS() {
        return /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
    }

    function getImageString(data) {
        if (typeof data === 'object') {
            var binary = '';
            var bytes = data;
            for (var i = 0, l = bytes.byteLength; i < l; i++) {
                binary += String.fromCharCode(bytes[i]);
            }
            data = window.btoa(binary);
        }
        return 'data:image/png;base64,' + data;
    }

    function bindEvent(el, eventName, eventHandler) {
        if (el.addEventListener != null) {
            el.addEventListener(eventName, eventHandler);
        }
    }

    function detectChrome() {
        return /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
    }

    function detectIE() {
        var ua = window.navigator.userAgent;

        var msie = ua.indexOf('MSIE ');
        if (msie > 0) {
            // IE 10 or older => return version number
            return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
        }

        var trident = ua.indexOf('Trident/');
        if (trident > 0) {
            // IE 11 => return version number
            var rv = ua.indexOf('rv:');
            return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
        }

        var edge = ua.indexOf('Edge/');
        if (edge > 0) {
            // IE 12 => return version number
            return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
        }
        // other browser
        return false;
    }

    function preventGhosts(element) {
        var ANTI_GHOST_DELAY = 2000;
        var POINTER_TYPE = {
            MOUSE: 0,
            TOUCH: 1
        };
        var latestInteractionType, latestInteractionTime;

        function handleTap(type, e) {
            // console.log('got tap ' + e.type + ' of pointer ' + type);

            var now = Date.now();

            if (type !== latestInteractionType) {

                if (now - latestInteractionTime <= ANTI_GHOST_DELAY) {
                    // console.log('!prevented!');
                    e.preventDefault();
                    e.stopPropagation();
                    e.stopImmediatePropagation();
                    return false;
                }

                latestInteractionType = type;
            }

            latestInteractionTime = now;
        }

        function attachEvents(eventList, interactionType) {
            eventList.forEach(function (eventName) {
                element[0].addEventListener(eventName, handleTap.bind(null, interactionType), true);
            });
        }

        var mouseEvents = ['mousedown', 'mouseup', 'mousemove'];
        var touchEvents = ['touchstart', 'touchend'];

        attachEvents(mouseEvents, POINTER_TYPE.MOUSE);
        attachEvents(touchEvents, POINTER_TYPE.TOUCH);
    }

    function GUID() {
        var S4 = function () {
            return Math.floor(Math.random() * 0x10000).toString(16);
        };
        return (S4() + S4() + S4());
    }

    function detectMac() {
        return navigator.platform.toUpperCase().indexOf('MAC') >= 0;
    }

    function detectFF() {
        return typeof InstallTrigger !== 'undefined';
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
        for (var i = 0; i < ca.length; i++) {
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

    function checkCookie(){
        // Quick test if browser has cookieEnabled host property
        if (navigator.cookieEnabled) return true;
        // Create cookie
        document.cookie = "cookietest=1";
        var ret = document.cookie.indexOf("cookietest=") != -1;
        // Delete cookie
        document.cookie = "cookietest=1; expires=Thu, 01-Jan-1970 00:00:01 GMT";
        return ret;
    }

    function getTimeZone() {
        var timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

        if (timeZone) {
            return timeZone;
        }

        // IE, get offset in minutes
        return getTimeZoneFromOffset(new Date().getTimezoneOffset());
    }

    function getTimeZoneFromOffset(offset) {
        return "GMT" + ((offset < 0 ? '+' : '-') + // Note the reversed sign!
                pad(parseInt(Math.abs(offset / 60)), 2) +
                pad(Math.abs(offset % 60), 2));
    }

    function pad(number, length) {
        var str = "" + number;
        while (str.length < length) {
            str = '0' + str;
        }
        return str;
    }

    function focusWithPreventScroll(focusElement, selectInput){
        if(focusElement){
            var temppos = focusElement.style.position;
            var templeft = focusElement.style.left;
            var temptop = focusElement.style.top;
            if(detectIE() && detectIE() <= 11){// prevent scroll does not work in IE
                focusElement.style.position = 'fixed';
                focusElement.style.left = '0px';
                focusElement.style.top = '0px'
            }
            focusElement.focus({preventScroll: true});
            if(selectInput){
                focusElement.select();
            }
            if(detectIE() && detectIE() <= 11) {
                focusElement.style.position = temppos;
                focusElement.style.left = templeft;
                focusElement.style.top = temptop;
            }
        }
    }
}