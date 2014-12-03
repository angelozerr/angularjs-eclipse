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
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

import tern.ITernFile;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.ui.TernUIPlugin;
import tern.eclipse.ide.ui.hover.IDEHoverControlCreator;
import tern.eclipse.ide.ui.hover.IDEPresenterControlCreator;
import tern.eclipse.ide.ui.hover.ITernHoverInfoProvider;

/**
 * 
 * Extends WTP {@link CustomCompletionProposal} to use
 * {@link BrowserInformationControl}.
 *
 */
public class HTMLAngularCompletionProposal extends CustomCompletionProposal
		implements ICompletionProposalExtension3, ITernHoverInfoProvider {

	private final IIDETernProject ternProject;
	private IInformationControlCreator ternControlCreator;

	public HTMLAngularCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance,
			IIDETernProject ternProject) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance);
		this.ternProject = ternProject;
	}

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		Shell shell = TernUIPlugin.getActiveWorkbenchShell();
		if (shell == null || !BrowserInformationControl.isAvailable(shell))
			return null;

		if (ternControlCreator == null) {
			IInformationControlCreator presenterControlCreator = new IDEPresenterControlCreator(
					this);
			ternControlCreator = new IDEHoverControlCreator(
					presenterControlCreator, true, this);
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
