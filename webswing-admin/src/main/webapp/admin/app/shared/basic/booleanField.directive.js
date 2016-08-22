(function (define) {
    define(['text!shared/basic/booleanField.template.html'], function f(htmlTemplate) {
        function wsBooleanFieldDirective() {
            return {
                restrict: 'E',
                template: htmlTemplate,
                scope: {
                    value: '=',
                    readonly: '=',
                    label: '@',
                    desc: '@'
                },
                controllerAs: 'vm',
                bindToController: true,
                controller: wsBooleanFieldDirectiveController
            };
        }

        function wsBooleanFieldDirectiveController($scope, $attrs) {
            var vm = this;
        }

        wsBooleanFieldDirectiveController.$inject = ['$scope', '$attrs'];

        return wsBooleanFieldDirective;
    });
})(adminConsole.define);