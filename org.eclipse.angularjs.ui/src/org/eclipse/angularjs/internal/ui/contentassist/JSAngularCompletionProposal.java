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
package org.eclipse.angularjs.internal.ui.contentassist;

import org.eclipse.angularjs.internal.ui.utils.HTMLAngularPrinter;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;

import tern.angular.AngularType;
import tern.eclipse.ide.ui.contentassist.JSTernCompletionProposal;
import tern.server.ITernServer;

/**
 * Extrends Javacsript Tern completion proposal to display "module" and
 * "controller" information from the tern completion.
 * 
 */
public class JSAngularCompletionProposal extends JSTernCompletionProposal
		implements IRelevanceCompletionProposal {

	private final ITernServer ternServer;
	private final Object completion;

	public JSAngularCompletionProposal(String name, String type, String doc,
			String url, String origin, int pos, Object completion,
			ITernServer server, AngularType angularType, int startOffset) {
		super(name, type, doc, url, origin, pos, startOffset);
		this.ternServer = server;
		this.completion = completion;
		super.setTriggerCharacters(new char[] {'.'});
	}

	@Override
	public String getAdditionalProposalInfo() {
		String module = ternServer.getText(completion, "module");
		String controller = ternServer.getText(completion, "controller");
		return HTMLAngularPrinter
				.getAngularInfo(this, null, module, controller);
	}

	// @Override
	protected String getAdditionalProposalInfoTitle() {
		String module = ternServer.getText(completion, "module");
		String controller = ternServer.getText(completion, "controller");

		StringBuilder title = new StringBuilder(getName());
		if (module != null || controller != null) {
			title.append(" <small>in </small>");
		}
		if (controller != null) {
			title.append("<b>");
			title.append(controller);
			title.append("</b>");
			title.append("<small> controller</small>");
			if (module != null) {
				title.append("<small> of </small>");
				title.append("<b>");
				title.append(module);
				title.append("</b>");
				title.append("<small> module</small>");
			}
		} else if (module != null) {
			title.append("<b>");
			title.append(module);
			title.append("</b>");
			title.append("<small> module</small>");
		}
		return title.toString();
	}

	@Override
	public int getRelevance() {
		// Since this proposal is limited only to Angular Expression regions, 
		// it should be higher than any of tag or attribute value proposals
		return XMLRelevanceConstants.R_STRICTLY_VALID_TAG_NAME + 5;
	}
}
