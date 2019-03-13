(function (define) {
    define([], function f() {
        function SessionsController($scope, $timeout, $location, configRestService, sessionsRestService, $route, $routeParams, wsUtils, baseUrl) {
            var vm = this;
            vm.appFilter = $routeParams.app;
            vm.apps=[];
            vm.nodes=[];
            vm.nodeFilter=$routeParams.nodeId;
            vm.sortExp = 'startedAt';
            vm.sortReverse = true;
            vm.sortBy = sortBy;
            vm.sessions=[];
            vm.lastUpdated = null;
            vm.refresh = refresh;
            vm.getOsIcon = getOsIcon;
            vm.getBrowserIcon = getBrowserIcon;
			vm.filter = filter;
			vm.view = view;
			vm.kill = kill;
            vm.now = function () {
                return new Date().getTime();
            }

            refresh();
            
            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
            });
            
            function refresh() {
            	sessionsRestService.getSessions(vm.appFilter, true).then(function(data){
					vm.sessions = data.sessions;
					vm.lastUpdated = new Date();
            	}).then(function () {
                    vm.timer = $timeout(refresh, 5000);
                    return vm.timer;
                }, function () {
                    $timeout.cancel(vm.timer);
                    vm.timer = undefined;
                });
            	
            	sessionsRestService.getStats(vm.appFilter).then(function(data) {
            		vm.memoryStats = getMemoryStats(data);
					vm.cpuStats = getCpuStats(data);
					vm.bandwidthStats = getBandwidthStats(data);
					vm.latencyStats = getLatencyStats(data);
            	});
            	
				configRestService.getApps().then(function(data) {
					vm.apps = data;
				});
            }
            
			function filter(paramName, value) {
				$timeout.cancel(vm.timer);
				$routeParams[paramName] = value;
				$route.updateParams($routeParams);
			}
			
			function getMemoryStats(stats) {
				return {
					names : [ 'Total Max Memory', 'Total Used Memory' ],
					keys : [ 'memoryAllocated.SUM', 'memoryUsed.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'memoryAllocated.SUM', 'memoryUsed.SUM' ]),
					tickFormat : function(value, index) {
						if(value>999){
							return (value/1024).toFixed(1) + 'GB';
						}else{
							return (value) + 'MB';
						}
					}
				};
			}

			function getBandwidthStats(stats) {
				return {
					names : [ 'Total Inbound trafic', 'Total Outbound trafic' ],
					keys : [ 'inboundSize.SUM', 'outboundSize.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'inboundSize.SUM', 'outboundSize.SUM' ]),
					tickFormat : function(value, index) {
						if(value>(999*1024)){
							return (value/1024/1024).toFixed(1) + 'm';
						}else{
							return Math.floor(value / 1024) + 'k';
						}
					}
				};
			}

			function getLatencyStats(stats) {
				return {
					names : [ 'Avg Ping latency', 'Avg Server rendering latency', 'Avg Client rendering latency', 'Max End-to-End Latency' ],
					keys : [ 'latencyPing.AVG', 'latencyServerRendering.AVG', 'latencyClientRendering.AVG', 'latency.MAX' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'latencyPing.AVG', 'latencyServerRendering.AVG', 'latencyClientRendering.AVG', 'latency.MAX' ]),
					tickFormat : function(value, index) {
						return value + 'ms';
					}
				};
			}

			function getCpuStats(stats) {
				return {
					names : [ 'Sessions CPU Utilization', 'Server CPU Utilization', 'Total CPU Utilization' ],
					keys : [ 'cpuUtilizationSession.SUM', 'cpuUtilizationServer.SUM', 'cpuUtilization.SUM' ],
					dataset : wsUtils.getStatsDataset(stats, [ 'cpuUtilizationSession.SUM', 'cpuUtilizationServer.SUM', 'cpuUtilization.SUM' ]),
					tickFormat : function(value, index) {
						return Math.floor(value) + '%';
					}
				};
			}
			
            function getOsIcon(os) {
                if (os != null) {
                    if (os === 'Windows') {
                        return 'wsa-icon-windows';
                    }
                    if (os === 'Mac' || os === 'IPhone') {
                        return 'wsa-icon-apple';
                    }
                    if (os === 'Linux') {
                        return 'wsa-icon-linux';
                    }
                    if (os === 'Android') {
                        return 'wsa-icon-android';
                    }
                    return 'wsa-icon-unknown';
                }
            }

            function getBrowserIcon(b) {
                if (b != null) {
                    if (b.indexOf('IE') >= 0) {
                        return 'wsa-icon-ie';
                    }
                    if (b.indexOf('Safari') >= 0) {
                        return 'wsa-icon-safari';
                    }
                    if (b.indexOf('Opera') >= 0) {
                        return 'wsa-icon-opera';
                    }
                    if (b.indexOf('Chrome') >= 0) {
                        return 'wsa-icon-chrome';
                    }
                    if (b.indexOf('Firefox') >= 0) {
                        return 'wsa-icon-firefox';
                    }
                    return 'wsa-icon-unknown';
                }
            }
            
            function sortBy(exp) {
                if (vm.sortExp === exp) {
                    vm.sortReverse = !vm.sortReverse;
                } else {
                    vm.sortExp = exp;
                    vm.sortReverse = false;
                }
            }
            
            function kill(session) {
                return sessionsRestService.killSession(session.applicationUrl, session.id).then(function () {
                    refresh();
                });
            }

            function view(session) {
                window.open(baseUrl + '/admin/#/dashboard/session' + session.applicationUrl + '?id=' + session.id, '_blank');
            }

        }

        SessionsController.$inject = ['$scope', '$timeout', '$location', 'configRestService', 'sessionsRestService', '$route', '$routeParams', 'wsUtils', 'baseUrl'];

        return SessionsController;
    });
})(adminConsole.define);