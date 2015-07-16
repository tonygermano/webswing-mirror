define(['text!shared/substitutorHelper.template.html'], function f(htmlTemplate) {
    function wsSubstitutorHelperDirective() {
        return {
            restrict: 'E',
            template: htmlTemplate,
            scope: {
                variables: '='
            },
            link: function ($scope, elem, attrs, parentCtrl) {
                $scope.$watch(function () {
                    return $scope.variables;
                }, function (val) {
                    $scope.vm.data = formatData(val);
                }, true);

            },
            controller: wsSubstitutorHelperDirectiveController
        };
    }

    function wsSubstitutorHelperDirectiveController($scope) {
        var vm = $scope.vm = {};
        vm.filter = '';
    }

    function formatData(obj) {
        var result = [];
        for (key in obj) {
            var val = obj[key];
            result.push({
                'variable': '${' + key + '}',
                'value': val
            });
        }
        return result;
    }

    wsSubstitutorHelperDirectiveController.$inject = ['$scope'];

    return wsSubstitutorHelperDirective;
});