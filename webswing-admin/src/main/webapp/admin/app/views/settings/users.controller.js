define([], function f() {
    function SettingsUsersController(usersRestService) {
        var vm = this;
        vm.users = '';
        vm.reset = reset;
        vm.apply = apply;

        activate();

        function activate() {
            usersRestService.getUsers().then(function (data) {
                vm.users = data.users;
            });
        }

        function reset() {
            activate();
        }

        function apply() {
            usersRestService.setUsers({users: vm.users});
        }
    }
    SettingsUsersController.$inject = ['usersRestService'];

    return SettingsUsersController;
});