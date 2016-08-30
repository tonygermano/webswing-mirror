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

		function wsAppViewDirectiveController($scope, $element, $attrs, $location, $timeout, configRestService, permissions) {
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
					vm.usageData = getUsageData(data);
					vm.memoryStats = getMemoryStats(data.stats);
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

			function start() {
				configRestService.start(vm.value.path);
				vm.value.status.status = 'Requesting Start';
				vm.stoppable = false;
				vm.startable = false;
			}

			function stop() {
				configRestService.stop(vm.value.path);
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
					keys: ['memoryAllocated.SUM',  'memoryUsed.SUM' ],
					dataset : getDataset(stats, ['memoryAllocated.SUM',  'memoryUsed.SUM' ]),
					tickFormat: function (value,index){
						return (value)+'MB';
					}
				};
			}

			function getBandwidthStats(stats) {
				return {
					names : [ 'Total Inbound trafic', 'Total Outbound trafic' ],
					keys : [ 'inboundSize.SUM', 'outboundSize.SUM' ],
					dataset : getDataset(stats, [ 'inboundSize.SUM', 'outboundSize.SUM' ]),
					tickFormat: function (value,index){
						return Math.floor(value/1024)+'k';
					}
				};
			}
			
			function getLatencyStats(stats){
				return {
					names : [ 'Avg Network latency', 'Avg Server rendering latency','Avg Client rendering latency' ],
					keys : [ 'latencyNetwork.AVG', 'latencyServerRendering.AVG','latencyClientRendering.AVG' ],
					dataset : getDataset(stats, [ 'latencyNetwork.AVG', 'latencyServerRendering.AVG','latencyClientRendering.AVG' ]),
					tickFormat: function (value,index){
						return value+'ms';
					}
				};
			}

			function getDataset(stats, names) {
				var result = [];
				var keysObj = {}
				for (var n = 0; n < names.length; n++) {
					if (stats[names[n]] != null) {
						for ( var item in stats[names[n]]) {
							keysObj[item] = null;
						}
					}
				}
				var keys = getKeys(keysObj).sort();
				for (var int = 0; int < keys.length; int++) {
					var key = keys[int];
					var entry = {
						x : new Date(parseInt(key))
					}
					for (var n = 0; n < names.length; n++) {
						var name = names[n];
						var dataset1 = stats[name];
						var value = dataset1 != null && dataset1[key] != null ? dataset1[key] : 0;
						entry[name] = value;
					}
					result.push(entry);
				}
				return result;
			}

			function getKeys(obj) {
				var keys = [];
				if (obj !== null) {
					for ( var key in obj) {
						if (obj.hasOwnProperty(key)) {
							keys.push(key);
						}
					}
				}
				return keys;
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

		wsAppViewDirectiveController.$inject = [ '$scope', '$element', '$attrs', '$location', '$timeout', 'configRestService', 'permissions' ];

		return wsAppViewDirective;
	});
})(adminConsole.define);