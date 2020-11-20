import { Translations } from "./webswing-translate";

interface ILoginData {
    securityToken?: string,
    realm?: string,
    successUrl?: string
}

interface ILoginResponse {
    partialHtml?: string
    redirectUrl?: string
}

let token: string | null;

export function Util(translations: Translations) {

    const translate = (key: string) => translations.translate(key);

    return {
        webswingLogin,
        webswingLogout,
        refreshLogin,
        preventGhosts,
        getToken,
        clearToken
    }

    function webswingLogin(baseUrl: string, element: (() => JQuery<HTMLElement>) | JQuery<HTMLElement>, loginData: ILoginData | string, successCallback: (data: any, request: JQuery.jqXHR) => void) {
        if (token == null) {
        	// try refresh token first
        	refreshLogin(baseUrl, () => {
                // continue with login, doesn't matter if we got the token or not, or if there was an error
                doWebswingLogin(baseUrl, element, loginData, successCallback);
            });
        } else {
        	doWebswingLogin(baseUrl, element, loginData, successCallback);
        }
    }
    
    function doWebswingLogin(baseUrl: string, element: (() => JQuery<HTMLElement>) | JQuery<HTMLElement>, loginData: ILoginData | string, successCallback: (data: any, request: JQuery.jqXHR) => void) {
    	$.ajax({
        	beforeSend: (xhr) => {
                xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
                if (token != null && token.length) {
                	xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            },
            xhrFields: {
                withCredentials: true
            },
            type: 'POST',
            url: baseUrl + 'login',
            contentType: typeof loginData === 'object' ? 'application/json' : 'application/x-www-form-urlencoded; charset=UTF-8',
            data: typeof loginData === 'object' ? JSON.stringify(loginData) : loginData,
            success: (data, _, request) => {
            	if (request.responseText) {
                    saveTokens(request.responseText);
            	}
                if (successCallback != null) {
                    successCallback(data, request);
                }
            },
            error: (xhr) => {
                const response = xhr.responseText;
                let elementResolved: JQuery<HTMLElement>;
                if (typeof element === 'function') {
                    elementResolved = element();
                } else {
                    elementResolved = element;
                }
                if (response != null) {
                    let loginMsg: ILoginResponse = {};
                    try {
                        loginMsg = JSON.parse(response);
                    } catch (error) {
                        loginMsg.partialHtml = translate('<p>${login.failedToAuthenticate}</p>');
                    }
                    if (loginMsg.redirectUrl != null) {
                        window.top.location.href = loginMsg.redirectUrl;
                    } else if (loginMsg.partialHtml != null) {
                        elementResolved.html(translate(loginMsg.partialHtml));
                        const form = elementResolved.find('form').first();
                        form.submit((event) => {
                            elementResolved.find('#progress').show();
                            webswingLogin(baseUrl, elementResolved, form.serialize(), successCallback);
                            event.preventDefault();
                        });
                    } else {
                        elementResolved.html(translate("<p>${login.unexpectedError}</p>"));
                    }
                } else {
                    elementResolved.html(translate("<p>${login.serverNotAvailable}</p>"));
                }
            }
        });
    }

    function webswingLogout(baseUrl: string, element: (() => JQuery<HTMLElement>) | JQuery<HTMLElement>, doneCallback: () => void, tabLogout?: boolean) {
    	const oldToken = token;
        clearToken();
        
        $.ajax({
            type: 'GET',
            url: baseUrl + 'logout',
            beforeSend: (xhr) => {
                xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
                if (oldToken != null && oldToken.length) {
                	xhr.setRequestHeader('Authorization', 'Bearer ' + oldToken);
                }
            },
            xhrFields: {
                withCredentials: true
            }
        }).done((_, _1, xhr) => {
            if (!tabLogout) {
                localStorage.setItem("webswingLogout", Date.now().toString());
            }

            const response = xhr.responseText;
            let elementResolved: JQuery<HTMLElement>;
            if (typeof element === 'function') {
                elementResolved = element();
            } else {
                elementResolved = element;
            }
            if (response != null) {
                let loginMsg: ILoginResponse = {};
                try {
                    loginMsg = JSON.parse(response);
                } catch (error) {
                    doneCallback();
                }
                if (loginMsg.redirectUrl != null) {
                    window.top.location.href = loginMsg.redirectUrl;
                } else if (loginMsg.partialHtml != null) {
                    elementResolved.html(translate(loginMsg.partialHtml));
                } else {
                    doneCallback();
                }
            } else {
                elementResolved.html(translate("<p>${login.serverNotAvailable}</p>"));
            }
        });
    }

    function refreshLogin(baseUrl: string, doneCallback?: (success: boolean) => void) {
        $.ajax({
            beforeSend: (xhr) => {
                xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            },
            xhrFields: {
                withCredentials: true
            },
            type: 'POST',
            url: baseUrl + "rest/refreshToken",
            complete: (request, textStatus) => {
            	let success = false;
                if (textStatus === "success" && request.responseText) {
                	saveTokens(request.responseText);
                	success = true;
                }

                if (doneCallback) {
                    doneCallback(success);
                }
            }
        });
    }

    function preventGhosts(element: JQuery<HTMLElement>) {
        const ANTI_GHOST_DELAY = 2000;
        const POINTER_TYPE = {
            MOUSE: 0,
            TOUCH: 1
        };
        let latestInteractionType: number
        let latestInteractionTime: number

        function handleTap(type: number, e: Event) {
            // console.log('got tap ' + e.type + ' of pointer ' + type);

            const now = Date.now();

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
            return false;
        }

        function attachEvents(eventList: string[], interactionType: number) {
            eventList.forEach((eventName) => {
                element[0].addEventListener(eventName, handleTap.bind(null, interactionType), true);
            });
        }

        const mouseEvents = ['mousedown', 'mouseup', 'mousemove'];
        const touchEvents = ['touchstart', 'touchend'];

        attachEvents(mouseEvents, POINTER_TYPE.MOUSE);
        attachEvents(touchEvents, POINTER_TYPE.TOUCH);
    }

    function saveTokens(response: string) {
        const result = JSON.parse(response);
        token = result.accessToken;
    }

    function clearToken() {
        token = null;
    }
    
}

export function getToken() {
    return token || "";
}

export function getParam(name: string) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    const results = new RegExp("[\\?&]" + name + "=([^&#]*)").exec(location.href);
    return results == null ? undefined : decodeURIComponent(results[1]);
}

