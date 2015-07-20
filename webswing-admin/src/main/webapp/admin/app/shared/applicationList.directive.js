(function (define) {
    define(['text!shared/applicationList.template.html'], function f(htmlTemplate) {
        function wsApplicationListDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    apps: '=',
                    config: '=',
                    variables: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsApplicationListDirectiveController
            };
        }

        function wsApplicationListDirectiveController($scope, $element, $attrs) {
            var vm = this;
            vm.readonly = watchBoolean('readonly', false);
            vm.visible = [];
            vm.openApp = openApp;
            vm.toggleApp = toggleApp;
            vm.deleteApp = deleteApp;

            $scope.$on('wsApplicationClose', function (evt, ctrl, index) {
                for (var i = 0; i < vm.visible.length; i++) {
                    if (vm !== ctrl || index !== i) {
                        vm.visible[i] = false;
                    }
                }
            });

            function toggleApp(index) {
                vm.visible[index] = !vm.visible[index];
                if (vm.visible[index]) {
                    $scope.$emit('wsApplicationOpened', vm, index);
                }
            }

            function openApp(index) {
                vm.visible[index] = true;
                $scope.$emit('wsApplicationOpened', vm, index);
            }

            function deleteApp(index) {
                vm.visible.splice(index, 1);
                vm.apps.splice(index, 1);
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

        wsApplicationListDirectiveController.$inject = ['$scope', '$element', '$attrs'];

        return wsApplicationListDirective;
    });
})(adminConsole.define);