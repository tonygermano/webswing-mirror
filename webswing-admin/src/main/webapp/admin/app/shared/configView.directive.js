(function(define) {
	define([ 'text!shared/configView.template.html' ], function f(htmlTemplate) {
		function wsConfigViewDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					value : '=',
					variables : '=',
					hide : '=',
					readonly : '=',
					apply : '=',
					reset : '='
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsConfigViewDirectiveController
			};
		}

		function wsConfigViewDirectiveController($scope, $element, $attrs, wsUtils) {
			var vm = this;
			vm.showJson = showJson;
			vm.showForm = showForm;
			vm.isForm = true;
			vm.json = {};
			vm.aceLoaded = aceLoaded;

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
					showJson();
					editor.resize(true);
				}, true);
			}

			function showForm() {
				vm.isForm = true;
			}
		}

		wsConfigViewDirectiveController.$inject = [ '$scope', '$element', '$attrs', 'wsUtils' ];

		return wsConfigViewDirective;
	});
})(adminConsole.define);