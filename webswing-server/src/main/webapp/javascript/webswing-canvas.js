define([ 'jquery' ], function($) {
	"use strict";
	var api;
	var canvas;
	var resizeCheck;

	function create() {
		if (canvas != null || resizeCheck != null) {
			dispose();
		}
		api.rootElement.append('<canvas data-id="canvas" width="' + width() + '" height="' + height() + '" tabindex="-1"/>');
		canvas = api.rootElement.find('canvas[data-id="canvas"]');
		resizeCheck = setInterval(function() {
			if (canvas.width !== width() || canvas.height !== height()) {
				canvas.width = width();
				canvas.height = height();
				api.ws.handshake();
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
	}

	function width() {
		return api.rootElement.offsetWidth;
	}

	function height() {
		return api.rootElement.offsetHeight;
	}

	return {
		init : function(wsApi) {
			api = wsApi;
			wsApi.canvas = {
				create : create,
				dispose : dispose,
				canvas : canvas
			};
		}
	};
});