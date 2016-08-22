(function (define) {
    define([
        'shared/appView.directive',
        'shared/field.directive',
        'shared/objectField.directive',
        'shared/objectListField.directive',
        'shared/objectMapField.directive',
        'shared/basic/jsonField.directive',
        'shared/basic/stringField.directive',
        'shared/basic/stringListField.directive',
        'shared/basic/booleanField.directive',
        'shared/basic/stringMapField.directive',
        'shared/basic/substitutorHelper.directive',
        'shared/basic/substitutor.filter'
    ], function f(appView, field, objectField, objectListField, objectMapField, jsonField, stringField, stringListField, booleanField,wsStringMapField, substitutorHelper, substitutorFilter) {
        var module = angular.module('wsShared', []);

        module.directive('wsAppView',appView)
        module.directive('wsField',field)
        module.directive('wsObjectField',objectField)
        module.directive('wsObjectListField',objectListField)
        module.directive('wsObjectMapField',objectMapField)
        module.directive('wsJsonField', jsonField);
        module.directive('wsStringField', stringField);
        module.directive('wsStringListField', stringListField);
        module.directive('wsBooleanField', booleanField);
        module.directive('wsStringMapField', wsStringMapField);
        module.directive('wsSubstitutorHelper', substitutorHelper);
        module.filter('substitutor', substitutorFilter);

    });
})(adminConsole.define);