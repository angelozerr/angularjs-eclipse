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
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.AngularELRegion;
import org.eclipse.angularjs.core.utils.AngularRegionUtils;
import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.AngularELWordFinder;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.angularjs.internal.ui.utils.HTMLAngularPrinter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tern.ITernFile;
import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.eclipse.ide.ui.hover.HTMLTernTypeCollector;
import tern.eclipse.ide.ui.hover.IDEHoverControlCreator;
import tern.eclipse.ide.ui.hover.IDEPresenterControlCreator;
import tern.eclipse.ide.ui.hover.ITernHoverInfoProvider;
import tern.eclipse.jface.text.TernBrowserInformationControlInput;
import tern.scriptpath.ITernScriptPath;
import tern.utils.StringUtils;

/**
 * Provides hover help documentation for Angular tags
 * 
 */
public class HTMLAngularTagInfoHoverProcessor extends HTMLTagInfoHoverProcessor
		implements ITextHoverExtension2, IInformationProviderExtension2,
		ITernHoverInfoProvider {

	private IInformationControlCreator fHoverControlCreator;
	private IDEPresenterControlCreator fPresenterControlCreator;
	private IIDETernProject ternProject;

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		TernBrowserInformationControlInput info = (TernBrowserInformationControlInput) getHoverInfo2(
				textViewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	@Override
	public Object getHoverInfo2(ITextViewer viewer, IRegion hoverRegion) {
		if ((hoverRegion == null) || (viewer == null)
				|| (viewer.getDocument() == null)) {
			return null;
		}

		int documentOffset = hoverRegion.getOffset();
		String displayText = computeHoverHelp(viewer, documentOffset);

		if (displayText == null) {
			return null;
		}

		return new TernBrowserInformationControlInput(null, displayText, 200);
	}

	@Override
	protected String computeTagAttNameHelp(IDOMNode xmlnode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		if (AngularDOMUtils.hasAngularNature(xmlnode)) {
			// Display Help of Angular Directive if it's an angular directive
			// attribute
			IDOMAttr attr = DOMUtils.getAttrByRegion(xmlnode, region);
			IProject project = DOMUtils.getFile(attr).getProject();
			Directive directive = AngularDOMUtils.getAngularDirective(project,
					attr);
			if (directive != null) {
				return HTMLAngularPrinter.getDirectiveInfo(directive);
			} else {
				// Check if it's a directive parameter which is hovered.
				DirectiveParameter parameter = AngularDOMUtils
						.getAngularDirectiveParameter(project, attr);
				if (parameter != null) {
					return HTMLAngularPrinter
							.getDirectiveParameterInfo(parameter);
				}
			}
		}
		// Here the attribute is not a directive, display classic Help.
		return super.computeTagAttNameHelp(xmlnode, parentNode, flatNode,
				region);
	}

	protected String computeTagAttValueHelp(IDOMNode xmlnode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region, IDocument document, int documentPosition) {
		if (AngularDOMUtils.hasAngularNature(xmlnode)) {
			IDOMAttr attr = DOMUtils.getAttrByRegion(xmlnode, region);
			IFile file = DOMUtils.getFile(attr);
			IProject project = file.getProject();
			Directive directive = AngularDOMUtils.getAngularDirective(project,
					attr);
			try {
				IIDETernProject ternProject = AngularProject
						.getTernProject(project);
				if (directive != null) {
					String expression = AngularScopeHelper.getAngularValue(
							attr, directive.getType());
					Integer expressionOffset = documentPosition
							- attr.getValueRegionStartOffset();
					String help = computeHelp(attr, expression,
							expressionOffset, file, document, ternProject,
							directive.getType());
					if (!StringUtils.isEmpty(help)) {
						return help;
					}

				} else {
					AngularELRegion angularRegion = AngularRegionUtils
							.getAngularELRegion(
									DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE,
									attr.getValue(),
									attr.getValueRegionStartOffset() + 1,
									documentPosition, project);
					if (angularRegion != null) {
						// angular expression inside attribute value
						// <span class="done-{{todo.done}}"
						String expression = angularRegion.getExpression();
						int expressionOffset = angularRegion
								.getExpressionOffset();
						String help = computeHelp(attr, expression,
								expressionOffset, file, document, ternProject,
								AngularType.model);
						if (!StringUtils.isEmpty(help)) {
							return help;
						}
					}
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error while tern hover.", e);
			}
		}
		return super.computeTagAttValueHelp(xmlnode, parentNode, flatNode,
				region);
	}

	@Override
	protected String computeHoverHelp(ITextViewer textViewer,
			int documentPosition) {
		String result = null;
		IndexedRegion treeNode = ContentAssistUtils.getNodeAt(textViewer,
				documentPosition);
		if (treeNode == null) {
			return null;
		}
		Node node = (Node) treeNode;

		while ((node != null) && (node.getNodeType() == Node.TEXT_NODE)
				&& (node.getParentNode() != null)) {
			node = node.getParentNode();
		}
		IDOMNode parentNode = (IDOMNode) node;

		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer
				.getDocument()).getRegionAtCharacterOffset(documentPosition);
		if (flatNode != null) {
			ITextRegion region = flatNode
					.getRegionAtCharacterOffset(documentPosition);
			if (region != null) {
				result = computeRegionHelp(treeNode, parentNode, flatNode,
						region, documentPosition, textViewer.getDocument());
			}
		}

		return result;
	}

	protected String computeRegionHelp(IndexedRegion treeNode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region, int documentPosition, IDocument document) {
		String result = null;
		if (region == null) {
			return null;
		}
		if (AngularDOMUtils.hasAngularNature(parentNode)) {
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_CONTENT) {
				return computeAngularExpressionHelp((IDOMNode) treeNode,
						parentNode, flatNode, region, document,
						documentPosition);
			} else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				return computeTagAttValueHelp((IDOMNode) treeNode, parentNode,
						flatNode, region, document, documentPosition);
			}
		}
		return super.computeRegionHelp(treeNode, parentNode, flatNode, region);
	}

	protected String computeAngularExpressionHelp(IDOMNode treeNode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region, IDocument document, int documentPosition) {
		IFile file = DOMUtils.getFile(treeNode);
		try {
			IIDETernProject ternProject = AngularProject.getTernProject(file
					.getProject());
			AngularELRegion angularRegion = AngularRegionUtils
					.getAngularELRegion(flatNode, documentPosition,
							file.getProject());
			if (angularRegion != null) {
				String expression = angularRegion.getExpression();
				int expressionOffset = angularRegion.getExpressionOffset() + 1;
				return computeHelp(treeNode, expression, expressionOffset,
						file, document, ternProject, AngularType.model);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error while tern hover.", e);
		}
		return null;
	}

	@Override
	protected String computeTagNameHelp(IDOMNode xmlnode, IDOMNode parentNode,
			IStructuredDocumentRegion flatNode, ITextRegion region) {
		// Display Help of Angular Directive if it's an angular directive
		// element
		if (AngularDOMUtils.hasAngularNature(xmlnode)
				&& xmlnode instanceof Element) {
			Element element = (Element) xmlnode;
			IProject project = DOMUtils.getFile(xmlnode).getProject();
			Directive directive = AngularDOMUtils.getAngularDirective(project,
					element);
			if (directive != null) {
				return HTMLAngularPrinter.getDirectiveInfo(directive);
			}
		}
		return super.computeTagNameHelp(xmlnode, parentNode, flatNode, region);
	}

	private String computeHelp(Node domNode, String expression, Integer end,
			IFile file, IDocument document, IIDETernProject ternProject,
			final AngularType angularType) throws Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(expression);
		query.setEnd(end);
		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
				DOMUtils.getOwnerElement(domNode), file, angularType, query);
		HTMLTernTypeCollector collector = createCollector(angularType);
		// update with the current tern project
		this.ternProject = ternProject;
		if (scriptPath != null) {
			ternProject.request(query, query.getFiles(), scriptPath, null,
					null, collector);
		} else {
			ITernFile tf = new TernDocumentFile(file, document);
			ternProject.request(query, query.getFiles(), null, domNode, tf,
					collector);
		}
		return collector.getInfo();
	}

	private HTMLTernTypeCollector createCollector(AngularType angularType) {
		if (angularType == AngularType.module
				|| angularType == AngularType.controller) {
			return new HTMLAngularTernTypeCollector();
		}
		return new HTMLTernTypeCollector();
	}

	/**
	 * Returns the region to hover the text over based on the offset.
	 * 
	 * @param textViewer
	 * @param offset
	 * 
	 * @return IRegion region to hover over if offset is within tag name,
	 *         attribute name, or attribute value and if offset is not over
	 *         invalid whitespace. otherwise, returns <code>null</code>
	 * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(ITextViewer, int)
	 */
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null)) {
			return null;
		}
		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer
				.getDocument()).getRegionAtCharacterOffset(offset);
		ITextRegion region = null;
		if (flatNode != null) {
			region = flatNode.getRegionAtCharacterOffset(offset);
		}
		if (region != null) {
			IDOMNode element = DOMUtils.getNodeByOffset(
					textViewer.getDocument(), offset);
			String startSymbol = AngularProject.DEFAULT_START_SYMBOL;
			String endSymbol = AngularProject.DEFAULT_END_SYMBOL;
			try {
				AngularProject angularProject = AngularProject
						.getAngularProject(DOMUtils.getFile(element)
								.getProject());
				startSymbol = angularProject.getStartSymbol();
				endSymbol = angularProject.getEndSymbol();
			} catch (CoreException e) {
			}
			// only supply hoverhelp for tag name, attribute name, or
			// attribute value
			String regionType = region.getType();
			if (DOMRegionContext.XML_CONTENT.equals(regionType)) {
				return AngularELWordFinder.findWord(textViewer.getDocument(),
						offset, startSymbol, endSymbol);
			}
			if ((regionType == DOMRegionContext.XML_TAG_NAME)
					|| (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
					|| (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				try {
					// check if we are at whitespace before or after line
					IRegion line = textViewer.getDocument()
							.getLineInformationOfOffset(offset);
					if ((offset > (line.getOffset()))
							&& (offset < (line.getOffset() + line.getLength()))) {
						// check if we are in region's trailing whitespace
						// (whitespace after relevant info)
						if (offset < flatNode.getTextEndOffset(region)) {
							if ((regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
								IDOMAttr attr = DOMUtils.getAttrByOffset(
										element, offset);
								if (AngularDOMUtils.isAngularDirective(attr)
										|| attr.getValue()
												.contains(startSymbol)) {
									return AngularELWordFinder.findWord(
											textViewer.getDocument(), offset,
											startSymbol, endSymbol);
								}
							}
						}
					}
				} catch (BadLocationException e) {
					Trace.trace(Trace.INFO, "Error while hovering.", e);
				}
			}
		}
		return null;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator = new IDEHoverControlCreator(
					getInformationPresenterControlCreator(), this);
		return fHoverControlCreator;
	}

	@Override
	public IDEPresenterControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator = new IDEPresenterControlCreator(this);
		return fPresenterControlCreator;
	}

	@Override
	public IIDETernProject getTernProject() {
		return ternProject;
	}

	@Override
	public ITernFile getFile() {
		return null;
	}

	@Override
	public Integer getOffset() {
		return null;
	}
}
