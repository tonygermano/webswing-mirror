(function (define) {
    define([], function f() {
        function durationFilter() {
            return function(number) {
                var secondsTotal = number / 1000;
                var seconds = secondsTotal % 60;
                var minutes = ((secondsTotal - seconds) / 60) % 60;
                var hours = (secondsTotal - minutes * 60 - seconds) / 3600;
                return (hours > 0?(hours +'h '):'')+minutes + 'min ' + Math.round(seconds) + 'sec';
            };
        }
        return durationFilter;
    });
})(adminConsole.define);
