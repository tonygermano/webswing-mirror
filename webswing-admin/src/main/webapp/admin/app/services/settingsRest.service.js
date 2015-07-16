define([], function f() {
	function settingsRestService($http, $log, messageService) {
		return {
			getSettings : getSettings
		};

		function getSettings() {
			return $http.get('/rest/admin/settings').then(success, failed);
			function success(data) {
				return data.data;
			}
			function failed(data) {
				messageService.error('Failed to load server settings.');
				$log.error('Loading of  server settings failed with status ' + data.status + ' and message :' + data.data);
				return null;
			}
		}
	}

	settingsRestService.$inject = [ '$http', '$log', 'messageService' ];
	return settingsRestService;
});