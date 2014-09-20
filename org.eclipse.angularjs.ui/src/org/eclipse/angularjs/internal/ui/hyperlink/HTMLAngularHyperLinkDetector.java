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
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.AngularELRegion;
import org.eclipse.angularjs.core.utils.AngularRegionUtils;
import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.angularjs.internal.ui.AngularELWordFinder;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.angularjs.internal.ui.utils.DOMUIUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.eclipse.ide.core.IIDETernProject;

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
		if (AngularProject.hasAngularNature(project)) {
			try {

				IStructuredDocumentRegion documentRegion = DOMUIUtils
						.getStructuredDocumentRegion(textViewer,
								region.getOffset());

				IIDETernProject ternProject = AngularProject
						.getTernProject(project);
				String startSymbol = AngularProject.DEFAULT_START_SYMBOL;
				String endSymbol = AngularProject.DEFAULT_END_SYMBOL;
				try {
					AngularProject angularProject = AngularProject
							.getAngularProject(project);
					startSymbol = angularProject.getStartSymbol();
					endSymbol = angularProject.getEndSymbol();
				} catch (CoreException e) {
				}
				switch (currentNode.getNodeType()) {
				case Node.TEXT_NODE:
					hyperlink = createHyperlinkForExpression(
							documentRegion.getType(), documentRegion.getText(),
							documentRegion.getStartOffset(),
							region.getOffset(), currentNode, document,
							ternProject, file, startSymbol, endSymbol);
					return createHyperlinks(hyperlink);
				case Node.ELEMENT_NODE:
					// Get selected attribute
					IDOMAttr attr = DOMUtils.getAttrByOffset(currentNode,
							region.getOffset());

					IDOMNode node = attr != null ? attr : currentNode;
					Directive directive = AngularDOMUtils.getAngularDirective(
							project, node);
					if (directive != null) {
						Integer end = null;
						if (attr != null) {
							boolean isAttrValue = region.getOffset() > attr
									.getNameRegionEndOffset();
							if (isAttrValue) {
								IRegion valueRegion = AngularELWordFinder
										.findWord(document, region.getOffset(),
												startSymbol, endSymbol);
								// Hyperlink on attr value
								end = region.getOffset()
										- attr.getValueRegionStartOffset() - 1;
								// the attribute is directive, try to open the
								// angular element controller, module, mode.
								hyperlink = new HTMLAngularHyperLink(
										attr.getOwnerElement(), valueRegion,
										file, textViewer.getDocument(),
										ternProject,
										AngularScopeHelper.getAngularValue(
												attr, directive.getType()),
										end, directive.getType());

							} else {
								// Hyperlink on attr name, try to open the
								// custom
								// directive
								if (directive.isCustom()) {
									hyperlink = new HTMLAngularHyperLink(
											attr.getOwnerElement(),
											HyperlinkUtils.getNameRegion(attr),
											file, textViewer.getDocument(),
											ternProject, directive.getName(),
											end, AngularType.directive);
								}
							}
						} else {
							// Hyperlink on element name, try to open the custom
							// directive
							if (directive.isCustom()) {
								IDOMElement element = (IDOMElement) node;
								hyperlink = new HTMLAngularHyperLink(element,
										HyperlinkUtils
												.getElementRegion(element),
										file, textViewer.getDocument(),
										ternProject, directive.getName(), end,
										AngularType.directive);
							}
						}
					} else {
						if (attr != null) {
							boolean isAttrValue = region.getOffset() > attr
									.getNameRegionEndOffset();
							if (isAttrValue) {
								// EL inside attribute
								// <span class="done-{{todo.done}}"
								hyperlink = createHyperlinkForExpression(
										DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE,
										attr.getValue(),
										attr.getValueRegionStartOffset() + 1,
										region.getOffset(),
										(IDOMNode) attr.getOwnerElement(),
										document, ternProject, file,
										startSymbol, endSymbol);
							}
						}
					}
				}
			} catch (CoreException e) {
				Trace.trace(Trace.WARNING, "Error while Angular hyperlink", e);
			}
		}
		return createHyperlinks(hyperlink);
	}

	private IHyperlink createHyperlinkForExpression(String regionType,
			String regionText, int regionStartOffset, int documentPosition,
			IDOMNode node, IDocument document, IIDETernProject ternProject,
			IFile file, String startSymbol, String endSymbol) {
		AngularELRegion angularRegion = AngularRegionUtils.getAngularELRegion(
				regionType, regionText, regionStartOffset, documentPosition,
				file.getProject());
		if (angularRegion != null) {
			String expression = angularRegion.getExpression();
			int expressionOffset = angularRegion.getExpressionOffset();
			return new HTMLAngularHyperLink(node, AngularELWordFinder.findWord(
					document, documentPosition, startSymbol, endSymbol), file,
					document, ternProject, expression, expressionOffset,
					AngularType.model);
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
