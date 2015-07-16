define([ 'views/settings/runtime.controller','views/settings/configuration.controller','views/settings/users.controller' ], function f(runtimeCtrl,configCtrl,usersCtrl) {
	var module = angular.module('wsSettings', [ ]);
	module.controller('SettingsRuntimeController', runtimeCtrl);
	module.controller('SettingsConfigurationController', configCtrl);
	module.controller('SettingsUsersController', usersCtrl);

	module.run([ 'navigationService', function(navigationService) {
		var primary = navigationService.addLocation('Settings', '#/settings');
		primary.addLocation("Runtime", '#/settings/runtime', true);
		primary.addLocation("Server configuration", '#/settings/edit');
		primary.addLocation("Users", '#/settings/users');
	} ]);

	module.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/settings/runtime', {
			controller : 'SettingsRuntimeController',
			controllerAs : 'vm',
			templateUrl : 'app/views/settings/runtime.template.html'
		}).when('/settings/edit', {
			controller : 'SettingsConfigurationController',
			controllerAs : 'vm',
			templateUrl : 'app/views/settings/configuration.template.html'
		}).when('/settings/users', {
			controller : 'SettingsUsersController',
			controllerAs : 'vm',
			templateUrl : 'app/views/settings/users.template.html'
		});
	} ]);
});