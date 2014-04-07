'use strict';


// Declare app level module which depends on filters, and services
angular.module('ws-console', [
  'ngRoute',
  'ui.ace',
  'ui.jq',
  'ws-console.filters',
  'ws-console.services',
  'ws-console.directives',
  'ws-console.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/dashboard/overview', {templateUrl: 'partials/dashboard/overview.html', controller: 'Dashboard'});
  $routeProvider.when('/settings/overview', {templateUrl: 'partials/settings/overview.html', controller: 'Settings'});
  $routeProvider.when('/settings/edit', {templateUrl: 'partials/settings/edit.html', controller: 'Settings'});
  $routeProvider.when('/settings/swing-app', {templateUrl: 'partials/settings/swing-a.html', controller: 'Settings'});
  $routeProvider.when('/settings/users', {templateUrl: 'partials/settings/users.html', controller: 'Settings'});
  $routeProvider.otherwise({redirectTo: '/dashboard/overview'});
}]);
