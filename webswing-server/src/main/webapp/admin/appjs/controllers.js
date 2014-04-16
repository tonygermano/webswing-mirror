'use strict';

/* Controllers */

angular.module('ws-console.controllers', [])
.controller('Application',['$scope','$location','atmosphereService','$http','$modal',function($scope,$location,atmosphereService,$http,$modal){
	$scope.loginForm={};
	var split=$location.url().split( '/' );
	$scope.location = split[1]==null?'dashboard':split[1];
	$scope.active = split[2]==null?'overview':split[2];

	$scope.connected=false;
	$scope.login=function(data){
		$http({
			method: 'POST', 
			url: '/login?role=admin',
			data    : $.param($scope.loginForm),
			headers : { 'Content-Type': 'application/x-www-form-urlencoded' }
		}).error(function(data, status, headers, config) {
			if($scope.loginDialog==null){
				$scope.loginDialog=$modal.open({
					templateUrl : 'partials/login.html',
					backdrop : 'static',
					keyboard : 'false'
				});
			}
		}).success(function(data, status, headers, config) {
			$scope.loginDialog.close();
			$scope.loginDialog=null;
			atmosphereService.subscribe({
				url : document.location.toString() + 'async/admin',
				contentType : "application/json",
				logLevel : 'debug',
				transport :  'websocket',
				trackMessageLength : true,
				reconnectInterval : 5000,
				fallbackTransport : 'long-polling',
				onMessage: function(response){
					$scope.currentCfg = response.configuration != null ? response.configuration : $scope.currentCfg;
					$scope.liveCfg = response.liveConfiguration != null ? response.liveConfiguration : $scope.liveCfg;
					$scope.sessions = response.sessions != null ? response.sessions : $scope.sessions;
					$scope.closedSessions = response.closedSessions != null ? response.closedSessions : $scope.closedSessions;
					$scope.serverProps = response.serverProperties != null ? response.serverProperties : $scope.serverProps;
					$scope.configurationBackup = response.configurationBackup != null ? response.configurationBackup : $scope.configurationBackup;
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
