(function (define) {
    define([], function f() {
        function usersRestService(baseUrl, $http, errorHandler, messageService) {
            return {
                getUsers: getUsers,
                setUsers: setUsers
            };

            function getUsers() {
                return $http.get(baseUrl + '/rest/admin/users').then(success, failed);
                function success(data) {
                    return data.data;
                }
                function failed(data) {
                    return errorHandler.handleRestError('load user configuration', data, true);
                }
            }
            function setUsers(users) {
                return $http.post(baseUrl + '/rest/admin/users', users).then(success, failed);
                function success() {
                    messageService.success('User properties saved.');
                }
                function failed(data) {
                    return errorHandler.handleRestError('save user configuration', data, true);
                }
            }
        }

        usersRestService.$inject = ['baseUrl', '$http', 'errorHandler', 'messageService'];
        return usersRestService;
    });
})(adminConsole.define);