(function (define) {
    define(['text!shared/basic/stringMapField.template.html'], function f(htmlTemplate) {
        function wsStringMapFieldDirective() {
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
                controller: wsStringMapFieldDirectiveController
            };
        }

        function wsStringMapFieldDirectiveController($scope, $attrs) {
            var vm = this;
            vm.model = [];
            vm.label = watch('label', '');
            vm.desc = watch('desc', null);
            vm.readonly = watchBoolean('readonly', false);
            vm.addPair = addPair;
            vm.deletePair = deletePair;
            vm.helpVisible = [];
            vm.openHelper = openHelper;
            vm.toggleHelper = toggleHelper;

            $scope.$watch("vm.value", function (value) {
                vm.model.splice(0, vm.model.length);
                angular.forEach(value, function (value, key) {
                    vm.model.push({key: key, value: value});
                });
            }, true);

            $scope.$watch("vm.model", function (model) {
                var newValue = {};
                var valid = true;
                for (var i = 0; i < model.length; i++) {
                    if (!newValue.hasOwnProperty(model[i].key)) {
                        newValue[model[i].key] = model[i].value;
                    } else {
                        valid = false;
                        model[i].error = 'Duplicate keys are not allowed! This field is ignored in output json.';
                    }
                }
                if (valid) {
                    vm.value = newValue;
                }
            }, true);


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

            function addPair() {
                vm.model.push({key: '', value: ''});
            }

            function deletePair(index) {
                vm.model.splice(index, 1);
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

        wsStringMapFieldDirectiveController.$inject = ['$scope', '$attrs'];

        return wsStringMapFieldDirective;
    });
})(adminConsole.define);