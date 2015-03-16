({
	baseUrl : "${project.build.directory}/${project.build.finalName}/javascript",
	dir : "${project.build.directory}/require-build/javascript/",
	paths : {
		requireLib : 'require'
	},
	namespace : 'webswing',
	modules : [ {
		name : "webswing-embed",
		include : [ 'requireLib', 'main','jquery-private' ],
		create : true
	} ]
})