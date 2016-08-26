(function (define) {
    define([
        'services/errorHandler.service',
        'services/sessionsRest.service',
        'services/configRest.service',
        'services/wsUtils.service',
    ], function f(errorHandlerService, sessionsRestService, configRestService, wsUtils) {
        var module = angular.module('wsServices', []);
        module.service('errorHandler', errorHandlerService);
        module.service('sessionsRestService', sessionsRestService);
        module.service('configRestService', configRestService);
        module.service('wsUtils', wsUtils);
    });
})(adminConsole.define);