/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.actions;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.dialogs.OpenAngularElementSelectionDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import tern.ITernProject;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.ui.utils.EditorUtils;
import tern.server.protocol.outline.IJSNode;

/**
 * Open an Angular module, controllers, directives, filters, providers services.
 * 
 *
 */
public class OpenAngularElementAction extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public OpenAngularElementAction() {
		super();
		super.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ANGULARJS));
		setText(AngularUIMessages.OpenAngularElementAction_label);
		setDescription(AngularUIMessages.OpenAngularElementAction_description);
		setToolTipText(AngularUIMessages.OpenAngularElementAction_tooltip);
	}

	public void run() {
		Shell parent = AngularUIPlugin.getActiveWorkbenchShell();
		OpenAngularElementSelectionDialog dialog = new OpenAngularElementSelectionDialog(parent, false);

		dialog.setTitle(AngularUIMessages.OpenAngularElementAction_dialogTitle);
		dialog.setMessage(AngularUIMessages.OpenAngularElementAction_dialogMessage);
		int result = dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] elements = dialog.getResult();
		if (elements != null && elements.length > 0) {
			IJSNode element = (IJSNode) elements[0];
			EditorUtils.openInEditor(element);
		}
	}

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void dispose() {
		window = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}

}
