(function (define) {
    define([], function f() {
        function ConfigEditController(configRestService, $routeParams) {
            var vm = this;
            vm.config = {};
            vm.variables = {};
            vm.reset = reset;
            vm.apply = apply;
            vm.path  = $routeParams.path;

            activate();

            function activate() {
                configRestService.getConfig(vm.path).then(function (data) {
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
        ConfigEditController.$inject = ['configRestService', '$routeParams'];

        return ConfigEditController;
    });
})(adminConsole.define);