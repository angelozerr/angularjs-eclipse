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
import org.eclipse.angularjs.core.modules.DirectiveType;
import org.eclipse.angularjs.core.modules.IDirectiveCollector;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.StringUtils;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.MarkupCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tern.eclipse.ide.core.EclipseTernProject;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.angular.TernAngularQuery;
import tern.server.protocol.angular.TernAngularQuery.AngularType;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.utils.IOUtils;

/**
 * Completion in HTML editor for :
 * 
 * <ul>
 * <li>attribute name with angular directive (ex : ng-app).</li>
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
		// get angular attribute name of the element
		final List<String> existingDirectiveNames = DOMUtils
				.getAngularDirectiveNames(element instanceof Element ? (Element) element
						: null);

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
								.getImage(ImageResource.IMG_ANGULARJS);
						String displayString = name;
						IContextInformation contextInformation = null;
						String additionalProposalInfo = directive
								.getHTMLDescription();
						int relevance = XMLRelevanceConstants.R_XML_ATTRIBUTE_NAME;

						CustomCompletionProposal proposal = new CustomCompletionProposal(
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
		AngularType angularType = getAngularType(directive);
		if (angularType == null) {
			return;
		}

		IFile file = DOMUtils.getFile(element);
		IProject eclipseProject = file.getProject();
		try {
			EclipseTernProject ternProject = AngularProject
					.getTernProject(eclipseProject);
			ITernServer ternServer = ternProject.getTernServer();

			TernAngularQuery query = new TernAngularQuery(angularType);
			String startsWith = contentAssistRequest.getMatchString();
			if (startsWith.startsWith("\"")) {
				startsWith = startsWith.substring(1, startsWith.length());
			}
			query.setExpression(startsWith);

			TernDoc doc = new TernDoc();
			doc.setQuery(query);

			// loop for each script elements (how to improve that?)
			Element scriptElt = null;
			String src = null;
			NodeList scripts = element.getOwnerDocument().getElementsByTagName(
					"script");
			for (int i = 0; i < scripts.getLength(); i++) {
				scriptElt = (Element) scripts.item(i);
				src = scriptElt.getAttribute("src");
				if (StringUtils.isEmpty(src)) {
					// TODO : get text content
				} else {
					if (src.startsWith("http")) {
						// TODO : manage this case
					} else {

						IContainer parent = file.getParent();
						IFile scriptFile = parent.getFile(new Path(src));
						if (scriptFile != null && scriptFile.exists()) {
							String name = scriptFile.getName();
							String text = IOUtils.toString(
									scriptFile.getContents(),
									scriptFile.getCharset());
							doc.addFile(name, text, null);
							query.setFile("#0");
						}
					}
				}
			}

			ITernCompletionCollector collector = new ITernCompletionCollector() {

				@Override
				public void addProposal(String name, String type, Object doc,
						int pos) {

					String replacementString = "\"" + name + "\"";
					int replacementOffset = contentAssistRequest
							.getReplacementBeginPosition();
					int replacementLength = contentAssistRequest
							.getReplacementLength();
					int cursorPosition = getCursorPositionForProposedText(replacementString);

					Image image = ImageResource
							.getImage(ImageResource.IMG_ANGULARJS);
					String displayString = name;
					IContextInformation contextInformation = null;
					String additionalProposalInfo = null;
					int relevance = XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE;

					CustomCompletionProposal proposal = new MarkupCompletionProposal(
							replacementString, replacementOffset,
							replacementLength, cursorPosition, image,
							displayString, contextInformation,
							additionalProposalInfo, relevance);
					contentAssistRequest.addProposal(proposal);

				}
			};

			ternServer.request(doc, collector);

		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error while tern completion.", e);
		}
		super.addAttributeValueProposals(contentAssistRequest, context);
	}

	private AngularType getAngularType(Directive directive) {
		if (directive == null) {
			return null;
		}
		DirectiveType type = directive.getType();
		if (type == null) {
			return null;
		}
		switch (type) {
		case module:
			return AngularType.module;
		case controller:
			return AngularType.controller;
		}
		return null;
	}
}
