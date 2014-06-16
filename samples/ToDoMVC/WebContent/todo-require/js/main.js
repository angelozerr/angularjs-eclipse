/*global require*/
'use strict';

/**
 * This is the configuration needed so require js knows where stuff is and how
 * 
 */
require.config({
    paths : {
        angular : '../bower_components/angular/angular'
    },
    shim : {
        angular : {
            exports : 'angular'
        }
    }
});

require([
    'angular', 'app', 'controllers/todo', 'directives/todoFocus'
], function (angular) {
    angular.bootstrap(document, [
        'todomvc'
    ]);
});
