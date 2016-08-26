(function(define) {
	define([], function f() {
		function utilsService(baseUrl, $http, errorHandler, messageService) {
			return {
				extractValues : extractValues,
				toJson : toJson
			};

			function extractValues(config) {
				if (config != null && config.fields != null) {
					var result = {};
					for ( var index in config.fields) {
						var field = config.fields[index];
						if (field.value == null) {
							result[field.name] = null;
						} else if (field.type === 'Object') {
							result[field.name] = extractValues(field.value);
						} else if (field.type === 'ObjectList') {
							var list = [];
							for ( var listIndex in field.value) {
								list.push(extractValues(field.value[listIndex]));
							}
							result[field.name] = list;
						} else if (field.type === 'ObjectMap') {
							var map = {};
							for ( var mapKey in field.value) {
								map[mapKey] = extractValues(field.value[mapKey]);
							}
							result[field.name] = map;
						} else {
							result[field.name] = field.value;
						}
					}
					if (config.data != null) {
						result = angular.merge({}, config.data, result);
					}
					return result;
				} else {
					return null;
				}
			}

			function toJson(config) {
				return angular.toJson(extractValues(config), true);
			}
		}

		utilsService.$inject = [ 'baseUrl', '$http', 'errorHandler', 'messageService' ];
		return utilsService;
	});
})(adminConsole.define);