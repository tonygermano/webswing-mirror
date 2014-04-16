'use strict';

/* Controllers */

angular.module('ws-console.controllers', [])
.controller('Application',['$scope','$location','atmosphereService','$http','$modal',function($scope,$location,atmosphereService,$http,$modal){
	$scope.loginForm={};
	var split=$location.url().split( '/' );
	$scope.location = split[1]==null?'dashboard':split[1];
	$scope.active = split[2]==null?'overview':split[2];
	var masterData=$scope.masterData = {};


	$scope.connected=false;
	$scope.login=function(){
		$http({
			method: 'POST', 
			url: '/login?role=admin',
			data    : $.param($scope.loginForm),
			headers : { 'Content-Type': 'application/x-www-form-urlencoded' }
		}).error(function(data, status, headers, config) {
			if($scope.loginDialog==null){
				$scope.loginDialog=$modal.open({
					templateUrl : 'partials/login.html',
					scope : $scope,
					backdrop : 'static',
					keyboard : 'false'
				});
			}
		}).success(function(data, status, headers, config) {
			if($scope.loginDialog!=null){
				$scope.loginDialog.close();
				$scope.loginDialog=null;
			}
			$scope.socket=atmosphereService.subscribe({
				url : '/async/admin',
				contentType : "application/json",
				logLevel : 'debug',
				transport :  'websocket',
				trackMessageLength : true,
				reconnectInterval : 5000,
				fallbackTransport : 'long-polling',
				onMessage: function(response){
					var message = response.responseBody;
					try {
						var data = atmosphere.util.parseJSON(message);
						masterData.currentCfg = data.configuration != null ? data.configuration : masterData.currentCfg;
						masterData.liveCfg = data.liveConfiguration != null ? data.liveConfiguration : masterData.liveCfg;
						masterData.sessions = data.sessions != null ? data.sessions : masterData.sessions;
						masterData.closedSessions = data.closedSessions != null ? data.closedSessions : masterData.closedSessions;
						masterData.serverProps = data.serverProperties != null ? data.serverProperties : masterData.serverProps;
						masterData.configurationBackup = data.configurationBackup != null ? data.configurationBackup : masterData.configurationBackup;
						masterData.userConfig = data.userConfig != null ? data.userConfig : masterData.userConfig;
					} catch (e) {
						return;
					}
				},
				onOpen : function(response){
					$scope.connected=true;
				},
				onClose : function(response){
					$scope.connected=false;
				}
			});
});
};
$scope.login();
}])
.controller('Dashboard', [function() {

}])
.controller('Settings', ['$scope',function($scope) {
	//edit
	$scope.config={};
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
		$scope.config.form = $scope.masterData.currentCfg;
		$scope.config.json = JSON.stringify($scope.masterData.currentCfg,undefined,2);
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
	
	$scope.resetUsers = function(){
		$scope.config.users=$scope.masterData.userConfig;
	}

	$scope.applyUsers = function(){
	}
	
	$scope.reset();
	$scope.resetUsers();

}]);
