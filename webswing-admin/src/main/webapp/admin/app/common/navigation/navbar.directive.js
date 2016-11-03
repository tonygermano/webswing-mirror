(function(define) {
	define([ 'text!common/navigation/navbar.template.html' ], function f(htmlTemplate) {
		function wsNavbarDirective() {
			return {
				restrict : 'E',
				replace : true,
				template : htmlTemplate,
				scope : {
					branding : '@',
					navigation : '@'
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsNavbarDirectiveController
			};
		}

		function wsNavbarDirectiveController($scope, navigationService, loginService, baseUrl, permissions) {
			var vm = this;
			vm.isActive = navigationService.isActive;
			vm.isCollapsed = true;
			vm.logout = logout;
			vm.baseUrl = baseUrl;

			$scope.$on('wsPermissionsReloaded', loadLocations);

			function loadLocations() {
				var locations = navigationService.getLocations();
				for (var i = locations.length - 1; i >= 0; i--) {
					if (locations[i].permission != null && !permissions[locations[i].permission]) {
						locations.splice(i, 1);
					}
				}
				vm.locations = locations;
			}

			function logout() {
				loginService.logout();
			}

		}
		wsNavbarDirectiveController.$inject = [ '$scope', 'navigationService', 'loginService', 'baseUrl', 'permissions' ];

		return wsNavbarDirective;
	});
})(adminConsole.define);