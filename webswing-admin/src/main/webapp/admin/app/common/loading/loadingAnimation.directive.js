(function(define) {
	define([], function f() {
		function wsLoadingAnimationDirective() {
			return {
				restrict : 'E',
				template : '<div ng-if="vm.isLoading()" class="loadingAnimationPanel"><div class="spinner"><div class="dot1"></div> <div class="dot2"></div></div></div>',
				controllerAs : 'vm',
				bindToController : true,
				controller : wsLoadingAnimationDirectiveController
			};
		}
		
		function wsLoadingAnimationDirectiveController(loading) {
			var vm = this;
			vm.isLoading = loading.isLoading;
		}
		wsLoadingAnimationDirectiveController.$inject = [ 'loading' ];
		
		return wsLoadingAnimationDirective;
	});
})(adminConsole.define);