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
		IDefinitionAware {

	private List<AngularElement> elements;

	public Module(String name, ITernScriptPath scriptPath) {
		super(name, Type.Module, scriptPath);
	}

	public Object[] getAngularElements() {
		if (elements == null) {
			this.elements = new ArrayList<AngularElement>();
			// load all controllers of the given module
			TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
					AngularType.controller);
			query.addType(AngularType.directive);
			query.getScope().setModule(super.getName());
			query.setExpression("");
			super.execute(query, this);
		}
		return elements.toArray();
	}

	@Override
	public void addProposal(String name, String type, String origin,
			Object doc, int pos, Object completion, ITernServer ternServer) {
		AngularType angularType = AngularType.get(ternServer.getText(
				completion, "angularType"));
		elements.add(new AngularElement(name, angularType, Module.this));
	}

	@Override
	public void findDefinition(ITernDefinitionCollector collector) {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.module);
		query.setExpression(super.getName());
		super.execute(query, collector);
	}
}
