(function(define) {
	define([ 'views/config/config.controller',
	         'views/config/swing.controller', ], function f(configCtrl, swingCtrl) {
		var module = angular.module('wsConfig', []);
		
		module.controller('ConfigController', configCtrl);
		module.controller('SwingController', swingCtrl);

		module.run([ 'navigationService', function(navigationService) {
			var primary = navigationService.addLocation('Configuration', '#/config/server');
		} ]);

		module.config([ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/config/server/:path', {
				controller : 'ConfigController',
				controllerAs : 'vm',
				templateUrl : 'app/views/config/config.template.html'
			});
			$routeProvider.when('/config/swing/:path', {
				controller : 'SwingController',
				controllerAs : 'vm',
				templateUrl : 'app/views/config/swing.template.html'
			});
		} ]);
	});
})(adminConsole.define);