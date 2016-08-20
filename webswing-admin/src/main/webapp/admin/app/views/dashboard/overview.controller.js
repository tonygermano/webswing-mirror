(function (define) {
    define([], function f() {
        function OverviewController($scope, $timeout, $location, sessionsRestService,$routeParams) {
            var vm = this;
            vm.sessions = [];
            vm.closedSessions = [];
            vm.view = view;
            vm.lastUpdated = null;
            vm.refresh = refresh;
            vm.timer = undefined;
            vm.play = play;
            vm.back = back;

            refresh();

            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
            });

            function refresh() {
                return sessionsRestService.getSessions($routeParams.path).then(function (data) {
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

            function play(session){
                $location.url('/dashboard/playback?playback='+session.recordingFile);
            }
            
            function back() {
                $location.path('/dashboard');
                $timeout.cancel(vm.timer);
            }
        }
        OverviewController.$inject = ['$scope', '$timeout', '$location', 'sessionsRestService', '$routeParams'];

        return OverviewController;
    });
})(adminConsole.define);