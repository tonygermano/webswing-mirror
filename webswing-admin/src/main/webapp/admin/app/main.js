(function (root) {
    require.config({
        baseUrl: 'app',
        map: {
            '*': {
                'text': 'libs/text'
            }
        },
        shim: {
            'libs/angular': {
                exports: 'angular',
                deps: ['libs/jquery']
            },
            'libs/bootstrap': {
                deps: ['libs/jquery']
            },
            'libs/angular-route': {
                deps: ['libs/angular']
            },
            'libs/ui-ace': {
                deps: ['libs/acejs/ace', 'libs/angular']
            },
            'libs/ui-bootstrap': {
                deps: ['libs/angular']
            },
            'libs/ui-utils': {
                deps: ['libs/angular']
            }
        }
    });
    require(['libs/angular', 'libs/bootstrap', 'libs/angular-route', 'libs/ui-ace', 'libs/ui-bootstrap', 'libs/ui-utils'], function () {
        require(['app.module'], function (admin) {
            angular.bootstrap(root, ['wsAdmin']);
        }, errorHandler);
    }, errorHandler);

    function errorHandler(err) {
        var failedId = err.requireModules && err.requireModules[0];
        throw Error("Error while starting ws-admin in module '" + failedId + "'. Reason:" + err);
    }

})(document);