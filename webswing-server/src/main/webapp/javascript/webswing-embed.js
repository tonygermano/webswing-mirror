//THIS FILE IS USED FOR DEVELOPMENT ONLY. AFTER EXECUTING MAVEN BUILD THIS FILE IS REPLACED BY GENERATED ONE (by requirejs optimizer)
(function (window, document) {
    // fix for ie
    if (!window.location.origin) {
        window.location.origin = window.location.protocol + "//" + window.location.hostname
                + (window.location.port ? ':' + window.location.port : '');
    }
	var baseUrl = document.location.origin + document.location.pathname;
	baseUrl = baseUrl.indexOf("/", baseUrl.length - 1) !== -1 ? baseUrl : (baseUrl + "/");
    var script = document.createElement("script"), tag = document.getElementsByTagName("script")[0];
    script.src = baseUrl + "javascript/require.js";
    tag.parentNode.insertBefore(script, tag);
    script.onload = function () {
        var script2 = document.createElement("script");
        script2.src = "javascript/main.js";
        tag.parentNode.insertBefore(script2, tag);
    }

})(window, document);