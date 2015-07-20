(function (define) {
    define(['text!shared/booleanField.template.html'], function f(htmlTemplate) {
        function wsBooleanFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    value: '='
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsBooleanFieldDirectiveController
            };
        }

        function wsBooleanFieldDirectiveController($scope, $attrs) {
            var vm = this;
            vm.label = resolve('label', '');
            vm.desc = resolve('desc', null);
            vm.readonly = watchBoolean('readonly', false);

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

        wsBooleanFieldDirectiveController.$inject = ['$scope', '$attrs'];

        return wsBooleanFieldDirective;
    });
})(adminConsole.define);