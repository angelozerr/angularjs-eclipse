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

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * This action refresh the explorer tree.
 * 
 */
public class RefreshExplorerAction extends Action {

	private final AngularExplorerView explorer;

	public RefreshExplorerAction(AngularExplorerView explorer) {
		this.explorer = explorer;
		super.setText(AngularUIMessages.RefreshExplorerAction_text);
		super.setToolTipText(AngularUIMessages.RefreshExplorerAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_REFRESH));
	}

	@Override
	public void run() {
		explorer.refreshTree(false);
	}

}
