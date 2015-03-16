define([ 'jquery' ], function($) {
	"use strict";
	var api;
	var canvas;
	var resizeCheck;

	function create() {
		if (canvas != null || resizeCheck != null) {
			dispose();
		}
		api.rootElement.append('<canvas data-id="canvas" style="display:block" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
		canvas = api.rootElement.find('canvas[data-id="canvas"]');
		resizeCheck = setInterval(function() {
			if (canvas.width() !== width() || canvas.height() !== height()) {
				get().width = width();
				get().height = height();
				api.base.handshake();
			}
		}, 500);
	}

	function dispose() {
		if (canvas != null) {
			canvas.remove();
		}
		if (resizeCheck != null) {
			clearInterval(resizeCheck);
		}
		canvas = null;
	}

	function width() {
		return api.rootElement.width();
	}

	function height() {
		return api.rootElement.height();
	}

	function get() {
		if (canvas == null) {
			create();
		}
		return canvas[0];
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.canvas = {
				create : create,
				dispose : dispose,
				get : get
			};
		}
	};
});