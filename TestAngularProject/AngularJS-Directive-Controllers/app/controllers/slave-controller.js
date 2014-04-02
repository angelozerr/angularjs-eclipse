(function( ng, app ) {

	"use strict";

	app.controller(
		"SlaveController",
		function( $scope ) {


			// -- Define Scope Methods. --------------------- //


			// I remove the current slave from the collection.
			$scope.remove = function() {

				// Pass this responsibility up the scope chain to the master controller (and its
				// collection of slave instances).
				$scope.removeSlave( $scope.slave );

			};


			// I reposition the current slave.
			$scope.reposition = function( x, y ) {

				$scope.slave.x = x;
				$scope.slave.y = y;

			};


		}
	);

})( angular, demo );