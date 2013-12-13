(function( ng, app ) {

	"use strict";

	app.directive(
		"bnSlave",
		function( $document ) {

			// I provide a way for directives to interact using the exposed API.
			function Controller( $scope, $element, $attrs ) {

				// -- Define Controller Methods. ------------ //
				

				// I move the current element to the given position (delta). Notice that I 
				// update the CSS of the element directly, rather than using the slave properties.
				// This is because the moveTo() will NOT happen inside a $digest (for 
				// performance reasons). As such, the ngStyle on the element will not have any
				// effect on the position resulting from the mouse movement.
				function moveTo( deltaX, deltaY ) {

					$element.css({
						left: ( $scope.slave.x + deltaX + "px" ),
						top: ( $scope.slave.y + deltaY + "px" )
					});

				}


				// I reposition the current slave to the given position (delta). This updates
				// the slave directly, as this WILL happen inside of a $digest.
				function reposition( deltaX, deltaY ) {

					$scope.reposition(
						( $scope.slave.x + deltaX ),
						( $scope.slave.y + deltaY )
					);

				}
				

				// -- Define Controller Variables. ---------- //


				// Return public API.
				return({
					moveTo: moveTo,
					reposition: reposition
				});

			}


			// I link the $scope to the DOM element and UI events.
			function link( $scope, element, attributes, controllers ) {


				// -- Define Link Methods. ------------------ //


				// I keep track of the initial click and start tracking movement.
				function handleMouseDown( event ) {

					$document.on( "mousemove.bnSlave", handleMouseMove );
					$document.on( "mouseup.bnSlave", handleMouseUp );

				}


				// I keep track of whether or not the mouse has been moved; if it has, we are 
				// no longer going to care about the position of the mouse upon release - we'll
				// consider the marker "activated".
				function handleMouseMove( event ) {

					$document.off( "mousemove.bnSlave" );
					$document.off( "mouseup.bnSlave" );

				}


				// I keep track of the final mouse release - and, remove the slave. If this 
				// event handler has fired, it means that the mouse-move event was not triggered,
				// which means the element has not been moved.
				function handleMouseUp( event ) {

					$document.off( "mousemove.bnSlave" );
					$document.off( "mouseup.bnSlave" );

					// Break the connection to the master controller so the master controller 
					// cannot send any further communications. 
					masterController.unbind( slaveController );

					// Remove the slave from the collection.
					$scope.$apply(
						function() {

							$scope.remove();
							
						}
					);

				}


				// -- Define Link Variables. ---------------- //

				
				// Get the required controllers from the link arguments.
				var slaveController = controllers[ 0 ];
				var masterController = controllers[ 1 ];

				// Listen to position updates from the master controller. When you bind to the
				// master controller, we expect to have our controller's moveTo() and reposition()
				// methods called.
				masterController.bind( slaveController );

				// Listen to the mouse click in order to start tracking movement changes.
				element.on( "mousedown.bnSlave", handleMouseDown );

				// When the scope is destroyed, make sure to unbind all event handlers to help
				// prevent a memory leak.
				$scope.$on( 
					"$destroy",
					function( event ) {

						// Clean up the master-slave binding in case this element is removed outside
						// of our internal event handling.
						masterController.unbind( slaveController );

						// Clear any existing mouse bindings.
						element.off( "mousedown.bnSlave" );
						$document.off( "mousemove.bnSlave" );
						$document.off( "mouseup.bnSlave" );

					}
				);

			}


			// Return the directives configuration.
			return({
				controller: Controller,
				link: link,
				require: [ "bnSlave", "^bnMaster" ],
				restrict: "A"
			});


		}
	);

})( angular, demo );