export function isArrayBufferSupported() {
    if ('ArrayBuffer' in window && ArrayBuffer.toString().indexOf("[native code]") !== -1) {
        return true;
    }
    return false;
}

export function detectIE() {
    const ua = window.navigator.userAgent;

    const msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    const trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        const rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    const edge = ua.indexOf('Edge/');
    if (edge > 0) {
        // IE 12 => return version number
        return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }
    // other browser
    return false;
}

export function detectChrome() {
    return /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
}

export function detectMac() {
    return navigator.platform.toUpperCase().indexOf('MAC') >= 0;
}


export function checkCookie() {
    // Quick test if browser has cookieEnabled host property
    if (navigator.cookieEnabled) {
        return true;
    }
    // Create cookie
    document.cookie = "cookietest=1";
    const ret = document.cookie.indexOf("cookietest=") !== -1;
    // Delete cookie
    document.cookie = "cookietest=1; expires=Thu, 01-Jan-1970 00:00:01 GMT";
    return ret;
}

export function getImageString(bytes: Uint8Array) {
    let data;
    if (typeof bytes === 'string') {
        data = bytes;
    } else if (typeof bytes === 'object') {
        let binary = '';
        for (let i = 0, l = bytes.byteLength; i < l; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        data = window.btoa(binary);
    }
    return 'data:image/png;base64,' + data;
}


export function isIOS() {
    const platforms = [
        'iPad Simulator',
        'iPhone Simulator',
        'iPod Simulator',
        'iPad',
        'iPhone',
        'iPod'
    ];
    return (platforms.indexOf(navigator.platform) !== -1)
        // iPad on iOS 13 detection
        || (navigator.userAgent.indexOf("Mac") !== -1 && "ontouchend" in document)
}

export function GUID() {
    const S4 = () => {
        return Math.floor(Math.random() * 0x10000).toString(16);
    };
    return (S4() + S4() + S4());
}

export function detectFF() {
    return navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
}

export function createCookie(name: string, value: string, days: number) {
    let expires;

    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    } else {
        expires = "";
    }
    document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
}

export function readCookie(name: string) {
    const nameEQ = escape(name) + "=";
    const ca = document.cookie.split(';');
    for (let c of ca) {
        while (c.charAt(0) === ' ') {
            c = c.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) === 0) {
            return unescape(c.substring(nameEQ.length, c.length));
        }
    }
    return null;
}

export function eraseCookie(name: string) {
    createCookie(name, "", -1);
}

export function getDpr() {
    return Math.ceil(window.devicePixelRatio) || 1;
}


export function isTouchDevice() {
    return 'ontouchstart' in window;
}

export function getTimeZone() {
    const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    if (timeZone) {
        return timeZone;
    }

    // IE, get offset in minutes
    return getTimeZoneFromOffset(new Date().getTimezoneOffset());
}

function getTimeZoneFromOffset(offset: number) {
    return "GMT" + ((offset < 0 ? '+' : '-') + // Note the reversed sign!
        pad(Math.abs(offset / 60), 2) +
        pad(Math.abs(offset % 60), 2));
}

function pad(value: number, length: number) {
    let str = "" + value;
    while (str.length < length) {
        str = '0' + str;
    }
    return str;
}
