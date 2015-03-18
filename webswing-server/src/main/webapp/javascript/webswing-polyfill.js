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
		if ('ArrayBuffer' in window && ArrayBuffer.toString().indexOf("[native code]") !== -1) {
			return true;
		}
		return false;
	}

	return {
		init : function(api) {
			if (isArrayBufferSupported()) {
				api.typedArraysSupported = true;
			}
			if (!isPromisesSupported()) {
				es6promise.polyfill();
			}
		}
	};
});
