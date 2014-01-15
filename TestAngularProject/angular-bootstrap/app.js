var app = angular.module("app", []);

app.run(function($rootScope) {
    $rootScope.random = Math.random()
})

angular.bootstrap(document.getElementById("container"), ["app"])
angular.bootstrap(document.getElementById("container2"), ["app"])