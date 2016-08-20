(function (define) {
    define([
        'services/errorHandler.service',
        'services/sessionsRest.service',
        'services/settingsRest.service',
        'services/configRest.service',
    ], function f(errorHandlerService, sessionsRestService, settingsRestService, configRestService) {
        var module = angular.module('wsServices', []);
        module.service('errorHandler', errorHandlerService);
        module.service('sessionsRestService', sessionsRestService);
        module.service('settingsRestService', settingsRestService);
        module.service('configRestService', configRestService);

    });
})(adminConsole.define);