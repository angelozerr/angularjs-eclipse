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
package org.eclipse.angularjs.internal.ui.autoedit;

import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.preferences.AngularUIPreferenceNames;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.text.StructuredAutoEditStrategy;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class StructuredAutoEditStrategyAngular extends
		StructuredAutoEditStrategy {

	@Override
	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {
		if (!supportsSmartInsert(document)) {
			return;
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			if (model != null) {
				if (command.text != null) {
					smartInsertCloseEndEL(command, document, model);
				}
			}
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private void smartInsertCloseEndEL(DocumentCommand command,
			IDocument document, IStructuredModel model) {
		try {
			if (isPreferenceEnabled(AngularUIPreferenceNames.TYPING_COMPLETE_END_EL)
					&& command.text.equals("{") && document.getLength() > 0 && document.getChar(command.offset - 1) == '{') { //$NON-NLS-1$
				IDOMNode node = (IDOMNode) model
						.getIndexedRegion(command.offset - 1);

				command.text += "}}";
				command.shiftsCaret = false;
				command.caretOffset = command.offset + 1;
				command.doit = false;
			}
		} catch (BadLocationException e) {

		}

	}

	private boolean isPreferenceEnabled(String key) {
		return (key != null && AngularUIPlugin.getDefault()
				.getPreferenceStore().getBoolean(key));
	}

}
