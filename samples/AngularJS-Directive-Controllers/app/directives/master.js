(function( ng, app ) {

	"use strict";

	app.directive(
		"bnMaster",
		function() {


			// I provide a way for directives to interact using the exposed API.
			function Controller( $scope ) {


				// -- Define Controller Methods. ------------ //


				// I am utility method that applies the given method to the each item using 
				// the given arguments.
				function applyForEach( collection, methodName, methodArguments ) {

					ng.forEach(
						collection,
						function( item ) {

							item[ methodName ].apply( item, methodArguments );
						}
					);

				}


				// I add the given listern to the list of subscribers. Each listener must expose
				// two methods: moveTo( deltaX, deltaY ) and reposition( deltaX, deltaY ).
				function bind( listener ) {

					listeners.push( listener );

				}


				// I invoke the moveTo() method on each bound listener.
				function moveTo( deltaX, deltaY ) {

					applyForEach( listeners, "moveTo", [ deltaX, deltaY ] );

				}


				// I invoke the reposition() method on each bound listener.
				function reposition( deltaX, deltaY ) {

					applyForEach( listeners, "reposition", [ deltaX, deltaY ] );

				}


				// I unbind the given listener from the list of subscribers.
				function unbind( listener ) {

					var index = listeners.indexOf( listener );

					if ( index === -1 ) {

						return;

					}

					listeners.splice( index, 1 );

				}


				// -- Define Controller Variables. ---------- //


				// I am the collection of listeners that want to know about updated coordinates.
				var listeners = [];


				// Return public API.
				return({
					bind: bind,
					moveTo: moveTo,
					reposition: reposition,
					unbind: unbind
				});

			}


			// I link the $scope to the DOM element and UI events.
			function link( $scope, element, attributes, controller ) {


				// -- Define Link Methods. ------------------ //


				// I keep track of the initial mouse click on the master. The behavior differs
				// depending on whether a slave was clicked; or, the master canvas was clicked.
				function handleMouseDown( event ) {

					var target = $( event.target );

					// Prevent default behavior to stop text selection.
					event.preventDefault();

					// The user clicked on a slave.
					if ( target.is( "li.slave" ) ) {

						// Record the initial position of the mouse so we can calculate the 
						// coordinates of the reposition (using deltas).
						initialPageX = event.pageX;
						initialPageY = event.pageY;

						// Bind to the movement so we can broadcast new coordinates.
						element.on( "mousemove.bnMaster", handleMouseMove );
						element.on( "mouseup.bnMaster", handleMouseUp );

					// The user clicked on the master canvas directly. We'll use this as an invite
					// to create a new slave handle.
					} else {

						$scope.$apply(
							function() {

								$scope.addSlave( event.pageX, event.pageY );
								
							}
						);

					}

				}


				// I listen for mouse movements to broadcast new position deltas to all of the slaves.
				function handleMouseMove( event ) {

					controller.moveTo(
						( event.pageX - initialPageX ),
						( event.pageY - initialPageY )
					);

				}


				// I listen for mouse ups to determine when movement has ceased and positions
				// of the slaves need to be finalized.
				function handleMouseUp( event ) {

					// Now that the user has finished moving the mouse, unbind the mouse events.
					element.off( "mousemove.bnMaster" );
					element.off( "mouseup.bnMaster" );

					// Check to see if the elements have moved at all. If they have not, then there
					// is nothing more that the master needs to do.
					if ( ! hasMoved( event.pageX, event.pageY ) ) {

						return;

					}

					// Tell all the slaves to finalize positions.
					$scope.$apply(
						function() {

							controller.reposition(
								( event.pageX - initialPageX ),
								( event.pageY - initialPageY )
							);
							
						}
					);

				}


				// I determine if the given coorindates indicate movement from the original position.
				function hasMoved( pageX, pageY ) {

					return(
						( pageX !== initialPageX ) ||
						( pageY !== initialPageY )
					);

				}


				// -- Define Link Variables. ---------------- //


				// I hold the initial position of the mouse click.
				var initialPageX = null;
				var initialPageY = null;

				// Bind to the mouse down event so we can interact with the slaves.
				element.on( "mousedown.bnMaster", handleMouseDown );

				// When the scope is destroyed, make sure to unbind all event handlers to help
				// prevent a memory leak.
				$scope.$on( 
					"$destroy",
					function( event ) {

						element.off( "mousedown.bnMaster" );
						element.off( "mousemove.bnMaster" );
						element.off( "mouseup.bnMaster" );

					}
				);

			}


			// Return the directives configuration.
			return({
				controller: Controller,
				link: link,
				require: "bnMaster",
				restrict: "A"
			});


		}
	);

})( angular, demo );