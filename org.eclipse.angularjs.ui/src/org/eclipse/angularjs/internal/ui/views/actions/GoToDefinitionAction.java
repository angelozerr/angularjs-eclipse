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
package org.eclipse.angularjs.internal.ui.views.actions;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * This action opens in an editor the selected element of the tree of the
 * angular explorer if the element can be opened.
 * 
 */
public class GoToDefinitionAction extends Action {

	private final AngularExplorerView explorer;

	public GoToDefinitionAction(AngularExplorerView explorer) {
		this.explorer = explorer;
		super.setText(AngularUIMessages.GoToDefinitionAction_text);
		super.setToolTipText(AngularUIMessages.GoToDefinitionAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_GOTO_DEF));
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) explorer
				.getViewer().getSelection();
		if (!selection.isEmpty()) {
			Object firstSelection = selection.getFirstElement();
			explorer.tryOpenInEditor(firstSelection);
		}
	}
}
