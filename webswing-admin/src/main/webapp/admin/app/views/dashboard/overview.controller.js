(function (define) {
    define([], function f() {
        function OverviewController($scope, $timeout, $location, sessionsRestService) {
            var vm = this;
            vm.sessions = [];
            vm.closedSessions = [];
            vm.view = view;
            vm.lastUpdated = null;
            vm.refresh = refresh;
            vm.timer = undefined;

            refresh();

            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
            });

            function refresh() {
                return sessionsRestService.getSessions().then(function (data) {
                    $timeout.cancel(vm.timer);
                    vm.sessions = data.sessions;
                    vm.closedSessions = data.closedSessions;
                    vm.lastUpdated = new Date();
                }).then(function () {
                    vm.timer = $timeout(refresh, 2000);
                    return vm.timer;
                }).catch(function () {
                    $timeout.cancel(vm.timer);
                    vm.timer = undefined;
                });
            }

            function view(session) {
                $location.path('/dashboard/session/' + session.id);
            }
        }
        OverviewController.$inject = ['$scope', '$timeout', '$location', 'sessionsRestService'];

        return OverviewController;
    });
})(adminConsole.define);