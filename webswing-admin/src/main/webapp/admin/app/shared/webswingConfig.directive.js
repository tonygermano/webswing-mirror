define(['text!shared/webswingConfig.template.html'], function f(htmlTemplate) {
    function wsWebswingConfigDirective() {
        return {
            restrict: 'E',
            template: htmlTemplate,
            scope: {
                config: '=',
                variables: '='
            },
            controllerAs: 'vm',
            bindToController: true,
            controller: wsWebswingConfigDirectiveController
        };
    }

    function wsWebswingConfigDirectiveController($scope, $element, $attrs, configRestService) {
        var vm = this;
        vm.readonly = watchBoolean('readonly', false);
        vm.json = updateJson();
        vm.aceLoaded = aceLoaded;
        vm.updateJson = updateJson;
        vm.newApp = newApp;
        vm.newApplet = newApplet;

        $scope.$watch('vm.json', function (value) {
            try {
                var newConfig = angular.fromJson(value);
                angular.merge(vm.config, newConfig);
            } catch (e) {
            }
        }, true);

        $scope.$on('wsApplicationOpened', function (evt, data, i) {
            $scope.$broadcast('wsApplicationClose', data, i);
        });

        function newApplet() {
            if (vm.config.applets == null) {
                vm.config.applets = [];
            }
            configRestService.getDefault('applet').then(function (defaultApplet) {
                vm.config.applets.push(defaultApplet);
            });
        }

        function newApp() {
            if (vm.config.applications == null) {
                vm.config.applications = [];
            }
            configRestService.getDefault('application').then(function (defaultApp) {
                vm.config.applications.push(defaultApp);
            });
        }

        function updateJson() {
            vm.json = angular.toJson(vm.config, true);
        }

        function aceLoaded(editor) {
            editor.setReadOnly(vm.readonly);
            $scope.$watch("vm.readonly", function (value) {
                editor.setReadOnly(value);
            });
            $scope.$watch('vm.config', function () {
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

    wsWebswingConfigDirectiveController.$inject = ['$scope', '$element', '$attrs', 'configRestService'];

    return wsWebswingConfigDirective;
});