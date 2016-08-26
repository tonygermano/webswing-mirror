(function(define) {
	define([ 'text!shared/configView.template.html' ], function f(htmlTemplate) {
		function wsConfigViewDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					value : '=',
					variables : '=',
					readonly : '=',
					apply : '=',
					reset : '='
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsConfigViewDirectiveController
			};
		}

		function wsConfigViewDirectiveController($scope, $element, $attrs, $timeout, messageService, wsUtils, configRestService) {
			var vm = this;
			vm.showJson = showJson;
			vm.showForm = showForm;
			vm.isForm = true;
			vm.json = {};
			vm.aceLoaded = aceLoaded;

			$scope.$on('wsHelperOpened', function(evt, data, i) {
				$scope.$broadcast('wsHelperClose', data, i);
			});

			$scope.$on('wsRequestFormUpdate', function(evt, data, i) {
				$timeout(function() {
					refreshForm(wsUtils.extractValues(vm.value));
				}, 0);
			});

			function refreshForm(config) {
				var e = $element.find('.ws-panel-heading').first();
				configRestService.getMeta(config).then(function(data) {
					vm.value = angular.extend({},vm.value,data);
					vm.isForm = true;
					$timeout(function() {
						e.height('auto');
					}, 0);
				});
				var height = e.innerHeight();
				e.height(height);
			}

			function showJson() {
				vm.isForm = false;
				vm.json = wsUtils.toJson(vm.value);
			}

			function aceLoaded(editor) {
				editor.setReadOnly(vm.readonly);
				$scope.$watch("vm.readonly", function(value) {
					editor.setReadOnly(value);
				});
				$scope.$watch('vm.value', function() {
					if (!vm.isForm) {
						vm.json = wsUtils.toJson(vm.value);
						editor.resize(true);
					}
				}, true);
			}

			function showForm() {
				var config = vm.value;
				try {
					refreshForm(JSON.parse(vm.json));
				} catch (e) {
					messageService.error('Failed to parse JSON. Back to last valid version.');
				}
			}
		}

		wsConfigViewDirectiveController.$inject = [ '$scope', '$element', '$attrs', '$timeout', 'messageService', 'wsUtils', 'configRestService' ];

		return wsConfigViewDirective;
	});
})(adminConsole.define);