(function (define) {
    define(['text!shared/basic/stringField.template.html'], function f(htmlTemplate) {
        function wsStringFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                transclude: true,
                scope: {
                    value: '=',
                    items: '=',
                    readonly: '=',
                    discriminator: '=',
                    restricted: '@',
                    label: '@',
                    desc: '@',
                    type: '@',
                    path: '=',
                    field: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                link: link,
                controller: controller
            };
        }

        function link(scope, element, attrs, ctrl) {

        }

        function controller($scope, $attrs, configRestService) {
            var vm = this;
            vm.originalValue = vm.value;
            vm.required = resolve('required', false);
            vm.requiredMsg = resolve('requiredMsg', 'This value is mandatory!');
            vm.helpVisible = false;
            vm.resolvedString = '';
            vm.setChoice = setChoice;
            vm.toggleHelper = toggleHelper;
            vm.toggleHelperClose = toggleHelperClose;
            vm.onBlur = onBlur;

            $scope.$on('wsHelperClose', function (evt, ctrl) {
                if (vm !== ctrl) {
                    vm.helpVisible = false;
                }
            });

            function toggleHelper() {
                configRestService
                    .resolve(vm.path, vm.field.variables, vm.value)
                    .then(function (responseData) {
                        vm.resolvedString = responseData;
                    });
                vm.helpVisible = !vm.helpVisible;
                if (vm.helpVisible) {
                    $scope.$emit('wsHelperOpened', vm);
                }
            }

            function toggleHelperClose() {
                $scope.$broadcast('wsHelperClose', null, null);
            }

            function setChoice(value) {
                if (vm.value !== value) {
                    vm.value = value;
                    if (vm.discriminator) {
                        requestFormUpdate();
                    }
                }
            }

            function onBlur() {
                if (vm.originalValue !== vm.value) {
                    vm.originalValue = vm.value;
                    if (vm.discriminator) {
                        requestFormUpdate();
                    }
                }
            }

            function requestFormUpdate() {
                $scope.$emit('wsRequestFormUpdate', vm);
            }

            function resolve(name, defaultVal) {
                if ($attrs[name] != null) {
                    return $attrs[name];
                } else {
                    return defaultVal;
                }
            }
        }

        controller.$inject = ['$scope', '$attrs', 'configRestService'];

        return wsStringFieldDirective;
    });
})(adminConsole.define);