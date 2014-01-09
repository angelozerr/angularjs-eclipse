package org.eclipse.angularjs.internal.ui.views;

public class Controller extends BaseModel {

	private final Module module;

	public Controller(String name, Module module) {
		super(name, Type.Controller, module.getScriptPath());
		this.module = module;
	}

	public Module getModule() {
		return module;
	}
}
