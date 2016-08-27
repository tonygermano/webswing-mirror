(function (define) {
    define([], function f() {
        function loginService(baseUrl, $rootScope, messageService, $http, $log, permissions) {

            return {
                login: login,
                logout: logout,
                requestLogin: requestLogin
            };


            function requestLogin() {

            }

            function login() {
                return new Promise(function (resolve) {
                    $rootScope.$broadcast('wsLoginRequestEvent', doLogin);
                    function doLogin(formData) {
                        return $http({
                            method: 'POST',
                            url: baseUrl + '/login?role=admin',
                            data: $.param(formData),
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            }
                        }).then(success, failed);
                        function success(data) {
                        	permissions.reload();
                            resolve(data);
                            return data;
                        }
                        function failed(data) {
                            $log.error('Authentication failed with status: ' + data.status + ' and message ' + data.data);
                            return data;
                        }
                    }
                });
            }

            function logout(simple) {
                $http({
                    method: 'GET',
                    url: baseUrl + '/logout'
                }).success(function (data, status, headers, config) {
                    if (!simple) {
                        login();
                    }
                }).error(function (data, status, headers, config) {
                    $log.error(data);
                    messageService.error("Logout failed.");
                    return status;
                });
            }
        }
        loginService.$inject = ['baseUrl', '$rootScope', 'messageService', '$http', '$log','permissions'];
        return loginService;
    });
})(adminConsole.define);