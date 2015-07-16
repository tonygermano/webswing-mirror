define([ 'text!common/navbar.template.html' ], function f(htmlTemplate) {
	function wsNavbarDirective() {
		return {
			restrict : 'E',
			replace : true,
			template : htmlTemplate,
			scope : {},
			controllerAs : 'vm',
			bindToController : true,
			controller : wsNavbarDirectiveController
		};
	}

	function wsNavbarDirectiveController(navigationService) {
		var vm = this;
		vm.locations = navigationService.getLocations();
		vm.isActive = navigationService.isActive;
	}
	wsNavbarDirectiveController.$inject = ['navigationService' ];

	return wsNavbarDirective;
});