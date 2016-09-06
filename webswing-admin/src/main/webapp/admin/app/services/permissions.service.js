(function(define) {
	define([], function f() {
		function permService(baseUrl, $http, messageService) {
			var permissions = {
					reload: getPermissions
			};
			getPermissions();
			return permissions;

			function getPermissions() {
				return $http.get(baseUrl + '/rest/permissions').then(success, failed);
				function success(data) {
					angular.merge(permissions,data.data);
				}
				function failed(data) {
					messageService.error('Failed to resolve permissions');
				}
			}
		}

		permService.$inject = [ 'baseUrl', '$http', 'messageService' ];
		return permService;
	});
})(adminConsole.define);