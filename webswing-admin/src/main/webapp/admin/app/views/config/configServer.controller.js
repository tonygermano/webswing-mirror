(function(define) {
	define([], function f() {
		function ConfigEditController($scope, baseUrl, configRestService, permissions, loading) {
			var vm = this;
			vm.permissions = permissions;
			vm.readonly = !vm.permissions.configEdit;
			vm.config = {
				hide : [ 'path', 'icon', 'swingConfig' ],
				enable : null
			};
			vm.variables = {};
			vm.reset = reset;
			vm.apply = apply;
			vm.baseUrl = baseUrl;

			activate();

			$scope.$on('vm.permissions.configEdit', function(evt, ctl) {
				vm.readonly = !vm.permissions.configEdit;
			});

			function activate() {
				if(!loading.isLoading()){
					loading.startLoading();
					return configRestService.getConfig(baseUrl).then(function(data) {
						vm.config = angular.extend({}, vm.config, data);
					}).then(function() {
						return configRestService.getVariables(baseUrl, 'Basic');
					}).then(function(data) {
						vm.variables['Basic'] = data;
					}).then(function() {
						return configRestService.getVariables(baseUrl, 'SwingApp');
					}).then(function(data) {
						vm.variables['SwingApp'] = data;
					}).then(function(){
						loading.stopLoading();
					});
				}
			}

			function reset() {
				return activate();
			}

			function apply(config) {
				return configRestService.setConfig(baseUrl, config).then(activate);
			}
		}
		ConfigEditController.$inject = [ '$scope', 'baseUrl', 'configRestService', 'permissions', 'loading' ];

		return ConfigEditController;
	});
})(adminConsole.define);