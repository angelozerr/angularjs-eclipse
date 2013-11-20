package org.eclipse.angularjs.internal.ui.contentassist;

import org.eclipse.angularjs.core.modules.AngularModulesManager;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.modules.IDirectiveCollector;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;

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
								.getDescription();
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
}
