(function(define) {
	define([], function f() {
		function LogViewController($scope, $timeout, $location, $routeParams, $anchorScroll, logRestService, configRestService, baseUrl) {
			var vm = this;
			
			vm.path = $routeParams.path;
			if (vm.path) {
				vm.path = vm.path.replace(/\/$/, "");
			}
			
			vm.types = [ {
				label : 'Audit',
				url : 'audit',
				delimiter : '|',
				lineDelimiter : '\n',
				headers : [ 'Timestamp', 'Result', 'User', 'Detail', 'Secured path', 'Protocol', 'Source IP', 'Module' ],
				downloadEnabled : true
			}, {
				label : 'Server',
				url : 'server',
				delimiter : null,
				lineDelimiter : /\n(?=\d{4})/g,
				headers : [ 'Messages' ],
				downloadEnabled : true
			}, {
				label : 'Session',
				url : 'session',
				delimiter : null,
				lineDelimiter : /\n(?=\d{4})/g,
				headers : [ 'Messages' ],
				downloadEnabled : false
			}  ];
			vm.showLog = showLog;
			vm.showAppLogNames = showAppLogNames;
			vm.showAppLog = showAppLog;
			vm.isActive = isActive;
			vm.isActiveApp = isActiveApp;
			vm.isActiveAppLogName = isActiveAppLogName;
			vm.isSessionType = isSessionType;
			vm.split = split;
			vm.timer = undefined;
			vm.filter = '';
			vm.endOffset = -1;
			vm.log = [];
			vm.download = download;
			vm.apps = [];
			vm.appLogNames = [];
			
			vm.type = findType();
			
			vm.app = $location.search().app;
			vm.logName = $location.search().logName;
			loadApps();
			loadAppLogNames();
			
			if (vm.type && (!isSessionType(vm.type) || (vm.app && vm.logName))) {
				load(100 * 1024, -1, true).then(function() {
					$timeout(function() {
						$location.hash('endOfPageAnchor');
						$anchorScroll();
					}, 100);
				}).then(loadDelta);
			}
			
			$scope.$on('$destroy', function() {
				$timeout.cancel(vm.timer);
			});
			
			$scope.$watch('vm.app', function(newVal, oldVal) {
			    vm.app = newVal;
			    if (vm.type && isSessionType(vm.type) && newVal && newVal !== oldVal) {
			    	showAppLogNames(vm.type, vm.app);
			    }
			});

			function loadDelta() {
				return load(100 * 1024, vm.endOffset, false).then(function(data) {
					$timeout.cancel(vm.timer);
				}).then(function() {
					if (vm.timer) {
						$timeout.cancel(vm.timer);
					}
					vm.timer = $timeout(loadDelta, 1000);
					return vm.timer;
				}, function() {
					$timeout.cancel(vm.timer);
					vm.timer = undefined;
				});
			}

			function load(size, start, backwards) {
				var params = {
					backwards : backwards,
					offset : start,
					max : size,
					logName : vm.logName || null
				};
				
				if (isSessionType(vm.type)) {
					return logRestService.getSessionLog(vm.app, params).then(function(data) {
						handleLogResponse(data, backwards);
					});
				}
				
				return logRestService.getLog(vm.type.url, params).then(function(data) {
					handleLogResponse(data, backwards);
				});
			}
			
			function handleLogResponse(data, backwards) {
				if (data.log.length > 0) {
					var log = data.log.split(vm.type.lineDelimiter);
					if (backwards) {
						vm.log = log.concat(vm.log);
						vm.startOffset = data.startOffset;
						vm.endOffset = Math.max(vm.endOffset, data.endOffset);
					} else {
						vm.log = vm.log.concat(log);
						vm.startOffset = Math.min(vm.startOffset, data.startOffset);
						vm.endOffset = data.endOffset;
					}
				}
				vm.lastUpdated = new Date();
			}
			
			function loadApps() {
				if (!vm.type || !isSessionType(vm.type)) {
					vm.apps = [];
					return;
				}
				return configRestService.getApps().then(function(data) {
					vm.apps = data;
					if (vm.apps && vm.apps.length && (!vm.app || !vm.app.length)) {
						// select first app by default
						vm.app = vm.apps[0].url;
					}
				});
			}
			
			function loadAppLogNames() {
				if (!vm.type || !isSessionType(vm.type) || !vm.app || !vm.app.length) {
					vm.appLogNames = [];
					return;
				}
				return logRestService.getSessionLogNames(vm.app, vm.app).then(function(data) {
					vm.appLogNames = data;
				});
			}
			
			function split(line) {
				if (vm.type.delimiter != null) {
					return line.split(vm.type.delimiter);
				} else {
					return [ line ];
				}
			}

			function findType() {
				for (var int = 0; int < vm.types.length; int++) {
					var t = vm.types[int];
					if (t.url === vm.path) {
						return t;
					}
				}
				return showLog(vm.types[0]);
			}

			function showLog(type) {
				if (isActive(type)) {
					return;
				}
				
				$location.path('/logs/' + type.url);
				$location.search('app', null);
				$location.search('logName', null);
			}
			
			function showAppLog(type, app, logName) {
				$location.path('/logs/' + type.url);
				$location.search('app', app);
				$location.search('logName', logName);
			}
			
			function showAppLogNames(type, app) {
				$location.path('/logs/' + type.url);
				$location.search('app', app);
				$location.search('logName', null);
			}

			function isActive(type) {
				return vm.type && vm.type.label === type.label;
			}
			
			function isActiveApp(app) {
				return vm.app && vm.app == app.url;
			}
			
			function isActiveAppLogName(logName) {
				return vm.logName && vm.logName == logName;
			}

			function download(type, event) {
				window.open(baseUrl + '/rest/logs/' + type.url, '_blank');
				event.stopPropagation();
			}
			
			function isSessionType(type) {
				return type.url == 'session';
			}

		}
		LogViewController.$inject = [ '$scope', '$timeout', '$location', '$routeParams', '$anchorScroll', 'logRestService', 'configRestService', 'baseUrl' ];

		return LogViewController;
	});
})(adminConsole.define);