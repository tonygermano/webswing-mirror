define([], function f() {
    function sessionsRestService($http, $log, messageService) {
        return {
            getSessions: getSessions,
            getSession:getSession
        };

        function getSessions() {
            return $http.get('/rest/admin/sessions').then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load swing sessions.');
                $log.error('Loading of sessions failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }

        function getSession(id) {
            return $http.get('/rest/admin/session/'+id).then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load swing session '+id+'.');
                $log.error('Loading of session '+id+' failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
    }

    sessionsRestService.$inject = ['$http', '$log', 'messageService'];
    return sessionsRestService;
});