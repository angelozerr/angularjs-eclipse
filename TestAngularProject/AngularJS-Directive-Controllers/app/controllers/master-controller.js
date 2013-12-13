(function( ng, app ) {

	"use strict";

	app.controller(
		"MasterController",
		function( $scope ) {


			// -- Define Controller Methods. ---------------- //


			// I get the next available ID for a new slave.
			function getNextID() {

				if ( ! $scope.slaves.length ) {

					return( 1 );

				}

				var lastSlave = $scope.slaves[ $scope.slaves.length - 1 ];

				return( lastSlave.id + 1 );

			}


			// I get a random coordinate based on the given constraints.
			function getRandomCoordinate( minCoordinate, maxCoordinate ) {

				var delta = ( maxCoordinate - minCoordinate );

				return(
					minCoordinate + Math.floor( Math.random() * delta )
				);

			}


			// -- Define Scope Methods. --------------------- //


			// I add a new slave at the given position.
			$scope.addSlave = function( x, y ) {

				$scope.slaves.push({
					id: getNextID(),
					x: x,
					y: y
				});

			};


			// I remove the given slave from the collection.
			$scope.removeSlave = function( slave ) {

				// Find the slave in the collection.
				var index = $scope.slaves.indexOf( slave );

				// Splice out slave.
				$scope.slaves.splice( index, 1 );

			};


			// -- Set Scope Variables. ---------------------- //


			// This is our list of slaves and their coordinates. Starting with an initial
			// collection of one (at a random position).
			$scope.slaves = [
				{
					id: 1,
					x: getRandomCoordinate( 50, 400 ),
					y: getRandomCoordinate( 50, 200 )
				}
			];


		}
	);

})( angular, demo );