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
package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Node;

import tern.ITernFile;
import tern.angular.AngularType;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.eclipse.ide.ui.hyperlink.AbstractTernHyperlink;
import tern.scriptpath.ITernScriptPath;

/**
 * HTML angular element hyperlink.
 */
public class HTMLAngularHyperLink extends AbstractTernHyperlink {

	private final Node node;
	private final IFile file;
	private final IDocument document;
	private final String expression;
	private Integer end;
	private final AngularType angularType;

	public HTMLAngularHyperLink(Node node, IRegion region, IFile file,
			IDocument document, IIDETernProject ternProject, String expression,
			Integer end, AngularType angularType) {
		super(region, ternProject);
		this.node = node;
		this.file = file;
		this.document = document;
		this.expression = expression;
		this.end = end;
		this.angularType = angularType;
	}

	@Override
	public void open() {
		try {
			TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
					angularType);
			query.setExpression(expression);
			query.setEnd(end);
			ITernScriptPath scriptPath = AngularScopeHelper.populateScope(node,
					file, angularType, query);
			if (scriptPath != null) {
				ternProject.request(query, query.getFiles(), scriptPath, null, null, this);
			} else {
				ITernFile tf = new TernDocumentFile(file, document);
				ternProject.request(query, query.getFiles(), null, node, tf, this);
			}
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

}
