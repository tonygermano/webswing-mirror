define([], function f() {
    function OverviewController($location,sessionsRestService) {
        var vm = this;
        vm.sessions = [];
        vm.closedSessions = [];
        vm.view = view;

        activate();

        function activate() {
            sessionsRestService.getSessions().then(function (data) {
                vm.sessions = data.sessions;
                vm.closedSessions = data.closedSessions;
            });
        }

        function view(session) {
            $location.path('/dashboard/session/'+session.id);
        }
    }
    OverviewController.$inject = ['$location','sessionsRestService'];

    return OverviewController;
});