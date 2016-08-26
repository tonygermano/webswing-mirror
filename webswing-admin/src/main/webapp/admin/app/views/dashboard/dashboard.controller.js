(function(define) {
	define([], function f() {
		function DashboardController($scope, $location, configRestService) {
			var vm = this;
			vm.refresh = refresh;
			vm.paths = [];
			vm.lastUpdated = null;

			refresh();

			function refresh() {
				configRestService.getPaths().then(function(data) {
					vm.paths = data;
					vm.lastUpdated = new Date();
				})
			}

		}
		DashboardController.$inject = [ '$scope', '$location', 'configRestService' ];

		return DashboardController;
	});
})(adminConsole.define);