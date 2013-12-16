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

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.w3c.dom.Document;

/**
 * Represents attributes implementation in Angular dom model
 * 
 */
public class TextImplForAngular extends TextImpl implements IAdaptable {

	protected TextImplForAngular() {
		super();
	}

	protected TextImplForAngular(Document doc, String data) {
		super();
		setOwnerDocument(doc);
		setData(data);
	}

	protected boolean isNotNestedContent(String regionType) {
		return regionType != AngularRegionContext.ANGULAR_EXPRESSION_CONTENT;
	}

	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}