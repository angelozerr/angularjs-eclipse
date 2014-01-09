package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.eclipse.ide.ui.viewers.TernScriptPathContentProvider;

public class AngularControllerContentProvider extends
		TernScriptPathContentProvider {

	private final AngularControllerView view;

	public AngularControllerContentProvider(AngularControllerView view) {
		this.view = view;
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof ITernScriptPath) {
			List<BaseModel> folders = new ArrayList<BaseModel>();
			folders.add(new ScriptsFolder((ITernScriptPath) element));
			folders.add(new ModulesFolder((ITernScriptPath) element));
			return folders.toArray();
		}
		if (element instanceof BaseModel) {
			switch (((BaseModel) element).getType()) {
			case ScriptsFolder:
				return ((ScriptsFolder) element).getScriptPath()
						.getScriptResources().toArray();
			case ModulesFolder:
				return ((ModulesFolder) element).getModules();
			case Module:
				return ((Module) element).getControllers();

			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (super.hasChildren(element)) {
			return true;
		}
		if (element instanceof BaseModel) {
			return !((BaseModel) element).getType().equals(
					BaseModel.Type.Controller);
		}
		return false;
	}

	private Object[] getModules(ITernScriptPath scriptPath) {
		// TODO Auto-generated method stub
		return null;
	}
}
