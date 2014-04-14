'use strict';

/* Controllers */

angular.module('ws-console.controllers', [])
.controller('Application',['$scope','$location',function($scope,$location){
	var split=$location.url().split( '/' );
	$scope.location = split[1]==null?'dashboard':split[1];
	$scope.active = split[2]==null?'overview':split[2];
	$scope.currentCfg={  "swingDebugEnabled" : false,  "applications" : [ {    "name" : "SwingSet3",    "icon" : null,    "mainClass" : "com.sun.swingset3.SwingSet3",    "classPathEntries" : [ "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar" ],    "vmArgs" : "-Xmx128m",    "args" : "",    "homeDir" : "F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target",    "maxClients" : 1,    "antiAliasText" : true,    "swingSessionTimeout" : 300  }, {    "name" : "Printing",    "icon" : null,    "mainClass" : "Main",    "classPathEntries" : [ "print.jar" ],    "vmArgs" : "",    "args" : "",    "homeDir" : "f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print",    "maxClients" : 1,    "antiAliasText" : true,    "swingSessionTimeout" : 300  } ]};
	$scope.liveCfg= {  "swingDebugEnabled" : false,  "applications" : [ {    "name" : "SwingSet3",    "icon" : null,    "mainClass" : "com.sun.swingset3.SwingSet3",    "classPathEntries" : [ "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar" ],    "vmArgs" : "-Xmx128m",    "args" : "",    "homeDir" : "F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target",    "maxClients" : 1,    "antiAliasText" : true,    "swingSessionTimeout" : 300  }, {    "name" : "Printing",    "icon" : null,    "mainClass" : "Main",    "classPathEntries" : [ "print.jar" ],    "vmArgs" : "",    "args" : "",    "homeDir" : "f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print",    "maxClients" : 1,    "antiAliasText" : true,    "swingSessionTimeout" : 300  } ]};
	$scope.sessions= {  "sessions" : [ {    "id" : "31d113c8-4ac-7eb0-d1fd-df2fd45bf1b5Printing",    "user" : null,    "application" : "Printing",    "startedAt" : 1397496609962,    "connected" : true,    "disconnectedSince" : null,    "state" : {      "snapshotTime" : 1397496611386,      "heapSize" : 116.0,      "heapSizeUsed" : 23.0    }  } ],  "closedSessions" : [ {    "id" : "f235bfe9-99d1-74b3-7b6-868078a6e666Printing",    "user" : null,    "application" : "Printing",    "startedAt" : 1397496505901,    "connected" : true,    "disconnectedSince" : null,    "state" : {      "snapshotTime" : 1397496527271,      "heapSize" : 116.0,      "heapSizeUsed" : 21.0    }  }, {    "id" : "f235bfe9-99d1-74b3-7b6-868078a6e666SwingSet3",    "user" : null,    "application" : "SwingSet3",    "startedAt" : 1397496480106,    "connected" : true,    "disconnectedSince" : null,    "state" : {      "snapshotTime" : 1397496521795,      "heapSize" : 109.0,      "heapSizeUsed" : 43.0    }  }, {    "id" : "31d113c8-4ac-7eb0-d1fd-df2fd45bf1b5Printing",    "user" : null,    "application" : "Printing",    "startedAt" : 1397496558115,    "connected" : true,    "disconnectedSince" : null,    "state" : {      "snapshotTime" : 1397496599551,      "heapSize" : 116.0,      "heapSizeUsed" : 15.0    }  }, {    "id" : "31d113c8-4ac-7eb0-d1fd-df2fd45bf1b5SwingSet3",    "user" : null,    "application" : "SwingSet3",    "startedAt" : 1397496535712,    "connected" : true,    "disconnectedSince" : null,    "state" : {      "snapshotTime" : 1397496617120,      "heapSize" : 122.0,      "heapSizeUsed" : 110.0    }  } ],  "configuration" : null,  "configurationBackup" : null};
}])
.controller('Dashboard', [function() {

}])
.controller('Settings', ['$scope',function($scope) {
	//overview 
	$scope.runtime={'host':'localhost','port':8080, 'configFile':'c:/configFile','tempFolder':'c:/tempFolder','jmsUrl':'url','warLocation':'warLocation'};
	//edit
	$scope.editType='form';
	$scope.$watch('editType', function(newValue, oldValue) {
		if(newValue == 'json'){
			$scope.config.json=JSON.stringify($scope.config.form,undefined,2);
		}else if(newValue == 'form'){
			$scope.config.form=angular.fromJson($scope.config.json);
		}
	});
	$scope.currentEditApplication=null;
	$scope.arrowPos={'top':18+'px'};
	$scope.reset= function(){
		$scope.config={
			form:$scope.currentCfg,
			json: JSON.stringify($scope.currentCfg,undefined,2),
			users: 'asdfasdfasdfasdf\nasdfasdfasfdadsf'
		};
	}
	$scope.edit= function(index){
		$scope.currentEditApplication=index;
		$scope.arrowPos={'top':18+index*55+'px'};
	}
	$scope.delete= function(index){
		if(index==$scope.currentEditApplication){
			$scope.currentEditApplication=null;
		}
		$scope.config.form.applications.splice(index,1);
	}
	$scope.create= function(){
		$scope.config.form.applications.push({'name': '','homeDir': '.','classPathEntries':[''],'maxClients': 1,'swingSessionTimeout': 300,'antiAliasText': true });	
	}

	$scope.deleteCp= function(index){
		$scope.config.form.applications[$scope.currentEditApplication].classPathEntries.splice(index,1);
	}
	$scope.createCp= function(){
		$scope.config.form.applications[$scope.currentEditApplication].classPathEntries.push('');
	}

	$scope.currentApp=function(){
		if($scope.currentEditApplication!=null){
			return $scope.config.form.applications[$scope.currentEditApplication];
		}
	} 
	$scope.save= function(){
		//TODO
	}
	$scope.apply= function(){
		//TODO
	}
	
	$scope.reset();

}]);
