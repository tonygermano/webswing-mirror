define([ 'views/dashboard/overview.controller','views/dashboard/session.controller' ], function f(DashboardOverviewController,DashboardSessionController) {
	var module = angular.module('wsDashboard', []);

	module.controller('DashboardOverviewController', DashboardOverviewController);
	module.controller('DashboardSessionController', DashboardSessionController);

	module.run([ 'navigationService', function(navigationService) {
		var dashboard = navigationService.addLocation('Dashboard', '#/dashboard');
		dashboard.addLocation("Overview", '#/dashboard/overview',true);
	} ]);

	module.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/dashboard/overview', {
			controller : 'DashboardOverviewController',
			controllerAs : 'vm',
			templateUrl : 'app/views/dashboard/overview.template.html'
		});
                $routeProvider.when('/dashboard/session/:sessionId', {
			controller : 'DashboardSessionController',
			controllerAs : 'vm',
			templateUrl : 'app/views/dashboard/session.template.html'
		});
		$routeProvider.otherwise('/dashboard/overview');
	} ]);

});