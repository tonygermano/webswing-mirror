(function(define) {
	define([], function f() {
		function ConfigEditController(baseUrl, configRestService, wsUtils) {
			var vm = this;
			vm.config = {
				hide : [ 'path', 'icon', 'swingConfig' ],
				enable : null
			};
			vm.variables = {};
			vm.reset = reset;
			vm.apply = apply;
			vm.hide = [ 'path', 'icon', 'swingConfig' ];

			activate();

			function activate() {
				configRestService.getConfig(baseUrl).then(function(data) {
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
				configRestService.setConfig(baseUrl, wsUtils.extractValues(vm.config)).then(activate);
			}
		}
		ConfigEditController.$inject = [ 'baseUrl', 'configRestService', 'wsUtils' ];

		return ConfigEditController;
	});
})(adminConsole.define);