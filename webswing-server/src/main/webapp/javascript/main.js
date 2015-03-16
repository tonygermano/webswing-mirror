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
	});
})(this);
