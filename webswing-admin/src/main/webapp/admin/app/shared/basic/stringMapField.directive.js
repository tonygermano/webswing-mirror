(function (define) {
    define(['text!shared/basic/stringMapField.template.html'], function f(htmlTemplate) {
        function wsStringMapFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    field: '=',
                    value: '=',
                    items: '=',
                    readonly: '=',
                    restricted: '@',
                    label: '@',
                    desc: '@',
                    path: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsStringMapFieldDirectiveController
            };
        }

        function wsStringMapFieldDirectiveController($scope, $attrs, configRestService) {
            var vm = this;
            vm.model = [];
            vm.addPair = addPair;
            vm.deletePair = deletePair;
            vm.helpVisible = [];
            vm.toggleHelper = toggleHelper;
            vm.toggleHelperClose = toggleHelperClose;
            vm.setChoice = setChoice;
            vm.resolvedKeyString = '';
            vm.resolvedValueString = '';

            if (vm.value == null) {
                vm.field.value = {};
            }

            $scope.$watch("vm.value", function (value) {
                vm.model.splice(0, vm.model.length);
                angular.forEach(value, function (value, key) {
                    vm.model.push({
                        key: key,
                        value: value
                    });
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
                configRestService
                    .resolve(vm.path, vm.field.variables, vm.model[index].key)
                    .then(function (responseData) {
                        vm.resolvedKeyString = responseData;
                    });
                configRestService
                    .resolve(vm.path, vm.variablesType, vm.model[index].value)
                    .then(function (responseData) {
                        vm.resolvedValueString = responseData;
                    });
                vm.helpVisible[index] = vm.helpVisible == null ? true : !vm.helpVisible[index];
                if (vm.helpVisible[index]) {
                    $scope.$emit('wsHelperOpened', vm, index);
                }
            }

            function toggleHelperClose() {
                $scope.$broadcast('wsHelperClose', null, null);
            }

            function addPair() {
                vm.model.push({
                    key: '',
                    value: ''
                });
            }

            function deletePair(index) {
                vm.model.splice(index, 1);
            }

            function setChoice(index, value) {
                vm.model[index].key = value;
            }

        }

        wsStringMapFieldDirectiveController.$inject = ['$scope', '$attrs', 'configRestService'];

        return wsStringMapFieldDirective;
    });
})(adminConsole.define);