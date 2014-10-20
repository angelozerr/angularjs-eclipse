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
package org.eclipse.angularjs.internal.ui.views.viewers;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.BaseModel;
import org.eclipse.angularjs.core.Module;
import org.eclipse.angularjs.core.ModulesFolder;
import org.eclipse.angularjs.core.ScriptsFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import tern.eclipse.ide.ui.viewers.TernScriptPathContentProvider;
import tern.scriptpath.ITernScriptPath;

/**
 * Content provider used in the tree of the angular explorer.
 * 
 */
public class AngularExplorerContentProvider extends
		TernScriptPathContentProvider {

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof ITernScriptPath) {
			ITernScriptPath scriptPath = (ITernScriptPath) element;
			try {
				AngularProject angularProject = AngularProject
						.getAngularProject((IProject) scriptPath.getOwnerProject().
								getAdapter(IProject.class));
				return angularProject.getFolders(scriptPath).toArray();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (element instanceof BaseModel) {
			switch (((BaseModel) element).getType()) {
			case ScriptsFolder:
				return ((ScriptsFolder) element).getScriptPath()
						.getScriptResources().toArray();
			case ModulesFolder:
				return ((ModulesFolder) element).getModules();
			case Module:
				return ((Module) element).getAngularElements();
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
					BaseModel.Type.AngularElement);
		}
		return false;
	}

}
