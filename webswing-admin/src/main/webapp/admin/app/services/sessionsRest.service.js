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
                return $http.get(toPath(path) + '/rest/sessions/').then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load swing sessions', data, true);
                }
            }

            function getSession(path,id) {
                return $http.get(toPath(path) + '/rest/session/' + id).then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load swing session ' + id, data, true);
                }
            }

            function forceKillSession(id) {
                return $http.delete(toPath(path) + '/rest/session/' + id +'?force=true').then(success, failed);
                function success(data) {
                    messageService.info('Swing application process has been forcefully terminated.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('Swing application process shutdown', data, true);
                }
            }
            
            function killSession(id) {
                return $http.delete(toPath(path) + '/rest/session/' + id ).then(success, failed);
                function success(data) {
                    messageService.info('Swing application signalled to exit.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('Swing application shutdown', data, true);
                }
            }
            
			function toPath(path) {
				if (path.substr(0, 4) === 'http') {
					return path;
				}
				if (path.substr(0, 1) !== '/') {
					path = '/' + path
				}
				if (path.length === 1) {
					path = '';
				}
				return path;
			}
        }

        sessionsRestService.$inject = ['baseUrl', '$http', 'errorHandler', 'messageService'];
        return sessionsRestService;
    });
})(adminConsole.define);