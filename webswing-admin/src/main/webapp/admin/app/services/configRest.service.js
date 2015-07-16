define([], function f() {
    function configRestService($http, $log, messageService) {
        return {
            getConfig: getConfig,
            setConfig: setConfig,
            getVariables: getVariables,
            getDefault: getDefault
        };

        function getConfig() {
            return $http.get('/rest/admin/config').then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load server configuration.');
                $log.error('Loading of server configuration failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
        function setConfig(config) {
            return $http.post('/rest/admin/config', config).then(success, failed);
            function success() {
                messageService.success('Configuration saved.');
            }
            function failed(data) {
                messageService.error('Failed to save server configuration.');
                $log.error('Saving of server configuration failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
        function getVariables() {
            return $http.get('/rest/admin/config/variables', {cache: true}).then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load available configuration variables.');
                $log.error('Loading of available configuration variables failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
        function getDefault(type) {
            return $http.get('/rest/admin/config/default/' + type, {cache: true}).then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load available configuration variables.');
                $log.error('Loading of available configuration variables failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
    }

    configRestService.$inject = ['$http', '$log', 'messageService'];
    return configRestService;
});