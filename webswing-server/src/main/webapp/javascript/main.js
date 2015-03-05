(function(root) {

	var typedArraysSupported=true;

	require.config({
		baseUrl : 'javascript',
		shim : {
			"bootstrap" : [ 'jquery' ]
		}
	});

	if (isCanvasSupported()) {
		polyfill(function() {
			startWebswing();
		});
	} else {
		window.location = '/notSupportedBrowser.html';
	}

	function polyfill(doneCallback) {
		if (!isPromisesSupported() || !isArrayBufferSupported()) {
			if(!isArrayBufferSupported()){
				typedArraysSupported=false;
			}
			require([ 'es6promise', 'typedarray' ], function(es6promise) {
				es6promise.polyfill();
				doneCallback();
			});
		} else {
			doneCallback();
		}
	}

	function startWebswing() {
		require([ 'webswing' ], function(ws) {
			ws.setTypedArraysSupported(typedArraysSupported);
		});
	}

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

})(this);
