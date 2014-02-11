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

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.AngularScopeHelper;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.protocol.completions.TernCompletionItem;
import tern.server.protocol.type.ITernTypeCollector;
import tern.utils.StringUtils;

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
		if (DOMUtils.hasAngularNature(xmlnode)) {
			// Display Help of Angular Directive if it's an angular directive
			// attribute
			IDOMAttr attr = DOMUtils.getAttrByRegion(xmlnode, region);
			IProject project = DOMUtils.getFile(attr).getProject();
			Directive directive = DOMUtils.getAngularDirective(project, attr);
			if (directive != null) {
				return directive.getHTMLDescription();
			} else {
				// Check if it's a directive parameter which is hovered.
				DirectiveParameter parameter = DOMUtils
						.getAngularDirectiveParameter(project, attr);
				if (parameter != null) {
					return parameter.getHTMLDescription();
				}
			}
		}
		// Here the attribute is not a directive, display classic Help.
		return super.computeTagAttNameHelp(xmlnode, parentNode, flatNode,
				region);
	}

	@Override
	protected String computeTagAttValueHelp(IDOMNode xmlnode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		if (DOMUtils.hasAngularNature(xmlnode)) {
			IDOMAttr attr = DOMUtils.getAttrByRegion(xmlnode, region);
			IFile file = DOMUtils.getFile(attr);
			IProject project = file.getProject();
			Directive directive = DOMUtils.getAngularDirective(project, attr);
			if (directive != null) {
				try {
					IDETernProject ternProject = AngularProject
							.getTernProject(project);
					String help = find(attr, file, ternProject,
							directive.getType());
					if (!StringUtils.isEmpty(help)) {
						return help;
					}

				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error while tern hover.", e);
				}
			}
		}
		return super.computeTagAttValueHelp(xmlnode, parentNode, flatNode,
				region);
	}

	@Override
	protected String computeTagNameHelp(IDOMNode xmlnode, IDOMNode parentNode,
			IStructuredDocumentRegion flatNode, ITextRegion region) {
		// Display Help of Angular Directive if it's an angular directive
		// element
		if (DOMUtils.hasAngularNature(xmlnode) && xmlnode instanceof Element) {
			Element element = (Element) xmlnode;
			IProject project = DOMUtils.getFile(xmlnode).getProject();
			Directive directive = DOMUtils
					.getAngularDirective(project, element);
			if (directive != null) {
				return directive.getHTMLDescription();
			}
		}
		return super.computeTagNameHelp(xmlnode, parentNode, flatNode, region);
	}

	private String find(IDOMAttr attr, IFile file, IDETernProject ternProject,
			final AngularType angularType) throws Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(attr.getValue());
		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
				attr.getOwnerElement(), file, angularType, query);

		final StringBuilder help = new StringBuilder();
		ITernTypeCollector collector = new ITernTypeCollector() {

			@Override
			public void setType(String name, String type, String origin) {
				if (name != null) {

					TernCompletionItem item = new TernCompletionItem(name,
							type, origin);
					help.append("<b>Angular ");
					help.append(angularType.name());
					help.append("</b><br/><br/><b>Signature : </b>");
					help.append(item.getSignature());
					help.append("<br/><b>Origin : </b>");
					help.append(item.getOrigin());
				}
			}
		};
		if (scriptPath != null) {
			ternProject.request(query, query.getFiles(), scriptPath, collector);
		} else {
			ternProject.request(query, query.getFiles(), attr, file, collector);
		}
		return help.toString();
	}
}
