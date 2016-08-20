(function(define) {
	define([ 'text!shared/appView.template.html' ], function f(htmlTemplate) {
		function wsAppViewDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					value : '=',
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsAppViewDirectiveController
			};
		}

		function wsAppViewDirectiveController($scope, $element, $attrs, $location) {
			var vm = this;
			vm.b64img = '';
			vm.start = start;
			vm.stop = stop;
			vm.viewSessions = viewSessions;
			vm.viewConfig = viewConfig;
			vm.panelStatusClass = panelStatusClass;
			vm.usageData = [ 0, 0, 0 ];
			vm.usageOptions = {
				thickness : 10
			};

			$scope.$watch('vm.value', function(newValue) {
				vm.b64img = 'data:image/png;base64,' + newValue.icon;
				vm.stoppable = newValue.status.status === 'Running';
				vm.startable = newValue.status.status === 'Stopped' | newValue.status.status === 'Error';
				vm.usageData = getUsageData(newValue);
				vm.configOptions = getConfigOptions(newValue);
			});

			function viewSessions() {
				$location.path('/dashboard/overview' + vm.value.path);
			}

			function viewConfig() {
				$location.path('/config/swing' + vm.value.path);
			}

			function start() {

			}

			function stop() {

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
					if (s === 'Starting') {
						return className + '-warning';
					}
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

		wsAppViewDirectiveController.$inject = [ '$scope', '$element', '$attrs', '$location' ];

		return wsAppViewDirective;
	});
})(adminConsole.define);