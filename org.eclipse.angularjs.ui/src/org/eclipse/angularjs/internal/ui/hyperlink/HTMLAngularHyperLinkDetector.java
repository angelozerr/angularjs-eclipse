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

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.angularjs.internal.ui.AngularScopeHelper;
import org.eclipse.angularjs.internal.ui.JavaWordFinder;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.text.XMLStructuredDocumentRegion;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.eclipse.ide.core.IDETernProject;

/**
 * 
 * HTML Angular HyperLink Detector.
 */
public class HTMLAngularHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}
		IDocument document = textViewer.getDocument();
		// Get the selected Node.
		IDOMNode currentNode = DOMUtils.getNodeByOffset(document,
				region.getOffset());
		if (currentNode == null) {
			return null;
		}

		IFile file = DOMUtils.getFile(currentNode);
		IProject project = file.getProject();
		IHyperlink hyperlink = null;
		if (IDETernProject.hasTernNature(project)) {
			try {

				IStructuredDocumentRegion documentRegion = ContentAssistUtils
						.getStructuredDocumentRegion(textViewer,
								region.getOffset());

				IDETernProject ternProject = AngularProject
						.getTernProject(project);
				switch (currentNode.getNodeType()) {
				case Node.TEXT_NODE:
					hyperlink = createHyperlinkForExpression(currentNode,
							document, documentRegion, region.getOffset(),
							ternProject, file);
					return createHyperlinks(hyperlink);
				case Node.ATTRIBUTE_NODE:
				case Node.ELEMENT_NODE:
					break;
				}

				// Get selected attribute
				IDOMAttr attr = DOMUtils.getAttrByOffset(currentNode,
						region.getOffset());

				IDOMNode node = attr != null ? attr : currentNode;
				Directive directive = DOMUtils.getAngularDirective(project,
						node);
				if (directive != null) {
					Integer end = null;
					if (attr != null) {
						boolean isAttrValue = region.getOffset() > attr
								.getNameRegionEndOffset();
						if (isAttrValue) {
							IRegion valueRegion = JavaWordFinder.findWord(
									document, region.getOffset());
							// Hyperlink on attr value
							end = region.getOffset()
									- attr.getValueRegionStartOffset() - 1;
							// the attribute is directive, try to open the
							// angular element controller, module, mode.
							hyperlink = new HTMLAngularHyperLink(
									attr.getOwnerElement(), valueRegion, file,
									ternProject,
									AngularScopeHelper.getAngularValue(attr,
											directive.getType()), end,
									directive.getType());

						} else {
							// Hyperlink on attr name, try to open the custom
							// directive
							if (directive.isCustom()) {
								hyperlink = new HTMLAngularHyperLink(
										attr.getOwnerElement(),
										HyperlinkUtils.getNameRegion(attr),
										file, ternProject, directive.getName(),
										end, AngularType.directive);
							}
						}
					} else {
						// Hyperlink on element name, try to open the custom
						// directive
						if (directive.isCustom()) {
							IDOMElement element = (IDOMElement) node;
							hyperlink = new HTMLAngularHyperLink(element,
									HyperlinkUtils.getElementRegion(element),
									file, ternProject, directive.getName(),
									end, AngularType.directive);
						}
					}
				}
			} catch (CoreException e) {
				Trace.trace(Trace.WARNING, "Error while Angular hyperlink", e);
			}
		}
		return createHyperlinks(hyperlink);
	}

	private IHyperlink createHyperlinkForExpression(IDOMNode node,
			IDocument document, IStructuredDocumentRegion documentRegion,
			int documentPosition, IDETernProject ternProject, IFile file) {
		IHyperlink hyperlink = null;
		String regionType = documentRegion.getType();
		int end = documentPosition - documentRegion.getStartOffset();
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
			// case for angular expression
			String expression = documentRegion.getText();
			end = end - AngularProject.START_ANGULAR_EXPRESSION_TOKEN.length();
			expression = HyperlinkUtils.getExpressionContent(expression);
			return new HTMLAngularHyperLink(node, JavaWordFinder.findWord(
					document, documentPosition), file, ternProject, expression,
					end, AngularType.model);

		} else if (regionType == DOMRegionContext.XML_CONTENT
				&& !DOMUtils.isAngularContentType(node)) {
			// case for JSP
		}
		return null;
	}

	private IHyperlink[] createHyperlinks(IHyperlink hyperlink) {
		if (hyperlink != null) {
			IHyperlink[] hyperlinks = new IHyperlink[1];
			hyperlinks[0] = hyperlink;
			return hyperlinks;
		}
		return null;
	}
}
