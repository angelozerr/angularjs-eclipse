package org.eclipse.angularjs.core;

import tern.angular.AngularType;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class Controller extends BaseModel implements IOpenableInEditor {

	private final Module module;

	public Controller(String name, Module module) {
		super(name, Type.Controller, module.getScriptPath());
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	@Override
	public void openInEditor(ITernDefinitionCollector collector) {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.controller);
		query.getScope().setModule(getModule().getName());
		query.setExpression(super.getName());
		super.execute(query, collector);
	}

}
