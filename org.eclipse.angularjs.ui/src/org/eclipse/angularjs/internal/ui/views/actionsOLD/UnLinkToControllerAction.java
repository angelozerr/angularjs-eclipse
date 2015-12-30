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
package org.eclipse.angularjs.internal.ui.views.actionsOLD;

import org.eclipse.angularjs.core.AngularElement;
import org.eclipse.angularjs.core.Module;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerViewOLD;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * This action unlink the selected controller to the current HTML/JSP file
 * opened in a editor.
 * 
 */
public class UnLinkToControllerAction extends Action {

	private final AngularExplorerViewOLD explorer;

	public UnLinkToControllerAction(AngularExplorerViewOLD explorer) {
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

				Module module = null;
				AngularElement controller = null;
				String elementId = null;
				if (firstSelection instanceof Module) {
					module = (Module) firstSelection;
				} else if (firstSelection instanceof AngularElement) {
					controller = (AngularElement) firstSelection;
					module = controller.getModule();
				}
				if (module != null) {
					try {
						AngularLinkHelper.removeController(resource, module
								.getScriptPath(), module.getName(),
								controller != null ? controller.getName()
										: null, elementId);
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
