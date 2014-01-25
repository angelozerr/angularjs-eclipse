/*******************************************************************************
 * Copyright (c) 2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import tern.angular.AngularType;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.ui.hyperlink.AbstractTernHyperlink;
import tern.server.protocol.TernDoc;

/**
 * HTML angular element hyperlink.
 */
public class HTMLAngularHyperLink extends AbstractTernHyperlink {

	private final IDOMAttr attr;
	private final IFile file;
	private final AngularType angularType;

	public HTMLAngularHyperLink(IDOMAttr attr, IFile file,
			IDETernProject ternProject, AngularType angularType) {
		super(HyperlinkUtils.getHyperlinkRegion(attr), ternProject);
		this.attr = attr;
		this.file = file;
		this.angularType = angularType;
	}

	@Override
	public void open() {
		try {
			TernAngularDefinitionQuery query = createQuery();
			ternProject.request(query, query.getFiles(), attr, file, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getHyperlinkText() {
		return AngularUIMessages.HTMLAngularHyperLink_text;
	}

	@Override
	public String getTypeLabel() {
		return AngularUIMessages.HTMLAngularHyperLink_typeLabel;
	}

	public TernAngularDefinitionQuery createQuery() {
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				angularType);
		query.setExpression(attr.getValue());
		HTMLTernAngularHelper.populateScope(attr.getOwnerElement(),
				DOMSSEDirectiveProvider.getInstance(), query);
		return query;
	}

}
