(function(define) {
	define([], function f() {
		function wsFieldViewDirective($compile) {

			var stringTemplate = '<ws-string-field></ws-string-field>';
			var boolTemplate = '<ws-boolean-field></ws-boolean-field>';
			var objectTemplate = '<ws-config-view></ws-config-view>';
			function linker(scope, element, attrs, ctrl) {
				var vm = scope.vm;

				if (vm.field != null) {
					var commonAttrs = {
						readonly : '{{vm.readonly}}',
						label : vm.field.label,
						desc : vm.field.description,
						variables : vm.field.variables ? 'vm.variables' : null,
						items : 'vm.field.presets',
						value : 'vm.field.value'
					};

					if (vm.field.type === 'String') {
						var template = angular.element(stringTemplate);
						template.attr(commonAttrs);
						element.append(template).show();
					}
					if (vm.field.type === 'Number') {
						var template = angular.element(stringTemplate);
						template.attr(commonAttrs);
						template.attr('type', 'number');
						element.append(template).show();
					}
					if (vm.field.type === 'Boolean') {
						var template = angular.element(boolTemplate);
						template.attr(commonAttrs);
						element.append(template).show();
					}
					if (vm.field.type === 'Object') {
						var template = angular.element(objectTemplate);
						template.attr(commonAttrs);
						element.append(template).show();
					}
					$compile(element.contents())(scope);
				}
			}

			return {
				restrict : 'E',
				link : linker,
				scope : {
					field : '=',
					variables : '='
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsFieldViewDirectiveController
			};
		}

		function wsFieldViewDirectiveController($scope, $element, $attrs) {
			var vm = this;
		}

		wsFieldViewDirectiveController.$inject = [ '$scope', '$element', '$attrs' ];
		wsFieldViewDirective.$inject = [ '$compile' ];

		return wsFieldViewDirective;
	});
})(adminConsole.define);