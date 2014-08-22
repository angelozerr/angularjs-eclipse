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
package org.eclipse.angularjs.internal.core.documentModel;

import org.eclipse.angularjs.internal.core.documentModel.dom.AngularDOMModelParser;
import org.eclipse.angularjs.internal.core.documentModel.dom.AngularDOMModelUpdater;
import org.eclipse.angularjs.internal.core.documentModel.dom.DOMDocumentForAngular;
import org.eclipse.wst.html.core.internal.document.DOMStyleModelImpl;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;
import org.eclipse.wst.xml.core.internal.document.XMLModelUpdater;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;

/*
 * The AngularModel will support both the DOM style interface and AngularJS expression specific API's.
 */
/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class DOMModelForAngular extends DOMStyleModelImpl {

	/*
	 * This is modeled after what is done for Angular
	 */
	protected Document internalCreateDocument() {
		DOMDocumentForAngular document = new DOMDocumentForAngular();
		document.setModel(this);
		return document;
	}

	protected XMLModelParser createModelParser() {
		return new AngularDOMModelParser(this);
	}

	protected XMLModelUpdater createModelUpdater() {
		return new AngularDOMModelUpdater(this);
	}

	@Override
	public IndexedRegion getIndexedRegion(int offset) {
		IndexedRegion result = super.getIndexedRegion(offset);
		if (result == null && offset == getDocument().getEndOffset()) {
			return (IDOMNode) getDocument().getLastChild();
		}
		return result;
	}
}
