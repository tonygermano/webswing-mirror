(function(define) {
	define([], function f() {
		function SwingConfigController($scope, $timeout, $location, configRestService, $routeParams, wsUtils, permissions) {
			var vm = this;
			vm.config = {
				hide : [ 'path' ],
				enable : [ 'swingConfig' ]
			};
			vm.variables = {};
			vm.reset = reset;
			vm.apply = apply;
			vm.back = back;
			vm.path = $routeParams.path;
			vm.permissions = permissions;
			activate();

			$scope.$on('wsStatusChanged', function(evt, ctl) {
				vm.stopped = ctl.startable;
				vm.config.enable = vm.stopped ? null : [ 'swingConfig' ];
				activate();
			});

			function activate() {
				configRestService.getConfig(vm.path).then(function(data) {
					vm.config = angular.extend({}, vm.config, data);
				});
				configRestService.getVariables().then(function(data) {
					vm.variables = data;
				});
			}

			function reset() {
				activate();
			}

			function apply() {
				if (vm.stopped) {
					configRestService.setConfig(vm.path, wsUtils.extractValues(vm.config)).then(activate);
				} else {
					configRestService.setSwingConfig(vm.path, wsUtils.extractValues(vm.config)).then(activate);
				}
			}

			function back() {
				$location.path('/dashboard');
			}
		}
		SwingConfigController.$inject = [ '$scope', '$timeout', '$location', 'configRestService', '$routeParams', 'wsUtils', 'permissions' ];

		return SwingConfigController;
	});
})(adminConsole.define);