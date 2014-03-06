/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
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
