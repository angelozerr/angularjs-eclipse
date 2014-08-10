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
import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMElement;
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;

/**
 * Represents elements in the dom model Angular.
 * 
 */
public class ElementImplForAngular extends ElementStyleImpl implements
		IAdaptable, IAngularDOMElement {

	private static final String WORKBENCH_ADAPTER = "org.eclipse.ui.model.IWorkbenchAdapter"; //$NON-NLS-1$

	private boolean angularDirectiveDirty = true;
	private Directive angularDirective;

	public ElementImplForAngular() {
		super();
	}

	public Object getAdapter(Class adapter) {
		if (adapter != null && adapter.getName().equals(WORKBENCH_ADAPTER)) {
			return null;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public ElementImplForAngular(ElementStyleImpl that) {
		super(that);
	}

	protected boolean isNestedClosed(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE;
	}

	public Node cloneNode(boolean deep) {
		ElementImpl cloned = new ElementImplForAngular(this);
		if (deep)
			cloneChildNodes(cloned, deep);
		return cloned;
	}

	/**
	 * @see ElementStyleImpl#setOwnerDocument(Document) make this method package
	 *      visible
	 */
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	/**
	 * @see setTagName(String) make this method package visible
	 */
	@Override
	protected void setTagName(String tagName) {
		super.setTagName(tagName);
		// Element tag name changes, the angular directive should be
		// re-computed.
		angularDirectiveDirty = true;
	}

	public boolean isGlobalTag() {
		return isPhpTag() ? false : super.isGlobalTag();
	}

	/**
	 * @return true if it is a angular element
	 */
	public boolean isPhpTag() {
		return AngularDOMModelParser.ANGULAR_TAG_NAME.equals(getNodeName());
	}

	public INodeAdapter getExistingAdapter(Object type) {

		// no validation or validation propagation for PHP tags
		if (isPhpTag() && type instanceof Class
				&& ValidationAdapter.class.isAssignableFrom((Class) type)) {
			return nullValidator;
		}
		return super.getExistingAdapter(type);
	}

	private final static ValidationComponent nullValidator = new NullValidator();

	public String getPrefix() {
		final String prefix = super.getPrefix();
		if (prefix == null && isPhpTag()) {
			return ""; //$NON-NLS-1$
		}
		return prefix;
	}

	@Override
	public boolean isStartTagClosed() {
		return isPhpTag() ? true : super.isStartTagClosed();
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

	@Override
	public DirectiveParameter getAngularDirectiveParameter() {
		// element cannot be a directive parameter
		return null;
	}

}
