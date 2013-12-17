/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.contentassist;

import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.modules.AngularModulesManager;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.modules.IDirectiveCollector;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HTMLTernAngularHelper;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.MarkupCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Element;

import tern.eclipse.ide.core.IDETernProject;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.angular.AngularType;
import tern.server.protocol.angular.TernAngularQuery;
import tern.server.protocol.angular.completions.TernAngularCompletionsQuery;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionItem;

/**
 * Completion in HTML editor for :
 * 
 * <ul>
 * <li>attribute name with angular directive (ex : ng-app).</li>
 * <li>attribute value with angular module, controller, model.</li>
 * </ul>
 * 
 */
public class HTMLAngularTagsCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	@Override
	protected void addAttributeNameProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

		// completion for attribute name with angular directive (ex : ng-app)
		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();
		String tagName = element.getNodeName();
		String directiveName = contentAssistRequest.getMatchString();
		IDOMAttr attr = DOMUtils.getAttrByRegion(element,
				contentAssistRequest.getRegion());
		// get angular attribute name of the element
		final List<String> existingDirectiveNames = DOMUtils
				.getAngularDirectiveNames(
						element instanceof Element ? (Element) element : null,
						attr);

		// Starts directives completion.
		AngularModulesManager.getInstance().collectDirectives(tagName,
				directiveName, false, new IDirectiveCollector() {

					@Override
					public void add(Directive directive, String name) {

						if (existingDirectiveNames.contains(directive.getName())) {
							// The directive already exists in the element,
							// completion should not show it.
							return;
						}

						// Add the directive in the completion.
						String replacementString = name + "=\"\"";
						int replacementOffset = contentAssistRequest
								.getReplacementBeginPosition();
						int replacementLength = contentAssistRequest
								.getReplacementLength();
						int cursorPosition = getCursorPositionForProposedText(replacementString);

						Image image = ImageResource
								.getImage(ImageResource.IMG_DIRECTIVE);
						String displayString = name + " - "
								+ directive.getModule().getName();
						IContextInformation contextInformation = null;
						String additionalProposalInfo = directive
								.getHTMLDescription();
						int relevance = XMLRelevanceConstants.R_XML_ATTRIBUTE_NAME;

						ICompletionProposal proposal = new CustomCompletionProposal(
								replacementString, replacementOffset,
								replacementLength, cursorPosition, image,
								displayString, contextInformation,
								additionalProposalInfo, relevance);
						contentAssistRequest.addProposal(proposal);

					}
				});
		super.addAttributeNameProposals(contentAssistRequest, context);
	}

	@Override
	protected void addAttributeValueProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();
		// is Angular directive attribute?
		Directive directive = DOMUtils.getAngularDirective(element,
				contentAssistRequest.getRegion());
		AngularType angularType = directive != null ? directive.getType()
				: null;
		if (angularType == null) {
			return;
		}

		populateAngularProposals(contentAssistRequest, element, angularType,
				false);
		super.addAttributeValueProposals(contentAssistRequest, context);
	}

	private void populateAngularProposals(
			final ContentAssistRequest contentAssistRequest, IDOMNode element,
			AngularType angularType, final boolean insideExpression) {
		IFile file = DOMUtils.getFile(element);
		IProject eclipseProject = file.getProject();
		try {
			IDETernProject ternProject = AngularProject
					.getTernProject(eclipseProject);

			// Create query
			TernAngularQuery query = new TernAngularCompletionsQuery(
					angularType);
			String startsWith = contentAssistRequest.getMatchString();
			if (insideExpression) {

			} else {
				if (startsWith.startsWith("\"")) {
					startsWith = startsWith.substring(1, startsWith.length());
				}
			}
			query.setExpression(startsWith);

			final Image image = getImage(angularType);

			TernDoc doc = HTMLTernAngularHelper.createDoc(element, file,
					ternProject.getFileManager(), query);

			ITernCompletionCollector collector = new ITernCompletionCollector() {

				@Override
				public void addProposal(String name, String type,
						String origin, Object doc, int pos) {

					
					String replacementString = insideExpression ? name : "\""
							+ name + "\"";
					int replacementOffset = contentAssistRequest
							.getReplacementBeginPosition();
					int replacementLength = contentAssistRequest
							.getReplacementLength();
					int cursorPosition = getCursorPositionForProposedText(replacementString);

					TernCompletionItem item = new TernCompletionItem(name, type, origin);
					
					String displayString = item.getText();
					IContextInformation contextInformation = null;
					String additionalProposalInfo = null;
					int relevance = insideExpression ? XMLRelevanceConstants.R_ENTITY
							: XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE;

					ICompletionProposal proposal = new MarkupCompletionProposal(
							replacementString, replacementOffset,
							replacementLength, cursorPosition, image,
							displayString, contextInformation,
							additionalProposalInfo, relevance);
					contentAssistRequest.addProposal(proposal);

				}
			};

			ITernServer ternServer = ternProject.getTernServer();
			ternServer.request(doc, collector);

		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error while tern completion.", e);
		}
	}

	private Image getImage(AngularType angularType) {
		switch (angularType) {
		case module:
			return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
		case controller:
			return ImageResource.getImage(ImageResource.IMG_CONTROLLER);
		}
		return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
	}

	@Override
	protected ContentAssistRequest computeCompletionProposals(
			String matchString, ITextRegion completionRegion,
			IDOMNode treeNode, IDOMNode xmlnode,
			CompletionProposalInvocationContext context) {
		String regionType = completionRegion.getType();
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN
				|| regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {

			int documentPosition = context.getInvocationOffset();
			IStructuredDocumentRegion documentRegion = ContentAssistUtils
					.getStructuredDocumentRegion(context.getViewer(),
							documentPosition);

			int length = documentPosition - documentRegion.getStartOffset();
			String match = documentRegion.getText().substring(2, length);

			ContentAssistRequest contentAssistRequest = new ContentAssistRequest(
					xmlnode, xmlnode.getParentNode(), documentRegion,
					completionRegion, documentPosition, 0, match);

			populateAngularProposals(contentAssistRequest, xmlnode,
					AngularType.model, true);

			return contentAssistRequest;
		}
		return super.computeCompletionProposals(matchString, completionRegion,
				treeNode, xmlnode, context);
	}

}
