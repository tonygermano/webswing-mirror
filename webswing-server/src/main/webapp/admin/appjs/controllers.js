'use strict';

/* Controllers */

angular.module('ws-console.controllers', [])
	.controller('Application', ['$scope', '$location', 'atmosphereService', '$http', '$modal', '$timeout',
		function($scope, $location, atmosphereService, $http, $modal, $timeout) {
			$scope.messages = [];
			$scope.closeMessage = function(index) {
				$timeout.cancel($scope.messages[index].timer);
				$scope.messages.splice(index, 1);
			};
			$scope.addMessage = function(data) {
				if ($scope.messages.length >= 3) {
					$scope.closeMessage(0);
				}
				data.timer = $timeout(function(data) {
					var i = $scope.messages.indexOf(data);
					$scope.messages.splice(i, 1);
				}, 10000);
				$scope.messages.push(data);
			};

			$scope.loginForm = {};
			var split = $location.url().split('/');
			$scope.location = split[1] == null ? 'dashboard' : split[1];
			$scope.active = split[2] == null ? 'overview' : split[2];
			var masterData = $scope.masterData = {};
			$scope.handleLoginEnter = function(event) {
				if (event.keyCode == 13) {
					$scope.login();
				}
			}

			$scope.config = {};

			$scope.connected = false;
			$scope.loginErrorMsg = null;
			$scope.login = function() {
				$http({
					method: 'POST',
					url: document.location.toString().substring(0,document.location.toString().lastIndexOf('admin'))+'login?role=admin',
					data: $.param($scope.loginForm),
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					}
				}).error(function(data, status, headers, config) {
					if ($scope.loginDialog == null) {
						$scope.loginDialog = $modal.open({
							templateUrl: 'partials/login.html',
							scope: $scope,
							backdrop: 'static',
							keyboard: false
						});
					} else {
						$scope.loginErrorMsg = data;
					}
				}).success(function(data, status, headers, config) {
					$scope.loginErrorMsg = null;
					if ($scope.loginDialog != null) {
						$scope.loginDialog.close();
						$scope.loginDialog = null;
					}
					$scope.socket = atmosphereService.subscribe({
						url: document.location.toString().substring(0,document.location.toString().lastIndexOf('admin'))+'async/admin',
						contentType: "application/json",
						logLevel: 'debug',
						transport: 'websocket',
						trackMessageLength: true,
						reconnectInterval: 5000,
						fallbackTransport: 'long-polling',
						onMessage: function(response) {
							var message = response.responseBody;
							try {
								var data = atmosphere.util.parseJSON(message);
								if (data.type == 'admin') {
									masterData.currentCfg = data.configuration != null ? data.configuration : masterData.currentCfg;
									if ($scope.config.form == null) {
										$scope.config.form = angular.copy($scope.masterData.currentCfg);
									}
									masterData.liveCfg = data.liveConfiguration != null ? data.liveConfiguration : masterData.liveCfg;
									masterData.sessions = data.sessions != null ? data.sessions : masterData.sessions;
									masterData.closedSessions = data.closedSessions != null ? data.closedSessions : masterData.closedSessions;
									masterData.serverProps = data.serverProperties != null ? data.serverProperties : masterData.serverProps;
									masterData.configurationBackup = data.configurationBackup != null ? data.configurationBackup : masterData.configurationBackup;
									masterData.userConfig = data.userConfig != null ? data.userConfig : masterData.userConfig;
									if ($scope.config.users == null) {
										$scope.config.users = angular.copy($scope.masterData.userConfig);
									}
									if (data.message != null) {
										$scope.addMessage(data.message);
									}
								}
							} catch (e) {
								return;
							}
						},
						onOpen: function(response) {
							$scope.connected = true;
						},
						onClose: function(response) {
							$scope.connected = false;
						}
					});
				});
			};
			$scope.login();
		}
	])


