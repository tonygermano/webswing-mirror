(function(define) {
	define([], function f() {
		function logRestService(baseUrl, $http, errorHandler, messageService) {
			return {
				getLog : getLog,
				getSessionLog : getSessionLog,
				getSessionLogNames : getSessionLogNames
			};

			function getLog(type, query) {
				return $http.post(toPath(path) + '/rest/logs/' + type, query).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load ' + type + ' log', data, true);
				}
			}
			
			function getSessionLog(path, query) {
				return $http.post(toPath(path) + '/rest/logs/session', query).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load session log', data, true);
				}
			}
			
			function getSessionLogNames(path, app) {
				return $http.get(toPath(path) + '/rest/logs/session/names?app=' + app).then(success, failed);
				function success(data) {
					return data.data;
				}
				function failed(data) {
					return errorHandler.handleRestError('load session log names - ' + app, data, true);
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

		logRestService.$inject = [ 'baseUrl', '$http', 'errorHandler', 'messageService' ];
		return logRestService;
	});
})(adminConsole.define);