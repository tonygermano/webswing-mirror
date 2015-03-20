define([ 'jquery' ], function($) {
	"use strict";
	var api;
	var canvas;
	var resizeCheck;

	function create() {
		if (canvas == null) {
			api.rootElement
					.append('<canvas data-id="canvas" style="display:block" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
			canvas = api.rootElement.find('canvas[data-id="canvas"]');
		}
		if (resizeCheck == null) {
			resizeCheck = setInterval(function() {
				if (!api.mirror && (canvas.width() !== width() || canvas.height() !== height())) {
					var snapshot = get().getContext("2d").getImageData(0, 0, get().width, get().height);
					get().width = width();
					get().height = height();
					get().getContext("2d").putImageData(snapshot, 0, 0);
					api.base.handshake();
				}
			}, 500);
		}
	}

	function dispose() {
		if (canvas != null) {
			canvas.remove();
			canvas = null;
		}
		if (resizeCheck != null) {
			clearInterval(resizeCheck);
			resizeCheck = null;
		}
	}

	function width() {
		return api.rootElement.width();
	}

	function height() {
		return api.rootElement.height();
	}

	function get() {
		if (canvas == null || resizeCheck != null) {
			create();
		}
		return canvas[0];
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.canvas = {
				dispose : dispose,
				get : get
			};
		}
	};
});