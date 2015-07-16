define([], function f() {
    function SessionController($location, sessionsRestService, $routeParams) {
        var vm = this;
        vm.session = {};
        vm.back = back;

        activate();

        function activate() {
            sessionsRestService.getSession($routeParams.sessionId).then(function (data) {
                vm.session = data;
            });
        }

        function back() {
            $location.path('/dashboard/overview');
        }
    }
    SessionController.$inject = ['$location', 'sessionsRestService', '$routeParams'];

    return SessionController;
});