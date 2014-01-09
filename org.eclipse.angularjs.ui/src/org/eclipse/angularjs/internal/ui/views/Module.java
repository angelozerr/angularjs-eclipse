package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import tern.angular.AngularType;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.protocol.completions.ITernCompletionCollector;

public class Module extends BaseModel {

	public Module(String name, ITernScriptPath scriptPath) {
		super(name, Type.Module, scriptPath);
	}

	public Object[] getControllers() {
		final List<Controller> controllers = new ArrayList<Controller>();
		// load all controllers of the given module
		TernAngularQuery query = new TernAngularCompletionsQuery(
				AngularType.controller);
		query.getScope().setModule(super.getName());
		query.setExpression("");

		ITernCompletionCollector collector = new ITernCompletionCollector() {

			@Override
			public void addProposal(String name, String type, String origin,
					Object doc, int pos, Object completion) {
				controllers.add(new Controller(name, Module.this));
			}
		};
		super.execute(query, collector);
		return controllers.toArray();
	}
}
