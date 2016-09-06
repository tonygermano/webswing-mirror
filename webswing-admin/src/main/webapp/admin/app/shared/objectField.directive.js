(function(define) {
	define([ 'text!shared/objectField.template.html' ], function f(htmlTemplate) {
		function wsObjectFieldDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					field : '=',
					value : '=',
					variables : '=',
					readonly : '=',
					desc : '@',
					label : '@',
					restricted : '@'
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
			vm.addObject = addObject;

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
						if (meta.hide == null || meta.hide.indexOf(field.name) === -1) {
							if (result[field.tab] == null) {
								result[field.tab] = {
									fields : [],
									objects : [],
									collapsed : vm.restricted === 'true' ? true : false
								};
							}
							setDisabled(meta.enable, field);
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

			function setDisabled(enableArray, field) {
				if (vm.restricted === 'true' || (enableArray != null && enableArray.indexOf(field.name) === -1)) {
					field.restricted = 'true';
				}
			}

			function addObject() {
				vm.field.value = {
					fields : []
				};
				requestFormUpdate();
			}

			function requestFormUpdate() {
				$scope.$emit('wsRequestFormUpdate', vm);
			}

		}

		wsObjectFieldDirectiveController.$inject = [ '$scope', '$element', '$attrs' ];

		return wsObjectFieldDirective;
	});
})(adminConsole.define);