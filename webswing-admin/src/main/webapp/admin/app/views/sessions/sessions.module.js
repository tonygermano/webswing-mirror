(function (define) {
    define(['views/sessions/sessions.controller'], function f(SessionsController) {
        var module = angular.module('wsSessions', []);

        module.controller('SessionsController', SessionsController);

        module.run(['navigationService', function (navigationService) {
            navigationService.addLocation('Sessions', '#/sessions');
        }]);

        module.config(['$routeProvider', function ($routeProvider) {
            $routeProvider.when('/sessions', {
                controller: 'SessionsController',
                controllerAs: 'vm',
                templateUrl: 'app/views/sessions/sessions.template.html'
            });
        }]);

    });
})(adminConsole.define);