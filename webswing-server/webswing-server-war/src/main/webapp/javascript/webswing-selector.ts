import Util from "./webswing-util"
import Translate from "./webswing-translate";
import {loadTranslations,getParam} from "./webswing-tools";

loadTranslations().then(
    function(t){
        var translateModule = new (Translate as any)(t.msg, t.langs);
        translateModule.ready();
        var translate = translateModule.provides.translate;
        var util = Util(t);
        if(!util.checkCookie()){
            $('#webswing-content').html(translate('${dialog.cookiesDisabledDialog}'));
            return;
        }

        var login = util.webswingLogin;
        var user:any;
        var loginData = {
            securityToken: getParam("token"),
            realm: getParam("realm")
        };
        var url = window.location.href;
        var baseUrl = url.substring(0, Math.min(url.indexOf('#'), url.indexOf('?')));
        login(baseUrl, $('#webswing-content'), loginData, loadApps);


        // @ts-ignore
        function loadApps(data:any, request:any) {
            user = request.getResponseHeader('webswingUsername');
            $.ajax({
                xhrFields: {
                    withCredentials: true
                },
                type: 'GET',
                url: 'apps',
                success: function (data) {
                    loadPermissions(function (showAdmin:any) {
                    	if (showAdmin) {
                    		loadAdminConsoleUrl(function(adminConsoleUrl:any) {
                    			if (adminConsoleUrl && adminConsoleUrl.length) {
                    				show(data, true, adminConsoleUrl);
                    			} else {
                    				show(data, false);
                    			}
                    		});
                    	} else {
                    		show(data, false);
                    	}
                    });
                }
            });
        }
        
        function loadAdminConsoleUrl(callback:any) {
        	$.ajax({
        		xhrFields: {
        			withCredentials: true
        		},
        		type: 'GET',
        		url: 'rest/adminConsoleUrl',
        		success: function (data) {
        			callback(data);
        		},
        		error: function () {
        			callback(false);
        		}
        	});
        }

        function loadPermissions(callback:any) {
            $.ajax({
                xhrFields: {
                    withCredentials: true
                },
                type: 'GET',
                url: 'rest/permissions',
                success: function (data) {
                    callback(data && data.dashboard);
                },
                error: function () {
                    callback(false);
                }
            });
        }


        function show(apps:any, showAdmin:any, adminConsoleUrl?:string) {
        	if (adminConsoleUrl && adminConsoleUrl.indexOf("http") == 0) {
        		// if admin console is located on a different server send current webswing server URL as a parameter
        		adminConsoleUrl += "?url=" + encodeURIComponent(window.location.href);
        	}
            var header:any = '<h1 id="commonDialog-title" class="ws-selector-title">${selector.welcome} <span>' + user + '</span>${selector.message}</h1>';
            var links = '${selector.lang} '+(showAdmin ? ' <a href="' + adminConsoleUrl + '" id="admin">${selector.admin}</a> | ' : '') + '<a href="logout" id="logout">${selector.logout}</a>';
            var content;
            $('#commonDialog').addClass('ws-selector')
            if (apps == null || apps.length === 0) {
                header = null;
                content = '<p id="commonDialog-title">${selector.noApp}</p>';
            } else {
                content = '<div class="ws-selector-container">';
                var counter = 0;
                for (var i in apps) {
                    var app = apps[i];
                    content += '<div class="ws-selector-btn">'
                        + '<a href="' + app.url + '" role="button" aria-labelledby="selector-btn-' + counter + '">'
                        + '<img src="' + util.getImageString(app.base64Icon) + '" class="ws-selector-btn-thumb"/>'
                        + '<div id="selector-btn-' + counter + '" class="ws-selector-btn-label">' + app.name + '</div>'
                        + '</a></div>';
                    counter++;
                }
                content += '</div>';
            }
            $('#webswing-header').html(translate(header));
            $('#webswing-links').html(translate(links));
            $('#webswing-content').html(translate(content));
        }
    }
).catch(function (e){
    console.error("Failed to load Translations",e);
})

