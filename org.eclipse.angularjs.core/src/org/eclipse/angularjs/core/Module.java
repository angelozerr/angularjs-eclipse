package org.eclipse.angularjs.core;

import java.util.ArrayList;
import java.util.List;

import tern.angular.AngularType;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.ITernServer;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class Module extends BaseModel implements ITernCompletionCollector,
		IOpenableInEditor {

	private List<Controller> controllers;

	public Module(String name, ITernScriptPath scriptPath) {
		super(name, Type.Module, scriptPath);
	}

	public Object[] getControllers() {
		if (controllers == null) {
			this.controllers = new ArrayList<Controller>();
			// load all controllers of the given module
			TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
					AngularType.controller);
			query.getScope().setModule(super.getName());
			query.setExpression("");
			super.execute(query, this);
		}
		return controllers.toArray();
	}

	@Override
	public void addProposal(String name, String type, String origin,
			Object doc, int pos, Object completion, ITernServer ternServer) {
		controllers.add(new Controller(name, Module.this));
	}

	@Override
	public void openInEditor(ITernDefinitionCollector collector) {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.module);
		query.setExpression(super.getName());
		super.execute(query, collector);
	}
}
