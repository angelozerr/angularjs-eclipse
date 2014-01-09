package org.eclipse.angularjs.internal.ui.views;

import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

public class ScriptsFolder extends BaseModel {

	public ScriptsFolder(ITernScriptPath scriptPath) {
		super("scripts", Type.ScriptsFolder, scriptPath);
	}

}
