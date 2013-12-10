package org.eclipse.angularjs.internal.ui.contentassist;

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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.MarkupCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tern.eclipse.ide.core.EclipseTernProject;
import tern.server.ITernCompletionCollector;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.angular.TernAngularQuery;
import tern.server.protocol.angular.TernAngularQuery.AngularType;
import tern.utils.IOUtils;

public class HTMLAngularTagsCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	@Override
	protected void addAttributeNameProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
		IStructuredDocumentRegion sdRegion = contentAssistRequest
				.getDocumentRegion();

		String tagName = node.getNodeName();
		String directiveName = contentAssistRequest.getMatchString();
		AngularModulesManager.getInstance().collectDirectives(tagName,
				directiveName, false, new IDirectiveCollector() {

					@Override
					public boolean add(Directive directive, String name) {

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

						return true;
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
		IDOMAttr attr = DOMUtils.getAttrByRegion(element,
				contentAssistRequest.getRegion());
		Directive directive = AngularModulesManager.getInstance().getDirective(
				element.getNodeName(), attr.getName());
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
			query.setStartsWith(startsWith);

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
