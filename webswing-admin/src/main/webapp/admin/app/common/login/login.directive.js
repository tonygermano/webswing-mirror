(function(define) {
	define([ 'text!common/login/login.template.html' ], function f(htmlTemplate) {
		function wsLoginDirective() {
			return {
				restrict : 'E',
				replace : true,
				scope : {},
				controller : wsLoginDirectiveController
			};
		}

		function wsLoginDirectiveController($scope, $element, $location, loginService, $modal) {
			var vm = $scope;
			vm.formData = {};
			vm.loginDialog = null;
			vm.loginErrorMsg = null;

			$scope.$on('wsLoginRequestEvent', displayLoginDialog);

			function displayLoginDialog(evt, doLoginCallback) {
				if (vm.loginDialog != null) {
					vm.loginDialog.dismiss();
				}
				vm.loginDialog = $modal.open({
					template : htmlTemplate,
					scope : $scope,
					backdrop : 'static'
				});
				vm.loginDialog.rendered.then(function(){
					var element = $('#webswingLoginDialogContent')
					var loginData = {
						securityToken : $location.search().securityToken,
						successUrl : window.top.location.href
					};
					doLoginCallback(element, loginData, function() {
						vm.loginDialog.dismiss();
					});
				})
			}

		}

		wsLoginDirectiveController.$inject = [ '$scope', '$element', '$location', 'loginService', '$modal' ];

		return wsLoginDirective;

	});
})(adminConsole.define);