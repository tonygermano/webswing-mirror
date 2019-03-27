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

		function wsNavbarDirectiveController($scope, navigationService, loginService, configRestService, baseUrl, permissions) {
			var vm = this;
			vm.isActive = navigationService.isActive;
			vm.isCollapsed = true;
			vm.logout = logout;
			vm.baseUrl = baseUrl;
			vm.locations = [];
			$scope.$on('wsPermissionsReloaded', loadLocations);
			
			loadVersion();

			function loadLocations() {
				vm.locations.length = 0;// clean array
				var locations = navigationService.getLocations();
				for (var i = 0; i < locations.length; i++) {
					if (locations[i].permission == null || permissions[locations[i].permission]) {
						vm.locations.push(locations[i]);
					}
				}
			}
			
			function loadVersion() {
				configRestService.getVersion().then(function(version) {
					vm.version = parseVersion(version);
				});
			}

			function logout() {
				loginService.logout();
			}

			function parseVersion(version) {
				if (!version) {
					return version;
				}
				var idx = version.indexOf('-');
				if (idx != -1) {
					if (version.indexOf('-', idx+1) != -1) {
						idx = version.indexOf('-', idx+1);
					}
					version = version.substring(0, idx);
				}
				
				return version;
			}
			
		}
		wsNavbarDirectiveController.$inject = [ '$scope', 'navigationService', 'loginService', 'configRestService', 'baseUrl', 'permissions' ];

		return wsNavbarDirective;
	});
})(adminConsole.define);