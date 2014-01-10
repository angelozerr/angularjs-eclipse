package org.eclipse.angularjs.internal.ui.views;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class RefreshExplorerAction extends Action {

	private final TreeViewer viewer;

	public RefreshExplorerAction(TreeViewer viewer) {
		this.viewer = viewer;
		super.setText(AngularUIMessages.RefreshExplorerAction_text);
		super.setToolTipText(AngularUIMessages.RefreshExplorerAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_REFRESH));
	}

	@Override
	public void run() {
		viewer.refresh();
	}

}
