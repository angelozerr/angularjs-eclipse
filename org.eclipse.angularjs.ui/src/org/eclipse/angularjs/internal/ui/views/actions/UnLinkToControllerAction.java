/*******************************************************************************
 * Copyright (c) 2013 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.views.actions;

import org.eclipse.angularjs.core.Controller;
import org.eclipse.angularjs.core.utils.PersistentUtils;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerView;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * This action unlink the selected controller to the current HTML/JSP file
 * opened in a editor.
 * 
 */
public class UnLinkToControllerAction extends Action {

	private final AngularExplorerView explorer;

	public UnLinkToControllerAction(AngularExplorerView explorer) {
		this.explorer = explorer;
		super.setText(AngularUIMessages.UnLinkToControllerAction_text);
		super.setToolTipText(AngularUIMessages.UnLinkToControllerAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_UNLINK_TO_CTRL));
	}

	@Override
	public void run() {
		IResource resource = explorer.getCurrentResource();
		if (resource != null) {
			IStructuredSelection selection = (IStructuredSelection) explorer
					.getViewer().getSelection();
			if (!selection.isEmpty()) {
				Object firstSelection = selection.getFirstElement();
				if (firstSelection instanceof Controller) {
					try {
						PersistentUtils.removeController(resource);
						explorer.updateEnabledLinkActions(false);
						explorer.refreshTree(true);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
