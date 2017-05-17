(function (root) {
    require.config({
        baseUrl: 'javascript',
        map: {
            '*': {
                'jquery': 'jquery-private'
            },
            'jquery-private': {
                'jquery': 'jquery'
            }
        }
    });
    var lang = localStorage.getItem("webswingLang") || (navigator.browserLanguage || navigator.language);
    require({locale: lang},['webswing'], function () {
    }, function (err) {
        var failedId = err.requireModules && err.requireModules[0];
        console.error(err);
        throw Error("Error while starting webswing in module '" + failedId + "'. Reason: " + err);
    });
})(this);
