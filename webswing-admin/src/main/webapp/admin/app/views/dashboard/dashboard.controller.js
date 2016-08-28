(function(define) {
	define([], function f() {
		function DashboardController($scope, $location, configRestService, permissions) {
			var vm = this;
			vm.refresh = refresh;
			vm.clean = clean;
			vm.create = create;
			vm.newPath = '';
			vm.paths = [];
			vm.lastUpdated = null;
			vm.permissions = permissions;
			refresh();

			function refresh() {
				vm.paths = [];
				configRestService.getPaths().then(function(data) {
					vm.paths = data;
					vm.lastUpdated = new Date();
				})
			}

			function clean() {
				vm.newPath = '';
			}

			function create() {
				configRestService.create(vm.newPath).then(refresh);
			}

		}
		DashboardController.$inject = [ '$scope', '$location', 'configRestService', 'permissions' ];

		return DashboardController;
	});
})(adminConsole.define);