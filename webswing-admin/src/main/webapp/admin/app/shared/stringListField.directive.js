(function (define) {
    define(['text!shared/stringListField.template.html'], function f(htmlTemplate) {
        function wsStringListFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    config: '=',
                    value: '=',
                    variables: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsStringListFieldDirectiveController
            };
        }

        function wsStringListFieldDirectiveController($scope, $attrs) {
            var vm = this;
            vm.label = watch('label', '');
            vm.desc = watch('desc', null);
            vm.readonly = watchBoolean('readonly', false);
            vm.addString = addString;
            vm.deleteString = deleteString;
            vm.helpVisible = [];
            vm.openHelper = openHelper;
            vm.toggleHelper = toggleHelper;
            if (vm.value == null) {
                vm.value = [''];
            }

            $scope.$on('wsHelperClose', function (evt, ctrl, index) {
                for (var i = 0; i < vm.helpVisible.length; i++) {
                    if (vm !== ctrl || index !== i) {
                        vm.helpVisible[i] = false;
                    }
                }
            });

            function toggleHelper(index) {
                if (vm.variables != null) {
                    vm.helpVisible[index] = vm.helpVisible == null ? true : !vm.helpVisible[index];
                    if (vm.helpVisible[index]) {
                        $scope.$emit('wsHelperOpened', vm, index);
                    }
                }
            }

            function openHelper(index) {
                if (vm.variables != null) {
                    vm.helpVisible[index] = true;
                    $scope.$emit('wsHelperOpened', vm, index);
                }
            }

            function addString() {
                vm.value.push('');
            }

            function deleteString(index) {
                vm.value.splice(index, 1);
            }

            function watchBoolean(name, defaultVal) {
                $scope.$watch(function () {
                    var val = resolve(name, defaultVal);
                    return  val !== false && val !== 'false';
                }, function (newValue) {
                    vm[name] = newValue;
                });
            }

            function watch(name, defaultVal) {
                $scope.$watch(function () {
                    return resolve(name, defaultVal);
                }, function (newValue) {
                    vm[name] = newValue;
                });
            }

            function resolve(name, defaultVal) {
                if ($attrs[name] != null) {
                    return $attrs[name];
                } else if (vm.config != null && vm.config[name] != null) {
                    return vm.config[name];
                } else {
                    return defaultVal;
                }
            }
        }

        wsStringListFieldDirectiveController.$inject = ['$scope', '$attrs'];

        return wsStringListFieldDirective;
    });
})(adminConsole.define);