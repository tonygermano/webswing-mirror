(function (define) {
    define([], function f() {
        function SettingsRuntimeController(settingsRestService, configRestService) {
            var vm = this;
            vm.settings = {};
            vm.config = {};
            vm.variables = {};

            activate();

            function activate() {
                settingsRestService.getSettings().then(function (data) {
                    angular.extend(vm.settings, data);
                    return configRestService.getConfig();
                }).then(function (data) {
                    angular.extend(vm.config, data);
                    return configRestService.getVariables();
                }).then(function (data) {
                    angular.extend(vm.variables, data);
                });
            }
        }
        SettingsRuntimeController.$inject = ['settingsRestService', 'configRestService'];

        return SettingsRuntimeController;
    });
})(adminConsole.define);