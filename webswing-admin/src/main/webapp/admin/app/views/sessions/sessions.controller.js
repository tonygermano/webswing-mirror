(function (define) {
    define([], function f() {
        function SessionsController($scope, $timeout, $location, configRestService, sessionsRestService, logRestService, $route, $routeParams, wsUtils, baseUrl) {
            var vm = this;
            vm.appFilter = $routeParams.app;
        	if (vm.appFilter && vm.appFilter.substr(0, 1) !== '/') {
        		vm.appFilter = '/' + vm.appFilter;
            }
            vm.paths = [];
            vm.sortExp = 'startedAt';
            vm.sortReverse = true;
            vm.sortBy = sortBy;
            vm.isSortByAsc = isSortByAsc;
            vm.isSortByDesc = isSortByDesc;
            vm.getSortByMetricsExpr = getSortByMetricsExpr;
            vm.getSortByBandwidthExpr = getSortByBandwidthExpr;
            vm.getSortByLatencyExpr = getSortByLatencyExpr;
            vm.sortFinishedExp = 'endedAt';
            vm.sortFinishedReverse = true;
            vm.sortFinishedBy = sortFinishedBy;
            vm.isSortFinishedByAsc = isSortFinishedByAsc;
            vm.isSortFinishedByDesc = isSortFinishedByDesc;
            vm.searchAppFilter = '';
            vm.sessions=[];
            vm.closedSessions = [];
            vm.recordings = [];
            vm.log = [];
            vm.lastUpdated = null;
            vm.refresh = refresh;
            vm.getOsIcon = getOsIcon;
            vm.getBrowserIcon = getBrowserIcon;
			vm.filter = filter;
			vm.view = view;
			vm.record = record;
			vm.play = play;
			vm.playOther = playOther;
			vm.kill = kill;
			vm.sessionTab = 'running';
			vm.showThreadDump = showThreadDump;
			vm.requestThreadDump = requestThreadDump;
			vm.hasWarnings = hasWarnings;
			vm.logTimer = undefined;
            vm.logEndOffset = -1;
            vm.loadSessionLogs = loadSessionLogs;
            vm.stopLogs = stopLogs;
            vm.split = split;
            vm.now = function () {
                return new Date().getTime();
            }

            refresh();
            
            $scope.$on('$destroy', function () {
                $timeout.cancel(vm.timer);
                $timeout.cancel(vm.logTimer);
            });
            
            function refresh() {
            	sessionsRestService.getSessions(vm.appFilter).then(function(data){
					vm.sessions = data.sessions || [];
					vm.closedSessions = data.closedSessions || [];
                    vm.recordings = data.recordings || [];
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
            	
				configRestService.getPaths().then(function(data) {
					vm.paths = data.sort();
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
            
            function isSortByAsc(exp) {
                return vm.sortExp === exp && !vm.sortReverse;
            }
            
            function isSortByDesc(exp) {
            	return vm.sortExp === exp && vm.sortReverse;
            }
            
            function getSortByMetricsExpr(aggreg) {
                var exp = 'metrics.memoryUsed';
                if (aggreg != null) {
                    exp = "metrics['memoryUsed." + aggreg + "']";
                }
                return exp;
            }

            function getSortByLatencyExpr(aggreg) {
                var exp = 'metrics.latency';
                if (aggreg != null) {
                    exp = "metrics['latency." + aggreg + "']";
                }
                return exp;
            }

            function getSortByBandwidthExpr(aggreg) {
                var exp = 'metrics.inboundSize + metrics.outboundSize';
                if (aggreg != null) {
                    exp = "metrics['inboundSize." + aggreg + "'] + metrics['outboundSize." + aggreg + "']";
                }
                return exp;
            }
            
            function sortFinishedBy(exp) {
                if (vm.sortFinishedExp === exp) {
                    vm.sortFinishedReverse = !vm.sortFinishedReverse;
                } else {
                    vm.sortFinishedExp = exp;
                    vm.sortFinishedReverse = false;
                }
            }
            
            function isSortFinishedByAsc(exp) {
            	return vm.sortFinishedExp === exp && !vm.sortFinishedReverse;
            }
            
            function isSortFinishedByDesc(exp) {
            	return vm.sortFinishedExp === exp && vm.sortFinishedReverse;
            }
            
            function kill(session) {
                return sessionsRestService.killSession(session.applicationUrl, session.id).then(function () {
                    refresh();
                });
            }
            
            function view(session) {
                $location.url('/dashboard/session' + session.applicationUrl + '?id=' + session.id);
            }
            
            function loadSessionLogs(session) {
            	stopLogs();
            	
            	vm.logSession = session;
            	vm.log = [];
            	
            	loadLogs(session.applicationUrl, 100 * 1024, -1, true).then(function() {
            		loadLogsDelta(session.applicationUrl);
            	});
            }
            
            function stopLogs() {
            	if (vm.logTimer) {
					$timeout.cancel(vm.logTimer);
				}
            	vm.log = [];
            }
            
            function loadLogsDelta(path) {
				return loadLogs(path, 100 * 1024, vm.logEndOffset, false).then(function(data) {
					$timeout.cancel(vm.logTimer);
				}).then(function() {
					if (vm.logTimer) {
						$timeout.cancel(vm.logTimer);
					}
					vm.logTimer = $timeout(function() {
						loadLogsDelta(path);
					}, 1000);
					return vm.logTimer;
				}, function() {
					$timeout.cancel(vm.logTimer);
					vm.logTimer = undefined;
				});
			}

			function loadLogs(path, size, start, backwards) {
				var params = {
					backwards : backwards,
					offset : start,
					max : size,
					instanceId: vm.logSession.id
				};
				
				return logRestService.getSessionLog(path, params).then(function(data) {
					handleLogResponse(data, backwards);
				});
			}
			
			function handleLogResponse(data, backwards) {
				if (data.log.length > 0) {
					var log = data.log.split(/\n(?=\d{4})/g);
					if (backwards) {
						vm.log = log.concat(vm.log);
						vm.logStartOffset = data.startOffset;
						vm.logEndOffset = Math.max(vm.logEndOffset, data.endOffset);
					} else {
						vm.log = vm.log.concat(log);
						vm.logStartOffset = Math.min(vm.logStartOffset, data.startOffset);
						vm.logEndOffset = data.endOffset;
					}
				}
			}
			
			function split(line) {
            	return line.split(/\n(?=\d{4})/g);
			}
            
            function record(session) {
                return sessionsRestService.recordSession(session.applicationUrl, session.id).then(function () {
                    refresh();
                });
            }

            function play(session, file) {
                $location.url('/dashboard/playback' + session.applicationUrl + '?playback=' + file);
            }
            
            function playOther(file) {
            	if (vm.appFilter && vm.appFilter.length) {
            		$location.url('/dashboard/playback' + vm.appFilter + '?playback=' + file);
            	}
            }
            
            function showThreadDump(session, key) {
                window.open(sessionsRestService.getStackDumpPath(session.applicationUrl, session.id, key), "_blank");
            }

            function requestThreadDump(session) {
                sessionsRestService.requestThreadDump(session.applicationUrl, session.id);
            }
            
            function hasWarnings(session) {
                return (session.warnings &&session.warnings.length > 0) || session.warningHistory.length > 0 || Object.keys(session.threadDumps).length > 0
            }

        }

        SessionsController.$inject = ['$scope', '$timeout', '$location', 'configRestService', 'sessionsRestService', 'logRestService', '$route', '$routeParams', 'wsUtils', 'baseUrl'];

        return SessionsController;
    });
})(adminConsole.define);