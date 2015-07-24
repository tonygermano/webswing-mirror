(function (define) {
    define(['text!common/login/login.template.html'], function f(htmlTemplate) {
        function wsLoginDirective() {
            return {
                restrict: 'E',
                replace: true,
                scope: {},
                controllerAs: 'vm',
                bindToController: true,
                controller: wsLoginDirectiveController
            };
        }

        function wsLoginDirectiveController($scope, loginService, $modal) {
            var vm = this;
            vm.formData = {};
            vm.loginDialog = null;
            vm.loginErrorMsg = null;
            vm.handleLoginEnter = handleLoginEnter;
            vm.login = login;

            $scope.$on('wsLoginRequestEvent', displayLoginDialog);

            function displayLoginDialog(evt, doLoginCallback) {
                if (vm.loginDialog != null) {
                    vm.loginDialog.dismiss();
                }
                vm.loginCallback = doLoginCallback;
                vm.loginDialog = $modal.open({
                    template: htmlTemplate,
                    scope: $scope,
                    backdrop: 'static',
                    keyboard: false,
                });
            }

            function handleLoginEnter(evt) {
                if (event.keyCode === 13) {
                    login();
                }
            }

            function login() {
                if (vm.loginCallback != null) {
                    vm.loginCallback(vm.formData).then(function (data) {
                        if (data.status === 200) {
                            vm.loginErrorMsg = null;
                            vm.loginCallback = null;
                            vm.loginDialog.close();
                        } else {
                            if (data.status === 403) {
                                vm.loginErrorMsg = "This user is not authorized to use this application.";
                                loginService.logout(true);
                            } else if (data.status === 401) {
                                vm.loginErrorMsg = "Authentication failed. User or password is not valid.";
                            } else {
                                vm.loginDialog = "Unexpected result.";
                            }
                        }
                    });
                } else {
                    loginService.login();
                }
            }
        }
        wsLoginDirectiveController.$inject = ['$scope', 'loginService', '$modal'];

        return wsLoginDirective;
    });
})(adminConsole.define);