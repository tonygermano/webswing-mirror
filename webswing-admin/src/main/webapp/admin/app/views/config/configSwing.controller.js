(function(define) {
	define([], function f() {
		function SwingConfigController($scope, $timeout, $location, configRestService, $routeParams, permissions) {
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
			vm.readonly = !vm.permissions.configEdit;
			activate();

			$scope.$on('vm.permissions.configEdit', function(evt, ctl) {
				vm.readonly = !vm.permissions.configEdit;
			});
			$scope.$watch("vm.readonly", function(value) {
				console.log('');
			});

			$scope.$on('wsStatusChanged', function(evt, ctl) {
				vm.stopped = ctl.startable;
				vm.config.message = null;
				vm.config.enable = vm.stopped ? null : [ 'swingConfig' ];
				activate();
			});

			function activate() {
				return configRestService.getConfig(vm.path).then(function(data) {
					vm.config = angular.extend({}, vm.config, data);
				}).then(configRestService.getVariables).then(function(data) {
					vm.variables = data;
				});
			}

			function reset() {
				return activate();
			}

			function apply(config) {
				if (vm.stopped) {
					return configRestService.setConfig(vm.path, config).then(activate);
				} else {
					return configRestService.setSwingConfig(vm.path, config).then(activate);
				}
			}

			function back() {
				$location.path('/dashboard');
			}
		}
		SwingConfigController.$inject = [ '$scope', '$timeout', '$location', 'configRestService', '$routeParams', 'permissions' ];

		return SwingConfigController;
	});
})(adminConsole.define);