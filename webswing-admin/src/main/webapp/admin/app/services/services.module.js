define([
    'services/navigation.service',
    'services/message.service',
    'services/sessionsRest.service',
    'services/settingsRest.service',
    'services/configRest.service',
    'services/usersRest.service'
], function f(navigationService, messageService, sessionsRestService, settingsRestService, configRestService,usersRestService) {
    var module = angular.module('wsServices', []);

    module.service('navigationService', navigationService);
    module.service('messageService', messageService);
    module.service('sessionsRestService', sessionsRestService);
    module.service('settingsRestService', settingsRestService);
    module.service('configRestService', configRestService);
    module.service('usersRestService', usersRestService);

});