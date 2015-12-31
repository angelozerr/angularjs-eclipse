package org.eclipse.angularjs.internal.ui.views.actions;

import org.eclipse.angularjs.core.BaseModel;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.views.AngularContentOutlinePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ViewerSorter;

import tern.angular.modules.IAngularElement;
import tern.eclipse.ide.ui.views.actions.AbstractLexicalSortingAction;

public class LexicalSortingAction extends AbstractLexicalSortingAction {

	public LexicalSortingAction(AngularContentOutlinePage page) {
		super(page, AngularUIMessages.LexicalSortingAction_text, AngularUIMessages.LexicalSortingAction_tooltip,
				AngularUIMessages.LexicalSortingAction_description, new LexicalSorter());
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return AngularUIPlugin.getDefault().getPreferenceStore();
	}

	static class LexicalSorter extends ViewerSorter {
		@Override
		public int category(Object element) {
			if (element instanceof IAngularElement) {
				IAngularElement angularElement = (IAngularElement) element;
				switch (angularElement.getAngularType()) {
				case controller:
					return 5;
				case directive:
					return 6;
				case filter:
					return 7;
				case factory:
					return 8;
				case provider:
					return 9;
				case service:
					return 10;
				default:
					return 11;
				}
			} else if (element instanceof BaseModel) {
				switch (((BaseModel) element).getType()) {
				case Module:
				case ScriptsFolder:
					return 0;
				default:
					return 1;
				}
			}
			return 12;
		}
	}
}
