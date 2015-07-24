(function (define) {
    define([], function f() {
        function SettingsEditController(configRestService) {
            var vm = this;
            vm.config = {};
            vm.variables = {};
            vm.reset = reset;
            vm.apply = apply;

            activate();

            function activate() {
                configRestService.getConfig().then(function (data) {
                    angular.extend(vm.config, data);
                    return configRestService.getVariables();
                }).then(function (data) {
                    angular.extend(vm.variables, data);
                });
            }

            function reset() {
                activate();
            }

            function apply() {
                configRestService.setConfig(vm.config);
            }
        }
        SettingsEditController.$inject = ['configRestService'];

        return SettingsEditController;
    });
})(adminConsole.define);