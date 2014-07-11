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
package org.eclipse.angularjs.internal.ui.taginfo;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.AngularScopeHelper;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.angularjs.internal.ui.utils.HTMLAngularPrinter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IInformationControlCreator;
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
import tern.eclipse.ide.ui.utils.HTMLTernPrinter;
import tern.eclipse.jface.text.HoverControlCreator;
import tern.eclipse.jface.text.PresenterControlCreator;
import tern.utils.StringUtils;

/**
 * Provides hover help documentation for Angular tags
 * 
 */
public class HTMLAngularTagInfoHoverProcessor extends HTMLTagInfoHoverProcessor {

	private IInformationControlCreator fHoverControlCreator;
	private IInformationControlCreator fPresenterControlCreator;

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
				return HTMLAngularPrinter.getDirectiveInfo(directive);
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
		return formatAsAdvancedHTML(super.computeTagAttNameHelp(xmlnode,
				parentNode, flatNode, region));
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
		return formatAsAdvancedHTML(super.computeTagAttValueHelp(xmlnode,
				parentNode, flatNode, region));
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
				return HTMLAngularPrinter.getDirectiveInfo(directive);
			}
		}
		return formatAsAdvancedHTML(super.computeTagNameHelp(xmlnode,
				parentNode, flatNode, region));
	}

	private String formatAsAdvancedHTML(String html) {
		if (StringUtils.isEmpty(html)) {
			return html;
		}
		StringBuffer advancedHTML = new StringBuffer(html);
		HTMLTernPrinter.endPage(advancedHTML);
		return advancedHTML.toString();
	}

	private String find(IDOMAttr attr, IFile file, IDETernProject ternProject,
			final AngularType angularType) throws Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(AngularScopeHelper.getAngularValue(attr,
				angularType));
		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
				attr.getOwnerElement(), file, angularType, query);

		HTMLAngularTernTypeCollector collector = new HTMLAngularTernTypeCollector(
				angularType);
		if (scriptPath != null) {
			ternProject.request(query, query.getFiles(), scriptPath, collector);
		} else {
			ternProject.request(query, query.getFiles(), attr, file, collector);
		}
		return collector.getInfo();
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator = new HoverControlCreator(
					getInformationPresenterControlCreator());
		return fHoverControlCreator;
	}

	// @Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator = new PresenterControlCreator();
		return fPresenterControlCreator;
	}
}
