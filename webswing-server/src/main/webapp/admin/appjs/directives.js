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
.directive('wsOutput',[ function() {
	return {
		restrict: 'E',
		scope:{label:'=',value:'='},
		template: "	<div class='form-group'>"+
		"<label class='col-sm-4 control-label'>{{label}}</label>"+
		"<div class='col-sm-8'>"+
		"<p class='form-control-static'>{{value}}</p>"+
		"</div>	</div>"
	};
}])
.directive('wsInputText',[ function() {
	return {
		restrict: 'E',
		scope:{label:'=',value:'=',help:'='},
		templateUrl: "partials/component/text-input-template.html"
	};
}]);