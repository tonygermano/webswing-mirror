(function (define) {
    define([], function f() {
        function settingsRestService(baseUrl, $http, errorHandler, messageService) {
            return {
                getSettings: getSettings
            };

            function getSettings() {
                return $http.get(baseUrl + '/rest/settings').then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load server settings', data, true);
                }
            }
        }

        settingsRestService.$inject = ['baseUrl', '$http', 'errorHandler', 'messageService'];
        return settingsRestService;
    });
})(adminConsole.define);