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
                    variables: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                link: link,
                controller: controller
            };
        }

        function link(scope, element, attrs, ctrl) {

        }

        function controller($scope, $attrs) {
            var vm = this;
            vm.label = resolve('label', '');
            vm.desc = resolve('desc', null);
            vm.type = resolve('type', 'text');
            vm.readonly = watchBoolean('readonly', false);
            vm.required = watchBoolean('required', false);
            vm.requiredMsg = resolve('requiredMsg', 'This value is mandatory!');
            vm.helpVisible = false;
            vm.setChoice = setChoice;
            vm.openHelper = openHelper;
            vm.toggleHelper = toggleHelper;

            $scope.$on('wsHelperClose', function (evt, ctrl) {
                if (vm !== ctrl) {
                    vm.helpVisible = false;
                }
            });

            function toggleHelper() {
                if (vm.variables != null) {
                    vm.helpVisible = !vm.helpVisible;
                    if (vm.helpVisible) {
                        $scope.$emit('wsHelperOpened', vm);
                    }
                }
            }

            function openHelper() {
                if (vm.variables != null) {
                    vm.helpVisible = true;
                    $scope.$emit('wsHelperOpened', vm);
                }
            }

            function setChoice(value) {
                vm.value = value;
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
                } else {
                    return defaultVal;
                }
            }
        }

        controller.$inject = ['$scope', '$attrs'];

        return wsStringFieldDirective;
    });
})(adminConsole.define);