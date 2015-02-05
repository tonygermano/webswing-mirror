({
	baseUrl : "${project.build.directory}/${project.build.finalName}/javascript",
	dir : "${project.build.directory}/require-build/javascript/",
	optimize : "none",
	modules : [ {
		name : "main",
		exclude : [ 'webswing', 'es6promise', 'typedarray' ]
	} ]
})