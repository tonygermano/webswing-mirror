(function(define) {
	define([], function f() {
		function configRestService(baseUrl, $http, errorHandler, messageService) {
			return {
				getConfig : getConfig,
				setConfig : setConfig,
				getVariables : getVariables,
				getMeta : getMeta
			};

			function getConfig(path) {
				return $http.get(baseUrl + '/rest/config/' + path).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load configuration', data, true);
				}
			}
			function setConfig(config) {
				return $http.post(baseUrl + '/rest/config', config).then(success, failed);
				function success() {
					messageService.success('Configuration saved.');
				}
				function failed(data) {
					return errorHandler.handleRestError('save server configuration', data);
				}
			}
			function getVariables() {
				return $http.get(baseUrl + '/rest/config/variables', {
					cache : true
				}).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load available configuration variables', data, true);
				}
			}
			function getMeta(config) {
				return $http.post(baseUrl + '/rest/configMeta', config).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('reload settings view', data, true);
				}
			}
		}

		configRestService.$inject = [ 'baseUrl', '$http', 'errorHandler', 'messageService' ];
		return configRestService;
	});
})(adminConsole.define);