.controller('Dashboard', ['$scope', '$modal', '$timeout',
	function($scope, $modal, $timeout) {
		$scope.$watch('masterData.sessions | filter:{id:mirrorSession.id}', function(newValue, oldValue) {
			if (typeof newValue != 'undefined') {
				if (newValue.length > 0) {
					$scope.mirrorSession = newValue[0];
				} else if (newValue.length == 0 && $scope.mirrorDialog != null) {
					$scope.mirrorDialog.close();
				}
			}
		}, true);
		$scope.$watch('config.control', function(newValue, oldValue) {
			if (window.webswingadmin!=null && window.webswingadmin.webswingmirrorview != null && newValue!=null) {
				window.webswingadmin.webswingmirrorview.setControl(newValue);
			}
		});
		$scope.view = function(session) {
			$scope.mirrorSession = session;
			$scope.mirrorDialog = $modal.open({
				templateUrl: 'partials/dashboard/mirror-viewer.html',
				scope: $scope,
				backdrop: 'static',
				keyboard: false,
				windowClass: 'mirror-view-modal-dialog'
			});
			$scope.closeMirrorView = function() {
				$scope.mirrorDialog.close();
			}
			$scope.mirrorDialog.opened.then(function() {
				$scope.config={
					autoStart : false,
					connectionUrl: document.location.toString().substring(0,document.location.toString().lastIndexOf('admin')),
					clientId: session.id,
					applicationName: session.application,
					control: false,
					mirrorMode: true
				};
				$timeout(function() {
					window.webswingadmin.scan();
					window.webswingadmin.webswingmirrorview.disconnect();
					window.webswingadmin.webswingmirrorview.configure($scope.config);
					window.webswingadmin.webswingmirrorview.start();
				}, 1000);
			});
			$scope.mirrorDialog.result.then(function() {
				window.webswingadmin.webswingmirrorview.disconnect();
			}, function() {});
		};
		$scope.kill = function(session) {
			if ($scope.mirrorDialog != null) {
				window.webswingadmin.webswingmirrorview.kill();
			}
		}
	}
])


.controller('Settings', ['$scope', 'base64Encoder',
	function($scope, base64Encoder) {

		// edit
		$scope.editType = 'form';
		$scope.$watch('config.form', function(newValue, oldValue) {
			if ($scope.editType == 'form') {
				$scope.config.json = angular.toJson(newValue, true);
			}
		}, true);
		$scope.$watch('config.json', function(newValue, oldValue) {
			if ($scope.editType == 'json') {
				$scope.config.form = angular.fromJson(newValue);
			}
		}, true);
		$scope.currentEditApplication = null;
		$scope.currentViewApplication = null;
		$scope.arrowPos = {
			'top': 18 + 'px'
		};
		$scope.reset = function() {
			if ($scope.editType == 'form') {
				$scope.config.form = angular.copy($scope.masterData.currentCfg);
			}else{
				$scope.config.json = angular.toJson(angular.copy($scope.masterData.currentCfg), true);
			}
		}
		$scope.edit = function(index) {
			$scope.currentEditApplication = index;
			$scope.arrowPos = {
				'top': 18 + index * 55 + 'px'
			};
		}
		$scope.view = function(index) {
			$scope.currentViewApplication = index;
			$scope.arrowPos = {
				'top': 18 + index * 55 + 'px'
			};
		}
		$scope.delete = function(index) {
			if (index == $scope.currentEditApplication) {
				$scope.currentEditApplication = null;
			}
			$scope.config.form.applications.splice(index, 1);
		}
		$scope.create = function() {
			$scope.config.form.applications.push({
				'name': '',
				'homeDir': '.',
				'classPathEntries': [''],
				'maxClients': 1,
				'swingSessionTimeout': 300,
				'antiAliasText': true,
				'authorization': false,
				'authentication': true,
				'isolatedFs' : true
			});
		}

		$scope.deleteCp = function(index) {
			$scope.config.form.applications[$scope.currentEditApplication].classPathEntries.splice(index, 1);
		}
		$scope.createCp = function() {
			$scope.config.form.applications[$scope.currentEditApplication].classPathEntries.push('');
		}

		$scope.currentApp = function() {
			if ($scope.currentEditApplication != null) {
				return $scope.config.form.applications[$scope.currentEditApplication];
			}
		}
		$scope.currentViewApp = function() {
			if ($scope.currentViewApplication != null) {
				return $scope.masterData.liveCfg.applications[$scope.currentViewApplication];
			}
		}
		$scope.apply = function() {
			var content = $scope.editType == 'form' ? angular.toJson($scope.config.form, true) : $scope.config.json;
			$scope.socket.push(atmosphere.util.stringifyJSON({
				type: 'config',
				configContent: base64Encoder().encode(content)
			}));
		}

		$scope.resetUsers = function() {
			$scope.config.users = angular.copy($scope.masterData.userConfig);
		}

		$scope.applyUsers = function() {
			$scope.socket.push(atmosphere.util.stringifyJSON({
				type: 'user',
				configContent: base64Encoder().encode($scope.config.users)
			}));
		}
	}
]);