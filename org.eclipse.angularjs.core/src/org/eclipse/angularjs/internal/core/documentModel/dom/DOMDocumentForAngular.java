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

import org.eclipse.wst.html.core.internal.document.DocumentStyleImpl;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentForAngular extends DocumentStyleImpl {

	public DOMDocumentForAngular() {
		super();
	}

	protected DOMDocumentForAngular(DocumentImpl that) {
		super(that);
	}

	public Node cloneNode(boolean deep) {
		DOMDocumentForAngular cloned = new DOMDocumentForAngular(this);
		if (deep)
			cloned.importChildNodes(this, true);
		return cloned;
	}

	/**
	 * createElement method
	 * 
	 * @return org.w3c.dom.Element
	 * @param tagName
	 *            java.lang.String
	 */
	public Element createElement(String tagName) throws DOMException {
		checkTagNameValidity(tagName);

		ElementImplForAngular element = new ElementImplForAngular();
		element.setOwnerDocument(this);
		element.setTagName(tagName);
		return element;
	}

	/**
	 * createAttribute method
	 * 
	 * @return org.w3c.dom.Attr
	 * @param name
	 *            java.lang.String
	 */
	public Attr createAttribute(String name) throws DOMException {
		AttrImplForAngular attr = new AttrImplForAngular();
		attr.setOwnerDocument(this);
		attr.setName(name);
		return attr;
	}

	/**
	 * createTextNode method
	 * 
	 * @return org.w3c.dom.Text
	 * @param data
	 *            java.lang.String
	 */
	public Text createTextNode(String data) {
		TextImplForAngular text = new TextImplForAngular();
		text.setOwnerDocument(this);
		text.setData(data);
		return text;
	}

	public void setModel(IDOMModel model) {
		super.setModel(model);
	}
}
