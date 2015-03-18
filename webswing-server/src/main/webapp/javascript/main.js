(function(root) {
	require.config({
		baseUrl : 'javascript',
		map : {
			'*' : {
				'jquery' : 'jquery-private'
			},
			'jquery-private' : {
				'jquery' : 'jquery'
			}
		},
	});

	require([ 'webswing' ], function(result) {
		for ( var exportName in result) {
			root[exportName] = result[exportName];
		}
	}, function(err) {
		var failedId = err.requireModules && err.requireModules[0];
		var elements = getAllElementsWithAttribute('data-webswing-id');
		require([ 'text!templates/notSupportedBrowser.html' ], function(html) {
			for ( var i=0; i < elements.length; i++) {
				elements[i].innerHTML = html;
			}
		});
		throw Error("Error while starting webswing in module '" + failedId + "'. Reason:" + err);
	});

	function getAllElementsWithAttribute(attribute) {
		var matchingElements = [];
		var allElements = document.getElementsByTagName('*');
		for ( var i = 0, n = allElements.length; i < n; i++) {
			if (allElements[i].getAttribute(attribute) !== null) {
				// Element exists with attribute. Add to array.
				matchingElements.push(allElements[i]);
			}
		}
		return matchingElements;
	}
})(this);
