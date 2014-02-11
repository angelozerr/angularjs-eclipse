package org.eclipse.angularjs.core.documentModel.dom;

import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;

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
	 * Returns the angular {@link DirectiveParameter} of the attribute and null
	 * otherwise.
	 * 
	 * @return the angular {@link DirectiveParameter} of the attribute and null
	 *         otherwise.
	 */
	DirectiveParameter getAngularDirectiveParameter();
}
