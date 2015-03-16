define([ 'es6promise', 'typedarray' ], function(es6promise) {

	function isCanvasSupported() {
		var elem = document.createElement('canvas');
		return !!(elem.getContext && elem.getContext('2d'));
	}

	function isPromisesSupported() {
		if (typeof Promise !== "undefined" && Promise.toString().indexOf("[native code]") !== -1) {
			return true;
		}
		return false;
	}

	function isArrayBufferSupported() {
		if ('ArrayBuffer' in window)
			return true;
		return false;
	}

	return {
		init : function(api) {
			if (!isCanvasSupported()) {
				api.dialog.show(api.dialog.content.notSupportedBrowser);
				api.start=function(){};
			}
			if (!isPromisesSupported() || !isArrayBufferSupported()) {
				if (!isArrayBufferSupported()) {
					api.typedArraysSupported = false;
				}
				es6promise.polyfill();
			}
		}
	};
});
