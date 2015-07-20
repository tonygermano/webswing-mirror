(function (define) {
    define(['text!common/navigation/navbar.template.html'], function f(htmlTemplate) {
        function wsNavbarDirective() {
            return {
                restrict: 'E',
                replace: true,
                template: htmlTemplate,
                scope: {},
                controllerAs: 'vm',
                bindToController: true,
                controller: wsNavbarDirectiveController
            };
        }

        function wsNavbarDirectiveController(navigationService, loginService) {
            var vm = this;
            vm.locations = navigationService.getLocations();
            vm.isActive = navigationService.isActive;
            vm.isCollapsed = true;
            vm.logout = logout;

            function logout() {
                loginService.logout();
            }
        }
        wsNavbarDirectiveController.$inject = ['navigationService', 'loginService'];

        return wsNavbarDirective;
    });
})(adminConsole.define);