package org.eclipse.angularjs.internal.ui.views.actions;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.Controller;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class UnLinkToControllerAction extends Action {

	private final TreeViewer viewer;

	public UnLinkToControllerAction(TreeViewer viewer) {
		this.viewer = viewer;
		super.setText(AngularUIMessages.UnLinkToControllerAction_text);
		super.setToolTipText(AngularUIMessages.UnLinkToControllerAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_UNLINK_TO_CTRL));
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (!selection.isEmpty()) {
			Object firstSelection = selection.getFirstElement();
			if (firstSelection instanceof Controller) {

			}
		}
	}
}
