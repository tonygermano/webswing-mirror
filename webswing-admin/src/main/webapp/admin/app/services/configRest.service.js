(function (define) {
    define([], function f() {
        function configRestService(baseUrl, $http, errorHandler, messageService) {
            return {
                getConfig: getConfig,
                setConfig: setConfig,
                getVariables: getVariables,
                getDefault: getDefault
            };

            function getConfig() {
                return $http.get(baseUrl + '/rest/config').then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load server configuration', data, true);
                }
            }
            function setConfig(config) {
                return $http.post(baseUrl + '/rest/config', config).then(success, failed);
                function success() {
                    messageService.success('Configuration saved.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('save server configuration', data);
                }
            }
            function getVariables() {
                return $http.get(baseUrl + '/rest/config/variables', {cache: true}).then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load available configuration variables', data, true);
                }
            }
            function getDefault(type) {
                return $http.get(baseUrl + '/rest/config/default/' + type, {cache: true}).then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load default application settings', data, true);
                }
            }
        }

        configRestService.$inject = ['baseUrl', '$http', 'errorHandler', 'messageService'];
        return configRestService;
    });
})(adminConsole.define);