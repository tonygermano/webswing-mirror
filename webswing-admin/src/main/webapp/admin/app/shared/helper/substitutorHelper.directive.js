(function (define) {
    define([], function f(htmlTemplate) {
        function wsSubstitutorHelperDirective(Textcomplete, configRestService) {
            return {
                restrict: 'A',
                scope: {
                    variablesType: '=',
                    message: '=',
                    path: '='
                },
                link: function (scope, iElement, iAttrs) {
                    var textcomplete = new Textcomplete(iElement, [{
                        match: /\$\{([.\w]*)$/,
                        search: function (term, callback) {
                            if(scope.variablesType!=null){
                                configRestService
                                    .getVariables(scope.path, scope.variablesType, term)
                                    .then(function (responseData) {
                                        callback(Object.entries(responseData));
                                    });
                            }else{
                                callback([]);
                            }
                        },
                        index: 1,
                        replace: function (mention) {
                            return '${' + mention[0] + '}';
                        },
                        template: function (value) {
                            return '<div class="ws-subs-key">${' + value[0] + '}</div><div class="ws-subs-value">' + value[1] + '</div>';
                        }
                    }]);

                    $(textcomplete).on({
                        'textComplete:select': function (e, value) {
                            scope.$apply(function () {
                                scope.message = value
                            })
                        },
                        'textComplete:show': function (e) {
                            $(this).data('autocompleting', true);
                        },
                        'textComplete:hide': function (e) {
                            $(this).data('autocompleting', false);
                        }
                    });
                },
                controller: wsSubstitutorHelperDirectiveController
            }
        }

        function wsSubstitutorHelperDirectiveController($scope) {
            var vm = $scope.vm = {};
        }

        wsSubstitutorHelperDirectiveController.$inject = ['$scope'];
        wsSubstitutorHelperDirective.$inject = ['Textcomplete', 'configRestService'];

        return wsSubstitutorHelperDirective;
    });
})(adminConsole.define);