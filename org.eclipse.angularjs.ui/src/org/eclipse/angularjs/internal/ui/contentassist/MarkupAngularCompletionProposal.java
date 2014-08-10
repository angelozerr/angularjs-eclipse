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
import org.eclipse.swt.widgets.Shell;

import tern.angular.AngularType;
import tern.eclipse.ide.ui.TernUIPlugin;
import tern.eclipse.jface.contentassist.TernCompletionProposal;
import tern.server.ITernServer;

/**
 * Extrends Tern completion proposal to display "module" and "controller"
 * information from the tern completion.
 * 
 */
public class MarkupAngularCompletionProposal extends TernCompletionProposal {

	private final ITernServer ternServer;
	private final Object completion;

	public MarkupAngularCompletionProposal(String name, String type,
			String doc, String url, String origin, int pos, Object completion,
			ITernServer server, AngularType angularType, int startOffset) {
		super(name, type, doc, url, origin, pos, startOffset);
		this.ternServer = server;
		this.completion = completion;
	}

	@Override
	public String getAdditionalProposalInfo() {
		String module = ternServer.getText(completion, "module");
		String controller = ternServer.getText(completion, "controller");
		AngularType angularType = AngularType.get(ternServer.getText(
				completion, "angularType"));
		return HTMLAngularPrinter.getAngularInfo(getType(), getName(),
				module, controller, angularType, super.getDoc(), getOrigin());
	}

	@Override
	protected Shell getActiveWorkbenchShell() {
		return TernUIPlugin.getActiveWorkbenchShell();
	}
}
