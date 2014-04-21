'use strict';

/* Services */

angular.module('ws-console.services', [])
  .service('atmosphereService', function($rootScope) {
    var responseParameterDelegateFunctions = ['onOpen', 'onClientTimeout', 'onReopen', 'onMessage', 'onClose', 'onError'];
    var delegateFunctions = responseParameterDelegateFunctions;
    delegateFunctions.push('onTransportFailure');
    delegateFunctions.push('onReconnect');

    return {
      subscribe: function(r) {
        var result = {};
        angular.forEach(r, function(value, property) {
          if (typeof value === 'function' && delegateFunctions.indexOf(property) >= 0) {
            if (responseParameterDelegateFunctions.indexOf(property) >= 0)
              result[property] = function(response) {
                $rootScope.$apply(function() {
                  r[property](response);
                });
              };
            else if (property === 'onTransportFailure')
              result.onTransportFailure = function(errorMsg, request) {
                $rootScope.$apply(function() {
                  r.onTransportFailure(errorMsg, request);
                });
              };
            else if (property === 'onReconnect')
              result.onReconnect = function(request, response) {
                $rootScope.$apply(function() {
                  r.onReconnect(request, response);
                });
              };
          } else
            result[property] = r[property];
        });

        return atmosphere.subscribe(result);
      }
    };
  })
  .constant('base64Encoder', function() {
    var PADCHAR = '=';
    var ALPHA = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';

    function b(s, i) {
      var x = s.charCodeAt(i);
      if (x > 255) {
        throw "Error";
      }
      return x;
    }

    function encode(s) {
      var i, b10;
      var x = [];
      s = "" + s;
      var imax = s.length - s.length % 3;
      if (s.length == 0) {
        return s;
      }
      for (i = 0; i < imax; i += 3) {
        b10 = (b(s, i) << 16) | (b(s, i + 1) << 8) | b(s, i + 2);
        x.push(ALPHA.charAt(b10 >> 18));
        x.push(ALPHA.charAt((b10 >> 12) & 0x3F));
        x.push(ALPHA.charAt((b10 >> 6) & 0x3f));
        x.push(ALPHA.charAt(b10 & 0x3f));
      }
      switch (s.length - imax) {
        case 1:
          b10 = b(s, i) << 16;
          x.push(ALPHA.charAt(b10 >> 18) + ALPHA.charAt((b10 >> 12) & 0x3F) +
            PADCHAR + PADCHAR);
          break;
        case 2:
          b10 = (b(s, i) << 16) | (b(s, i + 1) << 8);
          x.push(ALPHA.charAt(b10 >> 18) + ALPHA.charAt((b10 >> 12) & 0x3F) +
            ALPHA.charAt((b10 >> 6) & 0x3f) + PADCHAR);
          break;
      }
      return x.join('');
    }
    return {
      encode: encode,
    };
  });