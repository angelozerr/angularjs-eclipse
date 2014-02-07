package org.eclipse.angularjs.core.documentModel.dom;

import tern.angular.modules.Directive;

public interface IAngularDirectiveProvider {

	/**
	 * Returns the angular {@link Directive} of the attribute and null
	 * otherwise.
	 * 
	 * @return the angular {@link Directive} of the attribute and null
	 *         otherwise.
	 */
	Directive getAngularDirective();

	/**
	 * Returns true if the attribute is an angular directive (ex : ng-app) and
	 * false otherwise.
	 * 
	 * @return true if the attribute is an angular directive (ex : ng-app) and
	 *         false otherwise.
	 */
	boolean isAngularDirective();
}
