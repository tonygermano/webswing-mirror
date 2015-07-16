define([
    'shared/stringField.directive',
    'shared/stringListField.directive',
    'shared/booleanField.directive',
    'shared/applicationView.directive',
    'shared/applicationList.directive',
    'shared/webswingConfig.directive',
    'shared/stringMapField.directive',
    'shared/substitutorHelper.directive',
    'shared/substitutor.filter'
], function f(stringField, stringListField, booleanField, applicationView, applicationList,webswingConfig, wsStringMapField, substitutorHelper, substitutorFilter) {
    var module = angular.module('wsShared', []);

    module.directive('wsStringField', stringField);
    module.directive('wsStringListField', stringListField);
    module.directive('wsBooleanField', booleanField);
    module.directive('wsApplicationView', applicationView);
    module.directive('wsApplicationList', applicationList);
    module.directive('wsWebswingConfig', webswingConfig);
    module.directive('wsStringMapField', wsStringMapField);
    module.directive('wsSubstitutorHelper', substitutorHelper);
    module.filter('substitutor', substitutorFilter);

});