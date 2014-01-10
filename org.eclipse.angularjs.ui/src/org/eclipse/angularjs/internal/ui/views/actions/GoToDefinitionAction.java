package org.eclipse.angularjs.internal.ui.views.actions;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.IOpenableInEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class GoToDefinitionAction extends Action {

	private final TreeViewer viewer;

	public GoToDefinitionAction(TreeViewer viewer) {
		this.viewer = viewer;
		super.setText(AngularUIMessages.GoToDefinitionAction_text);
		super.setToolTipText(AngularUIMessages.GoToDefinitionAction_tooltip);
		super.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ELCL_GOTO_DEF));
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (!selection.isEmpty()) {
			Object firstSelection = selection.getFirstElement();
			if (firstSelection instanceof IOpenableInEditor) {
				((IOpenableInEditor) firstSelection).openInEditor();
			}
		}
	}
}
