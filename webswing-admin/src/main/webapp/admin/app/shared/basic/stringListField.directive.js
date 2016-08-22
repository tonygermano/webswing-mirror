(function(define) {
	define([ 'text!shared/basic/stringListField.template.html' ], function f(htmlTemplate) {
		function wsStringListFieldDirective() {
			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					config : '=',
					value : '=',
					items : '=',
					variables : '=',
					readonly : '=',
					label : '@',
					desc : '@'
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsStringListFieldDirectiveController
			};
		}

		function wsStringListFieldDirectiveController($scope, $attrs) {
			var vm = this;
			vm.addString = addString;
			vm.deleteString = deleteString;
			vm.helpVisible = [];
			vm.openHelper = openHelper;
			vm.toggleHelper = toggleHelper;
			vm.setChoice = setChoice;

			if (vm.value == null) {
				vm.value = [];
			}

			$scope.$on('wsHelperClose', function(evt, ctrl, index) {
				for (var i = 0; i < vm.helpVisible.length; i++) {
					if (vm !== ctrl || index !== i) {
						vm.helpVisible[i] = false;
					}
				}
			});

			function toggleHelper(index) {
				if (vm.variables != null) {
					vm.helpVisible[index] = vm.helpVisible == null ? true : !vm.helpVisible[index];
					if (vm.helpVisible[index]) {
						$scope.$emit('wsHelperOpened', vm, index);
					}
				}
			}

			function openHelper(index) {
				if (vm.variables != null) {
					vm.helpVisible[index] = true;
					$scope.$emit('wsHelperOpened', vm, index);
				}
			}

			function addString(index) {
				vm.value.splice(index + 1, 0, '');
			}

			function deleteString(index) {
				vm.value.splice(index, 1);
			}

			function setChoice(index, value) {
				vm.value[index] = value;
			}
		}

		wsStringListFieldDirectiveController.$inject = [ '$scope', '$attrs' ];

		return wsStringListFieldDirective;
	});
})(adminConsole.define);