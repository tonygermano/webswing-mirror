({
	baseUrl : "${project.build.directory}/${project.build.finalName}/javascript",
	dir : "${project.build.directory}/require-build/javascript/",
	optimize : "none",
	paths : {
		requireLib : 'require'
	},
	namespace : 'webswingRequirejs',
	modules : [ {
		name : "webswing-embed",
		include : [ 'requireLib', 'main', 'jquery-private'],
		create : true
	} ],
	wrap: {
		startFile: "${project.basedir}/src/main/config/parts/start.frag",
		endFile: "${project.basedir}/src/main/config/parts/end.frag"
	}
})