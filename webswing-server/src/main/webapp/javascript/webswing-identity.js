define([], function amdFactory() {
    "use strict";
    return function JsLinkModule() {
        var module = this;
        module.provides = {
            get: get,
            getLocale: getLocale,
            dispose: dispose
        };

        var cookieName = 'webswingID';
        function get() {
            var id = readCookie(cookieName);
            if (id != null) {
                return id;
            } else {
                id = GUID();
                createCookie(cookieName, id, 1);
                return id;
            }
        }

        function getLocale(){
            var lang = (navigator.language || navigator.browserLanguage).split('-')[0];
            return lang;
        }

        function dispose() {
            eraseCookie(cookieName);
        }

        function GUID() {
            var S4 = function () {
                return Math.floor(Math.random() * 0x10000).toString(16);
            };
            return (S4() + S4() + S4());
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
    };
});