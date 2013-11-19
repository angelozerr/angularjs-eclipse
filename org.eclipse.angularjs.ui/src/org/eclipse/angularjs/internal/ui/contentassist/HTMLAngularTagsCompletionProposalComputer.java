package org.eclipse.angularjs.internal.ui.contentassist;

import org.eclipse.angularjs.core.modules.AngulaModulesManager;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.modules.IDirectiveCollector;
import org.eclipse.angularjs.internal.core.documentModel.handler.AngularModelHandler;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
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
		AngulaModulesManager.getInstance().collectDirectives(tagName,
				directiveName, false, new IDirectiveCollector() {

					@Override
					public boolean add(Directive directive, String name) {
						String proposedText = name;
						int cursorAdjustment = getCursorPositionForProposedText(proposedText);

						String requiredName = name;
						CustomCompletionProposal proposal = new CustomCompletionProposal(
								proposedText,
								contentAssistRequest
										.getReplacementBeginPosition(),
								contentAssistRequest.getReplacementLength(),
								cursorAdjustment,
								HTMLEditorPluginImageHelper
										.getInstance()
										.getImage(
												HTMLEditorPluginImages.IMG_OBJ_TAG_GENERIC),
								requiredName, null, null,
								XMLRelevanceConstants.R_TAG_NAME);
						contentAssistRequest.addProposal(proposal);

						return true;
					}
				});
		super.addAttributeNameProposals(contentAssistRequest, context);
	}
}
