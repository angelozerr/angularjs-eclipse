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
package org.eclipse.angularjs.internal.core.documentModel.dom;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;

/**
 * Represents attributes implementation in Angular dom model
 * 
 */
public class AttrImplForAngular extends AttrImpl implements IAngularDOMAttr {

	private boolean angularDirectiveDirty = true;
	private Directive angularDirective;

	protected boolean isNestedLanguageOpening(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN;
	}

	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	protected void setName(String name) {
		super.setName(name);
		// Attribute name changes, the angular directive should be re-computed.
		angularDirectiveDirty = true;
	}

	@Override
	public boolean isAngularDirective() {
		return getAngularDirective() != null;
	}

	@Override
	public Directive getAngularDirective() {
		if (angularDirectiveDirty) {
			angularDirective = computeAngularDirective();
			angularDirectiveDirty = false;
		}
		return angularDirective;
	}

	private Directive computeAngularDirective() {
		Element element = getOwnerElement();
		if (element == null) {
			return null;
		}
		return AngularModulesManager.getInstance().getDirective(
				element.getNodeName(), super.getName());
	}

}
