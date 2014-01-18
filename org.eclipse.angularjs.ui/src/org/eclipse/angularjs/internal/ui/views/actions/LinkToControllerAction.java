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
import org.eclipse.angularjs.core.Module;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerView;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * This action link the selected controller to the current HTML/JSP file opened
 * in a editor.
 * 
 */
public class LinkToControllerAction extends Action {

	private final AngularExplorerView explorer;

	public LinkToControllerAction(AngularExplorerView explorer) {
		this.explorer = explorer;
		super.setText(AngularUIMessages.LinkToControllerAction_text);
		super.setToolTipText(AngularUIMessages.LinkToControllerAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_LINK_TO_CTRL));
	}

	@Override
	public void run() {
		IResource resource = explorer.getCurrentResource();
		if (resource != null) {
			IStructuredSelection selection = (IStructuredSelection) explorer
					.getViewer().getSelection();
			if (!selection.isEmpty()) {
				Module module = null;
				Controller controller = null;
				String elementId = null;
				Object firstSelection = selection.getFirstElement();
				if (firstSelection instanceof Module) {
					module = (Module) firstSelection;
				} else if (firstSelection instanceof Controller) {
					controller = (Controller) firstSelection;
					module = controller.getModule();
				}
				if (module != null) {
					try {
						AngularLinkHelper.setController(module.getScriptPath(),
								module.getName(),
								controller != null ? controller.getName()
										: null, resource, elementId);
						explorer.updateEnabledLinkActions(true);
						explorer.refreshTree(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
