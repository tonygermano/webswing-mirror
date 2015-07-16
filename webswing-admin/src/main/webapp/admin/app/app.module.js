define(['common/common.module', 'shared/shared.module', 'views/dashboard/dashboard.module', 'views/settings/settings.module',
    'services/services.module'], function f() {
    angular.module('wsAdmin', ['ngRoute', 'ui.bootstrap', 'ui.ace', 'ui.jq', 'wsCommon', 'wsShared', 'wsServices', 'wsDashboard', 'wsSettings']);
});