(function(define) {
	define([], function f() {
		function SwingConfigController($scope, $timeout, $location, configRestService, $routeParams) {
			var vm = this;
			vm.config = {};
			vm.variables = {};
			vm.reset = reset;
			vm.apply = apply;
			vm.back = back;
			vm.path = $routeParams.path;
			vm.readonly = false;
			vm.hide = ['path'];
			activate();

			function activate() {
				configRestService.getConfig(vm.path).then(function(data) {
					vm.config = data;
				});
				configRestService.getVariables().then(function(data) {
					vm.variables = data;
				});
			}

			function reset() {
				activate();
			}

			function apply() {
				configRestService.setConfig(vm.config);
			}

			function back() {
				$location.path('/dashboard');
			}
		}
		SwingConfigController.$inject = [ '$scope', '$timeout', '$location', 'configRestService', '$routeParams' ];

		return SwingConfigController;
	});
})(adminConsole.define);