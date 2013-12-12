/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.taginfo;

import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Provides hover help documentation for Angular tags
 * 
 */
public class HTMLAngularTagInfoHoverProcessor extends HTMLTagInfoHoverProcessor {
	public HTMLAngularTagInfoHoverProcessor() {
		super();
	}

	@Override
	protected String computeTagAttNameHelp(IDOMNode xmlnode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		// Display Help of Angular Directive if it's an angular directive
		// attribute
		Directive directive = DOMUtils.getAngularDirective(xmlnode, region);
		if (directive != null) {
			return directive.getHTMLDescription();
		}
		// Here the attribute is not a directive, display classic Help.
		return super.computeTagAttNameHelp(xmlnode, parentNode, flatNode,
				region);
	}

}
