'use strict';

/* Directives */


angular.module('ws-console.directives', [])
.directive('wsButtonsRadio',[ function() {
	return {
		restrict: 'E',
		scope: { model: '=', options:'='},
		controller: function($scope){
			$scope.activate = function(option){
				$scope.model = option;
			};      
		},
		template: "<label class='btn btn-default'" +
		"ng-class='{active: option == model}'" +
		"ng-repeat='option in options' " +
		"ng-click='activate(option)'>" +
		"<input type='radio' name='{{name}}'>{{option}}" +
		"</label>"
	};
}])
.directive('wsJsonEditor',[ function() {
	return {
		restrict: 'A',
		link: function (scope, element, attrs){
			var editor = ace.edit(element[0]);
		    editor.getSession().setMode("ace/mode/json");
		}
	};
}]);