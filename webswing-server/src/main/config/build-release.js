({
	baseUrl : "${project.build.directory}/${project.build.finalName}/javascript",
	dir : "${project.build.directory}/require-build/javascript/",
	removeCombined : true,
	modules : [ {
		name : "main",
		exclude : [ "webswing", "es6promise", "typedarray" ]
	}, {
		name : "webswing",
		exclude : [ "webswing-base", "webswing-dd" ]
	} ]
})