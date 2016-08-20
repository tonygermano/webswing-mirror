(function (define) {
    define([], function f() {
        function DashboardController($scope, $timeout, $location, sessionsRestService) {
            var vm = this;
            vm.apps = [];
            vm.lastUpdated = null;
            vm.refresh = refresh;
            vm.timer = undefined;

            refresh();

            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
            });

            function refresh() {
                return sessionsRestService.getApps().then(function (data) {
                    $timeout.cancel(vm.timer);
                    vm.apps = data;
                    vm.lastUpdated = new Date();
                }).then(function () {
                    vm.timer = $timeout(refresh, 5000);
                    return vm.timer;
                }).catch(function () {
                    $timeout.cancel(vm.timer);
                    vm.timer = undefined;
                });
            }

        }
        DashboardController.$inject = ['$scope', '$timeout', '$location', 'sessionsRestService'];

        return DashboardController;
    });
})(adminConsole.define);