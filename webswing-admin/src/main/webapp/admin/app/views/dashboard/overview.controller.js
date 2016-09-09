(function(define) {
	define([], function f() {
		function OverviewController($scope, $timeout, $location, sessionsRestService, $routeParams, wsUtils) {
			var vm = this;
			vm.path = $routeParams.path;
			vm.sessions = [];
			vm.closedSessions = [];
			vm.view = view;
			vm.lastUpdated = null;
			vm.refresh = refresh;
			vm.timer = undefined;
			vm.play = play;
			vm.back = back;

			refresh();

			$scope.$on('$destroy', function() {
				$timeout.cancel(vm.timer);
			});

			$scope.$watch('vm.sessions', function(value) {
				if (value != null) {
					for (var i = 0; i < value.length; i++) {
						var session = value[i];
						session.gauge = {};
						session.gauge.memory = wsUtils.getGaugeData(session.metrics, 'Mem', 'MB', 'memoryUsed', 'memoryAllocated', null, 1);
						session.gauge.inbound = wsUtils.getGaugeData(session.metrics, 'In', 'k/s', 'inboundSize', null, 360, 1024);
						session.gauge.outbound = wsUtils.getGaugeData(session.metrics, 'Out', 'k/s', 'outboundSize', null, 360, 1024);
						session.gauge.latency = wsUtils.getGaugeData(session.metrics, 'Lat', 'ms', 'latency', null, 200, 1);
					}
				}
			});

			function refresh() {
				return sessionsRestService.getSessions(vm.path).then(function(data) {
					$timeout.cancel(vm.timer);
					vm.sessions = data.sessions;
					vm.closedSessions = data.closedSessions;
					vm.lastUpdated = new Date();
				}).then(function() {
					vm.timer = $timeout(refresh, 2000);
					return vm.timer;
				}, function() {
					$timeout.cancel(vm.timer);
					vm.timer = undefined;
				});
			}

			function view(session) {
				$location.search('id', session.id);
				$location.path('/dashboard/session/' + vm.path);
			}

			function play(session) {
				$location.url('/dashboard/playback?playback=' + session.recordingFile);
			}

			function back() {
				$location.path('/dashboard');
				$timeout.cancel(vm.timer);
			}
		}
		OverviewController.$inject = [ '$scope', '$timeout', '$location', 'sessionsRestService', '$routeParams', 'wsUtils' ];

		return OverviewController;
	});
})(adminConsole.define);