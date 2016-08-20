(function (define) {
    define([
        'shared/appView.directive',
        'shared/configView.directive',
        'shared/fieldView.directive',
        'shared/basic/stringField.directive',
        'shared/basic/stringListField.directive',
        'shared/basic/booleanField.directive',
        'shared/basic/stringMapField.directive',
        'shared/basic/substitutorHelper.directive',
        'shared/basic/substitutor.filter'
    ], function f(appView, configView, fieldView, stringField, stringListField, booleanField,wsStringMapField, substitutorHelper, substitutorFilter) {
        var module = angular.module('wsShared', []);

        module.directive('wsAppView',appView)
        module.directive('wsConfigView',configView)
        module.directive('wsFieldView',fieldView)
        module.directive('wsStringField', stringField);
        module.directive('wsStringListField', stringListField);
        module.directive('wsBooleanField', booleanField);
        module.directive('wsStringMapField', wsStringMapField);
        module.directive('wsSubstitutorHelper', substitutorHelper);
        module.filter('substitutor', substitutorFilter);

    });
})(adminConsole.define);