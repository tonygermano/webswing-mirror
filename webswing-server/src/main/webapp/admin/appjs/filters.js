'use strict';

/* Filters */

angular.module('ws-console.filters', [])
	.filter('duration', [

		function() {
			return function(number) {
				var secondsTotal = number / 1000;
				var seconds = secondsTotal % 60;
				var minutes = ((secondsTotal - seconds) / 60) % 60;
				return minutes + ' min ' + Math.round(seconds) + ' sec';
			};
		}
	])
	.filter('bool', [

		function() {
			return function(i, trueRes, falseRes) {
				if (i) {
					return trueRes;
				} else {
					return falseRes;
				}
			};
		}
	]);