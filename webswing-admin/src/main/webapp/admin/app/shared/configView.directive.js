(function(define) {
	define([ 'text!shared/configView.template.html' ], function f(htmlTemplate) {
		function wsConfigViewDirective() {

			return {
				restrict : 'E',
				template : htmlTemplate,
				scope : {
					value : '=',
					variables : '=',
					hide : '='
				},
				controllerAs : 'vm',
				bindToController : true,
				controller : wsConfigViewDirectiveController
			};
		}

		function wsConfigViewDirectiveController($scope, $element, $attrs) {
			var vm = this;
			vm.readonly = watchBoolean('readonly', false);
			vm.tabs = {};
			vm.needTabs = false;
			vm.label = resolve('label', '');
			vm.desc = resolve('desc', null);
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
								result[field.tab] = [];
							}
							result[field.tab].push(field);
						}
					}
				}
				return result;
			}

			function addObject() {
				vm.value = {};
			}

			function watchBoolean(name, defaultVal) {
				$scope.$watch(function() {
					var val = resolve(name, defaultVal);
					return val !== false && val !== 'false';
				}, function(newValue) {
					vm[name] = newValue;
				});
			}

			function resolve(name, defaultVal) {
				if ($attrs[name] != null) {
					return $attrs[name];
				} else {
					return defaultVal;
				}
			}
		}

		wsConfigViewDirectiveController.$inject = [ '$scope', '$element', '$attrs' ];

		return wsConfigViewDirective;
	});
})(adminConsole.define);