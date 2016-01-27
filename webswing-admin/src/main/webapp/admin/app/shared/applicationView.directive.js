(function (define) {
    define(['text!shared/applicationView.template.html'], function f(htmlTemplate) {
        function wsApplicationViewDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    app: '=',
                    variables: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsApplicationViewDirectiveController
            };
        }

        function wsApplicationViewDirectiveController($scope, $element, $attrs) {
            var vm = this;
            vm.readonly = watchBoolean('readonly', false);
            vm.applet = watchBoolean('applet', false);
            vm.json = updateJson();
            vm.aceLoaded = aceLoaded;
            vm.updateJson = updateJson;

            $scope.$watch('vm.json', function (value) {
                try {
                    var newConfig = angular.fromJson(value);
                    angular.merge(vm.app, newConfig);
                } catch (e) {
                }
            }, true);

            $scope.$on('wsHelperOpened', function (evt, data, i) {
                $scope.$broadcast('wsHelperClose', data, i);
            });

            
            function updateJson() {
                vm.json = angular.toJson(vm.app, true);
            }

            function aceLoaded(editor) {
                editor.setReadOnly(vm.readonly);
                $scope.$watch("vm.readonly", function (value) {
                    editor.setReadOnly(value);
                });
                $scope.$watch('vm.app', function () {
                    updateJson();
                    editor.resize(true);
                }, true);
            }

            function watchBoolean(name, defaultVal) {
                $scope.$watch(function () {
                    var val = resolve(name, defaultVal);
                    return  val !== false && val !== 'false';
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

        wsApplicationViewDirectiveController.$inject = ['$scope', '$element', '$attrs'];

        return wsApplicationViewDirective;
    });
})(adminConsole.define);