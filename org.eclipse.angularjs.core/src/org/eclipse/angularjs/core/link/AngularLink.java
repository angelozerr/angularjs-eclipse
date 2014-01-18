package org.eclipse.angularjs.core.link;

import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

public class AngularLink {

	private final String elementId;
	private final ITernScriptPath scriptPath;
	private final String module;
	private final String controller;

	public AngularLink(String elementId, ITernScriptPath scriptPath,
			String module, String controller) {
		this.elementId = elementId;
		this.scriptPath = scriptPath;
		this.module = module;
		this.controller = controller;
	}

	public String getElementId() {
		return elementId;
	}

	public ITernScriptPath getScriptPath() {
		return scriptPath;
	}

	public String getModule() {
		return module;
	}

	public String getController() {
		return controller;
	}
}
