/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.validation;

import org.eclipse.wst.html.core.validate.extension.IHTMLCustomTagValidator;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

import tern.angular.modules.Restriction;

/**
 * Custom HTML angular tag validator to ignore error like 'Unknown tag
 * (ng-pluralize)' for angular tag element which is an angular directive which
 * have 'E' restriction (like ng-pluralize).
 * 
 * <pre>
 *   <ng-pluralize /> // ng-pluralize is not marked as error because it's an angular directive with restrict 'E'
 * </pre>
 */
public class HTMLAngularTagValidator extends AbstractHTMLAngularValidator implements IHTMLCustomTagValidator {

	@Override
	public boolean canValidate(IDOMElement target) {
		if (hasAngularNature()) {
			// the project has angular nature
			// return true if the tag element is an known angular directive (ex
			// <ng-pluralize) and false otherwise.
			String tagName = target.getTagName();
			return (getDirective(null, tagName, Restriction.E) != null);
		}
		return false;
	}

	@Override
	public ValidationMessage validateTag(IDOMElement target) {
		// it's a valid tag element because it's an angular directive with 'E'
		// restrict
		return null;
	}

}
