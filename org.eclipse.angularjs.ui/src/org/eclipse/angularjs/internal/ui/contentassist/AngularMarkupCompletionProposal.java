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

import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.ui.internal.contentassist.MarkupCompletionProposal;

import tern.eclipse.ide.ui.TernUIPlugin;
import tern.eclipse.jface.text.HoverControlCreator;
import tern.eclipse.jface.text.PresenterControlCreator;

/**
 * Extends WTP {@link MarkupCompletionProposal} to use
 * {@link BrowserInformationControl}.
 */
public class AngularMarkupCompletionProposal extends MarkupCompletionProposal
		implements ICompletionProposalExtension3 {

	private IInformationControlCreator ternControlCreator;

	public AngularMarkupCompletionProposal(String string,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance) {
		super(string, replacementOffset, replacementLength, cursorPosition,
				image, displayString, contextInformation,
				additionalProposalInfo, relevance);
	}

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		Shell shell = TernUIPlugin.getActiveWorkbenchShell();
		if (shell == null || !BrowserInformationControl.isAvailable(shell))
			return null;

		if (ternControlCreator == null) {
			PresenterControlCreator presenterControlCreator = new PresenterControlCreator();
			ternControlCreator = new HoverControlCreator(
					presenterControlCreator, true);
		}
		return ternControlCreator;
	}

	@Override
	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return getReplacementOffset();
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return null;
	}

}
