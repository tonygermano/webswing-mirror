(function (define) {
    define([], function f() {
        function PlaybackController($scope, $location, $routeParams) {
            var vm = this;
            vm.path = $routeParams.path;
            vm.back = back;
            view();

            $scope.$on('$destroy', function () {
                window.webswingadmin.webswingplayback.disconnect();
                window.webswingadmin.webswingplayback=null;
            });

            function view() {
                var config = {
                    autoStart: false,
                    connectionUrl: "/"+vm.path,
                    recordingPlayback: $routeParams.playback,
                    debugLog: true,
                    control: false,
                    mirrorMode: true
                };
                window.webswingadmin.scan();
                window.webswingadmin.webswingplayback.disconnect();
                window.webswingadmin.webswingplayback.configure(config);
                window.webswingadmin.webswingplayback.start();
            }

            function back() {
            	$location.search('app', vm.path);
                $location.path('/sessions');
            }
        }
        PlaybackController.$inject = ['$scope', '$location', '$routeParams'];

        return PlaybackController;
    });
})(adminConsole.define);