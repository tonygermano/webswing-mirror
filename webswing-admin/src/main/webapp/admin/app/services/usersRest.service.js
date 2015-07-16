define([], function f() {
    function usersRestService($http, $log, messageService) {
        return {
            getUsers: getUsers,
            setUsers: setUsers,
        };

        function getUsers() {
            return $http.get('/rest/admin/users').then(success, failed);
            function success(data) {
                return data.data;
            }
            function failed(data) {
                messageService.error('Failed to load users configuration.');
                $log.error('Loading of users configuration failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
        function setUsers(users) {
            return $http.post('/rest/admin/users', users).then(success, failed);
            function success() {
                messageService.success('Users properties saved.');
            }
            function failed(data) {
                messageService.error('Failed to save users configuration.');
                $log.error('Saving of users configuration failed with status ' + data.status + ' and message :' + data.data);
                return null;
            }
        }
    }

    usersRestService.$inject = ['$http', '$log', 'messageService'];
    return usersRestService;
});