/*******************************************************************************
 * Copyright (c) 2013 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.core.documentModel.dom;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Attr;

import tern.angular.modules.Directive;

/**
 * Angular DOM {@link Attr} API.
 * 
 */
public interface IAngularDOMAttr extends IDOMAttr {

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
