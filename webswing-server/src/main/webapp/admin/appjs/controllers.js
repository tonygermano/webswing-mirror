'use strict';

/* Controllers */

angular.module('ws-console.controllers', [])
.controller('Application',['$scope','$location',function($scope,$location){
	var split=$location.url().split( '/' );
	$scope.location = split[1]==null?'dashboard':split[1];
	$scope.active = split[2]==null?'overview':split[2];
	$scope.currentCfg={"swingDebugEnabled" : false,	"applications" : {			"Printing" : {"icon" : null,"mainClass" : "Main","classPathEntries" : [ "print.jar" ],"vmArgs" : "","args" : "","homeDir" : "f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print",	"maxClients" : 1,"antiAliasText" : true},"SwingSet3" : {"icon" : null,"mainClass" : "com.sun.swingset3.SwingSet3",				"classPathEntries" : [ "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar" ],				"vmArgs" : "-Xmx128m",				"args" : "",				"homeDir" : "F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target",				"maxClients" : 1,				"antiAliasText" : true			}		}	};
	$scope.liveCfg= {"swingDebugEnabled" : false,	"applications" : {			"Printing" : {"icon" : null,"mainClass" : "Main","classPathEntries" : [ "print.jar" ],"vmArgs" : "","args" : "","homeDir" : "f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print",	"maxClients" : 1,"antiAliasText" : true},"SwingSet3" : {"icon" : null,"mainClass" : "com.sun.swingset3.SwingSet3",				"classPathEntries" : [ "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar" ],				"vmArgs" : "-Xmx128m",				"args" : "",				"homeDir" : "F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target",				"maxClients" : 1,				"antiAliasText" : true			}		}	};
}])
.controller('Dashboard', [function() {

}])
.controller('Settings', ['$scope',function($scope) {
	$scope.editType='form';
	$scope.$watch('editType', function(newValue, oldValue) {
		if(newValue == 'json'){
			$scope.config.json=JSON.stringify($scope.config.form,undefined,2);
		}else if(newValue == 'form'){
			$scope.config.form=angular.fromJson($scope.config.json);
		}
	});
	
	$scope.reset= function(){
		$scope.config={
			form:$scope.currentCfg,
			json: JSON.stringify($scope.currentCfg,undefined,2)
		};
	}
	$scope.reset();
}]);
