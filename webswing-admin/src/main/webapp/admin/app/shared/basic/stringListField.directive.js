(function (define) {
    define(['text!shared/basic/stringListField.template.html'], function f(htmlTemplate) {
        function wsStringListFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    field: '=',
                    value: '=',
                    items: '=',
                    readonly: '=',
                    discriminator: '=',
                    restricted: '@',
                    label: '@',
                    desc: '@',
                    path: '=',
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsStringListFieldDirectiveController
            };
        }

        function wsStringListFieldDirectiveController($scope, $attrs, configRestService) {
            var vm = this;
            vm.addString = addString;
            vm.deleteString = deleteString;
            vm.helpVisible = [];
            vm.toggleHelper = toggleHelper;
            vm.setChoice = setChoice;
            vm.onBlur = onBlur;
            vm.toggleHelperClose = toggleHelperClose;
            vm.resolvedString = '';

            if (vm.value == null) {
                vm.field.value = [];
            }

            $scope.$on('wsHelperClose', function (evt, ctrl, index) {
                for (var i = 0; i < vm.helpVisible.length; i++) {
                    if (vm !== ctrl || index !== i) {
                        vm.helpVisible[i] = false;
                    }
                }
            });

            function toggleHelper(index) {
                configRestService
                    .resolve(vm.path, vm.field.variables, vm.value[index])
                    .then(function (responseData) {
                        vm.resolvedString = responseData;
                    });
                vm.helpVisible[index] = vm.helpVisible == null ? true : !vm.helpVisible[index];
                if (vm.helpVisible[index]) {
                    $scope.$emit('wsHelperOpened', vm, index);
                }
            }

            function toggleHelperClose() {
                $scope.$broadcast('wsHelperClose', null, null);
            }

            function addString(index) {
                vm.value.splice(index + 1, 0, '');
            }

            function deleteString(index) {
                toggleHelperClose();
                vm.value.splice(index, 1);
                if (vm.discriminator) {
                    requestFormUpdate();
                }
            }

            function setChoice(index, value) {
                if (vm.value[index] !== value) {
                    vm.value[index] = value;
                    if (vm.discriminator) {
                        requestFormUpdate();
                    }
                }
            }

            function onBlur() {
                if (vm.discriminator) {
                    requestFormUpdate();
                }
            }

            function requestFormUpdate() {
                $scope.$emit('wsRequestFormUpdate', vm);
            }
        }

        wsStringListFieldDirectiveController.$inject = ['$scope', '$attrs', 'configRestService'];

        return wsStringListFieldDirective;
    });
})(adminConsole.define);