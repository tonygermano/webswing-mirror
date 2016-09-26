(function(define) {
	define([], function f() {
		function SwingConfigController($scope, $timeout, $location, configRestService, $routeParams, permissions, loading) {
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

			$scope.$on('wsStatusChanged', function(evt, ctl) {
				vm.stopped = ctl.startable;
				vm.config.message = null;
				vm.config.enable = vm.stopped ? null : [ 'swingConfig' ];
				activate();
			});

			function activate() {
				if (!loading.isLoading()) {
					loading.startLoading();
					return configRestService.getConfig(vm.path).then(function(data) {
						vm.config = angular.extend({}, vm.config, data);
					}).then(function() {
						return configRestService.getVariables(vm.path, 'Basic');
					}).then(function(data) {
						vm.variables['Basic'] = data;
					}).then(function() {
						return configRestService.getVariables(vm.path, 'SwingApp');
					}).then(function(data) {
						vm.variables['SwingApp'] = data;
					}).then(function() {
						return configRestService.getVariables(vm.path, 'SwingInstance');
					}).then(function(data) {
						vm.variables['SwingInstance'] = data;
					}).then(function() {
						loading.stopLoading();
					});
				}
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
		SwingConfigController.$inject = [ '$scope', '$timeout', '$location', 'configRestService', '$routeParams', 'permissions', 'loading' ];

		return SwingConfigController;
	});
})(adminConsole.define);