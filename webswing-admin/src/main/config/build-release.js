({
    baseUrl: "${basedir}/src/main/webapp/admin/app",
    dir: "${project.build.directory}/require-build/admin/app",
    map: {
        '*': {
            'text': 'libs/text'
        }
    },
    modules: [{
            name: "admin",
            include: ['main', 'text', 'app.module'],
            create: true
        }],
    wrap: {
        start: "(function(require,define,requirejs) {",
        end: "}(adminConsole.require,adminConsole.define,adminConsole.requirejs));"
    }
})