define([ 'common/navbar.directive', 'common/sidebar.directive', 'common/messages.directive', 'common/duration.filter' ], function f(navbarDirective,
		sidebarDirective, messagesDirective, durationFilter) {
	var module = angular.module('wsCommon', []);

	module.directive('wsNavbar', navbarDirective);
	module.directive('wsSidebar', sidebarDirective);
	module.directive('wsMessages', messagesDirective);
	module.filter('duration', durationFilter);
});