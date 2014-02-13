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
package org.eclipse.angularjs.internal.ui.views.viewers;

import org.eclipse.angularjs.core.AngularElement;
import org.eclipse.angularjs.core.BaseModel;
import org.eclipse.angularjs.core.Module;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.AngularExplorerView;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;

import tern.eclipse.ide.ui.viewers.TernScriptPathLabelProvider;

/**
 * Label provider used in the tree of the angular explorer.
 * 
 */
public class AngularExplorerLabelProvider extends TernScriptPathLabelProvider {

	private final AngularExplorerView view;

	public AngularExplorerLabelProvider(AngularExplorerView view) {
		this.view = view;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof BaseModel) {
			return ((BaseModel) element).getName();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof BaseModel) {
			IResource resource = view.getCurrentResource();
			switch (((BaseModel) element).getType()) {
			case Module:
				Module module = (Module) element;
				if (resource != null
						&& AngularLinkHelper.isSameController(resource,
								module.getScriptPath(), module.getName(), null,
								null)) {
					return ImageResource
							.getImage(ImageResource.IMG_ANGULARJS_CHECKED);
				}
				return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
			case AngularElement:
				AngularElement angularElement = (AngularElement) element;
				switch (angularElement.getAngularType()) {
				case controller:
					if (resource != null
							&& AngularLinkHelper.isSameController(resource,
									angularElement.getScriptPath(),
									angularElement.getModule().getName(),
									angularElement.getName(), null)) {
						return ImageResource
								.getImage(ImageResource.IMG_CONTROLLER_CHECKED);
					}
					return ImageResource.getImage(ImageResource.IMG_CONTROLLER);
				case directive:
					return ImageResource.getImage(ImageResource.IMG_DIRECTIVE);
				case filter:
					return ImageResource.getImage(ImageResource.IMG_FILTER);
				case factory:
					return ImageResource.getImage(ImageResource.IMG_FACTORY);
				case provider:
					return ImageResource.getImage(ImageResource.IMG_PROVIDER);
				case service:
					return ImageResource.getImage(ImageResource.IMG_SERVICE);
				}

			default:
				return ImageResource.getImage(ImageResource.IMG_FOLDER);
			}
		}
		return super.getImage(element);
	}
}
