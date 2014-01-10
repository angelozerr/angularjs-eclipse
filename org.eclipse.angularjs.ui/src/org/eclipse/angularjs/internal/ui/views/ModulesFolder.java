package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import tern.angular.AngularType;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.protocol.completions.ITernCompletionCollector;

public class ModulesFolder extends BaseModel implements
		ITernCompletionCollector {

	private final List<Module> modules;

	public ModulesFolder(ITernScriptPath scriptPath) {
		super("modules", Type.ModulesFolder, scriptPath);
		modules = new ArrayList<Module>();
	}

	public Object[] getModules() {
		modules.clear();
		// load all modules
		TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
				AngularType.module);
		query.setExpression("");
		super.execute(query, this);
		return modules.toArray();
	}

	@Override
	public void addProposal(String name, String type, String origin,
			Object doc, int pos, Object completion) {
		modules.add(new Module(name, getScriptPath()));
	}
}
