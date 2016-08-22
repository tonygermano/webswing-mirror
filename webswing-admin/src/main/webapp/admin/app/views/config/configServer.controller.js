(function (define) {
    define([], function f() {
        function ConfigEditController(configRestService) {
            var vm = this;
            vm.config = {};
            vm.variables = {};
            vm.reset = reset;
            vm.apply = apply;

            activate();

            function activate() {
                configRestService.getConfig("/").then(function (data) {
                    vm.config = data;
                });
                configRestService.getVariables().then(function (data) {
                    vm.variables = data;
                });
            }

            function reset() {
                activate();
            }

            function apply() {
                configRestService.setConfig(vm.config);
            }
        }
        ConfigEditController.$inject = ['configRestService'];

        return ConfigEditController;
    });
})(adminConsole.define);