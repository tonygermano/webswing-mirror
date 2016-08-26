(function(define) {
	define([], function f() {
		function configRestService(baseUrl, $http, errorHandler, messageService) {
			return {
				getPaths : getPaths,
				getInfo : getInfo,
				getConfig : getConfig,
				setConfig : setConfig,
				setSwingConfig : setSwingConfig,
				getVariables : getVariables,
				start : start,
				stop : stop,
				getMeta : getMeta
			};

			function getPaths() {
				return $http.get(baseUrl + '/rest/paths').then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load installed swing apps', data, true);
				}
			}

			function getInfo(path) {
				return $http.get(toPath(path) + '/info').then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load installed swing apps', data, true);
				}
			}

			function getConfig(path) {
				return $http.get(toPath(path) + '/rest/config').then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load configuration', data, true);
				}
			}

			function setConfig(path, config) {
				return $http.post(toPath(path) + '/rest/config', config).then(success, failed);
				function success() {
					messageService.success('Configuration saved.');
				}
				function failed(data) {
					return errorHandler.handleRestError('save path configuration', data);
				}
			}

			function setSwingConfig(path, config) {
				return $http.post(toPath(path) + '/rest/swingConfig', config).then(success, failed);
				function success() {
					messageService.success('Swing Configuration saved.');
				}
				function failed(data) {
					return errorHandler.handleRestError('save ' + path + ' configuration', data);
				}
			}

			function start(path) {
				return $http.get(toPath(path) + '/start').then(success, failed);
				function success() {
					messageService.success('Starting Webswing handler.');
				}
				function failed(data) {
					return errorHandler.handleRestError('start ' + path + ' handler', data);
				}
			}

			function stop(path) {
				return $http.get(toPath(path) + '/stop').then(success, failed);
				function success() {
					messageService.success('Stopping Webswing handler.');
				}
				function failed(data) {
					return errorHandler.handleRestError('stop ' + path + ' handler', data);
				}
			}

			function getVariables() {
				return $http.get(baseUrl + '/rest/admin/variables', {
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
				return $http.post(baseUrl + '/rest/admin/metaObject', config).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('reload settings view', data, true);
				}
			}

			function toPath(path) {
				if (path.substr(0, 4) === 'http') {
					return path;
				}
				if (path.substr(0, 1) !== '/') {
					path = '/' + path
				}
				if (path.length === 1) {
					path = '';
				}
				return path;
			}

		}

		configRestService.$inject = [ 'baseUrl', '$http', 'errorHandler', 'messageService' ];
		return configRestService;
	});
})(adminConsole.define);