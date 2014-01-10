package org.eclipse.angularjs.core;

import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

public class ScriptsFolder extends BaseModel {

	public ScriptsFolder(ITernScriptPath scriptPath) {
		super("scripts", Type.ScriptsFolder, scriptPath);
	}

}
