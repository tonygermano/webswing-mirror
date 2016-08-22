(function(define) {
	define([ 'text!shared/objectField.template.html' ], function f(htmlTemplate) {
		function wsObjectFieldDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					value : '=',
					variables : '=',
					hide : '=',
					readonly : '=',
					desc : '@',
					label : '@'
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsObjectFieldDirectiveController
			};
		}

		function wsObjectFieldDirectiveController($scope, $element, $attrs) {
			var vm = this;
			vm.tabs = {};
			vm.needTabs = false;
			vm.addObject = vm.addObject;

			$scope.$watch('vm.value', function(newValue) {
				vm.tabs = createTabs(newValue);
				vm.needTabs = Object.keys(vm.tabs).length > 1;
			});
			
			function createTabs(meta) {
				var result = {};
				var field;
				if (meta != null && meta.fields != null) {
					for (var int = 0; int < meta.fields.length; int++) {
						field = meta.fields[int];
						if (vm.hide == null || vm.hide.indexOf(field.name) === -1) {
							if (result[field.tab] == null) {
								result[field.tab] = {
									fields : [],
									objects : [],
									collapsed : false
								};
							}
							if (field.type.indexOf("Object") === 0) {
								result[field.tab].objects.push(field);
							} else {
								result[field.tab].fields.push(field);
							}
						}
					}
				}
				return result;
			}

			function addObject() {
				vm.value = {};
			}

		}

		wsObjectFieldDirectiveController.$inject = [ '$scope', '$element', '$attrs' ];

		return wsObjectFieldDirective;
	});
})(adminConsole.define);