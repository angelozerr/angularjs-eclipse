package org.eclipse.angularjs.internal.ui.views;

import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.swt.graphics.Image;

import tern.eclipse.ide.ui.viewers.TernScriptPathLabelProvider;

public class AngularControllerLabelProvider extends TernScriptPathLabelProvider {

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
			switch (((BaseModel) element).getType()) {
			case Module:
				return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
			case Controller:
				return ImageResource.getImage(ImageResource.IMG_CONTROLLER);
				default:
					return ImageResource.getImage(ImageResource.IMG_FOLDER);
			}
		}
		return super.getImage(element);
	}
}
