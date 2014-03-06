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

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;

public class AngularDOMModelParser extends XMLModelParser {

	public static final String ANGULAR_TAG_NAME = "ANGULAR"; //$NON-NLS-1$

	public AngularDOMModelParser(DOMModelImpl model) {
		super(model);
	}

	protected boolean isNestedContent(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT;
	}

	protected boolean isNestedTag(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE;
	}

	protected boolean isNestedTagOpen(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN;
	}

	protected String computeNestedTag(String regionType, String tagName,
			IStructuredDocumentRegion structuredDocumentRegion,
			ITextRegion region) {
		return AngularDOMModelParser.ANGULAR_TAG_NAME;
	}

	protected boolean isNestedTagClose(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE;
	}
}
