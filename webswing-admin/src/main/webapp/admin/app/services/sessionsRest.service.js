(function (define) {
    define([], function f() {
        function sessionsRestService(baseUrl, $http, errorHandler, messageService) {
            return {
                getSessions: getSessions,
                getSession: getSession,
                killSession: killSession,
                forceKillSession: forceKillSession
            };

            function getSessions(path) {
                return $http.get(baseUrl + '/rest/admin/sessions/'+path).then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load swing sessions', data, true);
                }
            }

            function getSession(id) {
                return $http.get(baseUrl + '/rest/admin/session/' + id).then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load swing session ' + id, data, true);
                }
            }

            function forceKillSession(id) {
                return $http.delete(baseUrl + '/rest/admin/session/' + id +'?force=true').then(success, failed);
                function success(data) {
                    messageService.info('Swing application process has been forcefully terminated.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('Swing application process shutdown', data, true);
                }
            }
            
            function killSession(id) {
                return $http.delete(baseUrl + '/rest/admin/session/' + id ).then(success, failed);
                function success(data) {
                    messageService.info('Swing application signalled to exit.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('Swing application shutdown', data, true);
                }
            }
        }

        sessionsRestService.$inject = ['baseUrl', '$http', 'errorHandler', 'messageService'];
        return sessionsRestService;
    });
})(adminConsole.define);