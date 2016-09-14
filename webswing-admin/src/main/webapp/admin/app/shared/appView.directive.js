(function(define) {
	define([ 'text!shared/appView.template.html' ], function f(htmlTemplate) {
		function wsAppViewDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					path : '=',
					reload : '=',
					detail : '@'
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsAppViewDirectiveController
			};
		}

		function wsAppViewDirectiveController($scope, $element, $attrs, $location, $timeout, configRestService, permissions, wsUtils) {
			var vm = this;
			vm.permissions = permissions;
			vm.b64img = '';
			vm.start = start;
			vm.stop = stop;
			vm.remove = remove;
			vm.viewSessions = viewSessions;
			vm.viewConfig = viewConfig;
			vm.panelStatusClass = panelStatusClass;
			vm.usageData = [ 0, 0, 0 ];
			vm.usageOptions = {
				thickness : 10
			};
			vm.lastUpdated = null;
			vm.refresh = refresh;
			vm.timer = undefined;
			vm.viewSession = viewSession;

			refresh();

			$scope.$on('$destroy', function() {
				$timeout.cancel(vm.timer);
			});

			$scope.$watch('vm.startable', function(value) {
				$scope.$emit('wsStatusChanged', vm);
			});

			function refresh() {
				return configRestService.getInfo(vm.path).then(function(data) {
					$timeout.cancel(vm.timer);
					vm.value = data;
					vm.b64img = 'data:image/png;base64,' + data.icon;
					vm.stoppable = data.status.status === 'Running';
					vm.startable = data.status.status === 'Stopped' | data.status.status === 'Error';
					vm.hasWarnings = wsUtils.getKeys(data.warnings).length > 0;
					vm.usageData = getUsageData(data);
					vm.memoryStats = getMemoryStats(data.stats);
					vm.cpuStats = getCpuStats(data.stats);
					vm.bandwidthStats = getBandwidthStats(data.stats);
					vm.latencyStats = getLatencyStats(data.stats);
					vm.configOptions = getConfigOptions(data);
					vm.lastUpdated = new Date();
				}).then(function() {
					vm.timer = $timeout(refresh, 5000);
					return vm.timer;
				}, function() {
					$timeout.cancel(vm.timer);
					vm.timer = undefined;
				});
			}

			function viewSessions() {
				$location.path('/dashboard/overview' + vm.path);
			}

			function viewConfig() {
				$location.path('/config/swing' + vm.path);
			}

			function viewSession(id) {
				$timeout(function() {
					$location.search('id', id);
					$location.path('/dashboard/session' + vm.path);
				}, 500);
			}

			function start() {
				configRestService.start(vm.path);
				vm.value.status.status = 'Requesting Start';
				vm.stoppable = false;
				vm.startable = false;
			}

			function stop() {
				configRestService.stop(vm.path);
				vm.value.status.status = 'Requesting Stop';
				vm.stoppable = false;
				vm.startable = false;
			}

			function remove() {
				configRestService.remove(vm.value.path).then(function() {
					if (vm.refresh != null) {
						$location.path('/dashboard');
						if (vm.reload != null) {
							vm.reload();
						}
					}
				});
				vm.value.status.status = 'Uninstalling Application';
				vm.stoppable = false;
				vm.startable = false;
			}

			function getUsageData(newValue) {
				var connected = newValue.connectedInstances;
				var disconnected = newValue.runningInstances - connected;
				var available = newValue.maxRunningInstances - connected - disconnected;
				return [ {
					label : 'Connected',
					value : connected,
					color : '#5cb85c'
				}, {
					label : 'Disconnected',
					value : disconnected,
					color : '#f0ad4e'
				}, {
					label : 'Available',
					value : available,
					color : '#777'
				} ];
			}

			function getMemoryStats(stats) {
				return {
					names : [ 'Total Allocated Memory', 'Total Used Memory' ],
					keys : [ 'memoryAllocated.SUM', 'memoryUsed.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'memoryAllocated.SUM', 'memoryUsed.SUM' ]),
					tickFormat : function(value, index) {
						return (value) + 'MB';
					}
				};
			}

			function getBandwidthStats(stats) {
				return {
					names : [ 'Total Inbound trafic', 'Total Outbound trafic' ],
					keys : [ 'inboundSize.SUM', 'outboundSize.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'inboundSize.SUM', 'outboundSize.SUM' ]),
					tickFormat : function(value, index) {
						return Math.floor(value / 1024) + 'k';
					}
				};
			}

			function getLatencyStats(stats) {
				return {
					names : [ 'Avg Network latency', 'Avg Server rendering latency', 'Avg Client rendering latency', 'Max End-to-End Latency' ],
					keys : [ 'latencyNetwork.AVG', 'latencyServerRendering.AVG', 'latencyClientRendering.AVG', 'latency.MAX' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'latencyNetwork.AVG', 'latencyServerRendering.AVG', 'latencyClientRendering.AVG', 'latency.MAX' ]),
					tickFormat : function(value, index) {
						return value + 'ms';
					}
				};
			}

			function getCpuStats(stats) {
				return {
					names : [ 'Total CPU Utilization' ],
					keys : [ 'cpuUtilization.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'cpuUtilization.SUM' ]),
					tickFormat : function(value, index) {
						return Math.floor(value) + '%';
					}
				};
			}

			function panelStatusClass(className) {
				if (vm.value.status != null) {
					var s = vm.value.status.status;
					if (s === 'Running') {
						return className + '-success';
					}
					if (s === 'Stopped') {
						return className + '-default';
					}
					if (s === 'Error') {
						return className + '-danger';
					}
					return className + '-warning';
				}
			}

			function getConfigOptions(newValue) {
				var c = newValue.config;
				var result = {
					"Home Folder" : c.homeDir,
					"Web Folder" : c.webFolder,
					"Security Module" : c.security.module,
				}
				if (c.swingConfig != null) {
					result["Type"] = c.swingConfig.launcherType;
					result["DirectDraw"] = c.swingConfig.directdraw;
					result["Theme"] = c.swingConfig.theme;
					result["Session mode"] = c.swingConfig.sessionMode;
					result["Session timeout"] = c.swingConfig.swingSessionTimeout;
				}
				return result
			}

			function resolve(name, defaultVal) {
				if ($attrs[name] != null) {
					return $attrs[name];
				} else {
					return defaultVal;
				}
			}
		}

		wsAppViewDirectiveController.$inject = [ '$scope', '$element', '$attrs', '$location', '$timeout', 'configRestService', 'permissions', 'wsUtils' ];

		return wsAppViewDirective;
	});
})(adminConsole.define);