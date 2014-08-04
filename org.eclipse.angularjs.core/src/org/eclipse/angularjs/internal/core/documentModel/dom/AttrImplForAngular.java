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
package org.eclipse.angularjs.internal.core.documentModel.dom;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.w3c.dom.Document;

import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;

/**
 * Represents attributes implementation in Angular dom model
 * 
 */
public class AttrImplForAngular extends AttrImpl implements IAngularDOMAttr {

	private Boolean angularDirectiveDirty = true;
	private Directive angularDirective;
	private DirectiveParameter directiveParameter;

	protected boolean isNestedLanguageOpening(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN;
	}

	@Override
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	@Override
	protected void setName(String name) {
		super.setName(name);
		// Attribute name changes, the angular directive should be re-computed.
		synchronized (angularDirectiveDirty) {
			angularDirectiveDirty = true;
		}
	}

	@Override
	public Directive getAngularDirective() {
		computeIfNeeded();
		return angularDirective;
	}

	public void computeIfNeeded() {
		synchronized (angularDirectiveDirty) {
			if (angularDirectiveDirty) {
				// check if it's a angular directive.
				angularDirective = computeAngularDirective();
				if (angularDirective == null) {
					// check if it's an angular directive parameter
					directiveParameter = computeAngularDirectiveParameter();
				}
				angularDirectiveDirty = false;
			}
		}
	}

	private Directive computeAngularDirective() {
		try {
			IProject project = DOMUtils.getFile(this).getProject();
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMDirectiveProvider.getInstance().getAngularDirective(
					angularProject, this);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return null;
	}

	private DirectiveParameter computeAngularDirectiveParameter() {
		try {
			IProject project = DOMUtils.getFile(this).getProject();
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMDirectiveProvider.getInstance()
					.getAngularDirectiveParameter(angularProject, this);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return null;
	}

	@Override
	public DirectiveParameter getAngularDirectiveParameter() {
		computeIfNeeded();
		return directiveParameter;
	}

}
