(function (define) {
    define([
        'services/errorHandler.service',
        'services/sessionsRest.service',
        'services/settingsRest.service',
        'services/configRest.service',
        'services/usersRest.service'
    ], function f(errorHandlerService, sessionsRestService, settingsRestService, configRestService, usersRestService) {
        var module = angular.module('wsServices', []);
        module.service('errorHandler', errorHandlerService);
        module.service('sessionsRestService', sessionsRestService);
        module.service('settingsRestService', settingsRestService);
        module.service('configRestService', configRestService);
        module.service('usersRestService', usersRestService);

    });
})(adminConsole.define);