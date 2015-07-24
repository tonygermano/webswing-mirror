({
    baseUrl: "${basedir}/src/main/webapp/admin/app",
    dir: "${project.build.directory}/require-build/admin/app",
    optimize: 'none',
    map: {
        '*': {
            'text': 'libs/text'
        }
    },
    modules: [{
            name: "admin",
            include: ['main', 'text'],
            create: true
        }],
    wrap: {
        start: "(function(require,define,requirejs) {",
        end: "}(adminConsole.require,adminConsole.define,adminConsole.requirejs));"
    }
})