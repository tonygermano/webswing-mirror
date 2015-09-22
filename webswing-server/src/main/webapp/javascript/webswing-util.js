define([ 'jquery' ], function Util($) {
    "use strict";

    return {
        isTouchDevice : isTouchDevice,
        getImageString : getImageString,
        bindEvent : bindEvent,
        detectIE : detectIE,
        preventGhosts : preventGhosts
    }

    function isTouchDevice() {
        return !!('ontouchstart' in window);
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

    function bindEvent(el, eventName, eventHandler) {
        if (el.addEventListener != null) {
            el.addEventListener(eventName, eventHandler);
        }
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
            MOUSE : 0,
            TOUCH : 1
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
            eventList.forEach(function(eventName) {
                element[0].addEventListener(eventName, handleTap.bind(null, interactionType), true);
            });
        }

        var mouseEvents = [ 'mousedown', 'mouseup', 'mousemove' ];
        var touchEvents = [ 'touchstart', 'touchend' ];

        attachEvents(mouseEvents, POINTER_TYPE.MOUSE);
        attachEvents(touchEvents, POINTER_TYPE.TOUCH);
    }

});
