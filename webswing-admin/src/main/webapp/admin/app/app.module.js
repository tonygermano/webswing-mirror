(function (define) {
    define([
        'common/common.module',
        'shared/shared.module',
        'views/dashboard/dashboard.module',
        'views/settings/settings.module',
        'services/services.module'], function f() {
        var app = angular.module('wsAdmin', ['ngRoute', 'ui.bootstrap', 'ui.ace', 'ui.jq', 'wsCommon', 'wsShared', 'wsServices', 'wsDashboard', 'wsSettings']);
        app.value('baseUrl', getBaseLocation());

        function getBaseLocation() {
            var path = document.location.toString();
            path = path.lastIndexOf('#') > -1 ? path.substring(0, path.lastIndexOf('#')) : path;
            path = path.substring(0, path.lastIndexOf('/admin'));
            return path;
        }
    });
})(adminConsole.define);