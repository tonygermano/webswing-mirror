(function (define) {
    define([], function f() {
        function SessionController(baseUrl, $scope, $timeout, $location, sessionsRestService, $routeParams) {
            var vm = this;
            vm.session = {};
            vm.lastUpdated = null;
            vm.back = back;
            vm.refresh = refresh;
            vm.kill = kill;
            vm.forceKill = forceKill;
            vm.timer = undefined;
            vm.control = false;

            load().then(view).then(refresh);

            $scope.$watch('vm.control', function (val) {
                if (window.webswingadmin != null && window.webswingadmin.webswingmirrorview != null && val != null) {
                    window.webswingadmin.webswingmirrorview.setControl(val);
                }
            });

            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
                window.webswingadmin.webswingmirrorview.disconnect();
            });

            function load() {
                return sessionsRestService.getSession($routeParams.sessionId).then(function (data) {
                    $timeout.cancel(vm.timer);
                    vm.session = data;
                    vm.lastUpdated = new Date();
                });
            }
            function refresh() {
                return load().then(function () {
                    vm.timer = $timeout(refresh, 2000);
                    return vm.timer;
                }).catch(function () {
                    $timeout.cancel(vm.timer);
                    vm.timer = undefined;
                });
            }

            function view() {
                var config = {
                    autoStart: false,
                    connectionUrl: baseUrl,
                    clientId: vm.session.id,
                    applicationName: vm.session.application,
                    control: vm.control,
                    mirrorMode: true
                };
                window.webswingadmin.scan();
                window.webswingadmin.webswingmirrorview.disconnect();
                window.webswingadmin.webswingmirrorview.configure(config);
                window.webswingadmin.webswingmirrorview.start();
            }

            function kill() {
                return sessionsRestService.killSession($routeParams.sessionId).then(function () {
                    back();
                });
            }
            
            function forceKill() {
            	return sessionsRestService.forceKillSession($routeParams.sessionId).then(function () {
                    back();
                });
            }

            function back() {
                $location.path('/dashboard/overview');
                $timeout.cancel(vm.timer);
            }
        }
        SessionController.$inject = ['baseUrl', '$scope', '$timeout', '$location', 'sessionsRestService', '$routeParams'];

        return SessionController;
    });
})(adminConsole.define);