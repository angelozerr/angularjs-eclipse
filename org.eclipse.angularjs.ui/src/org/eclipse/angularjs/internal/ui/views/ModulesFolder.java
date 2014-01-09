package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.json.simple.JSONArray;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.IPageScriptPath;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath.ScriptPathsType;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.completions.ITernCompletionCollector;

public class ModulesFolder extends BaseModel {

	public ModulesFolder(ITernScriptPath scriptPath) {
		super("modules", Type.ModulesFolder, scriptPath);
	}

	public Object[] getModules() {
		final List<Module> modules = new ArrayList<Module>();
		// load all modules
		TernAngularQuery query = new TernAngularCompletionsQuery(
				AngularType.module);
		query.setExpression("");
		ITernCompletionCollector collector = new ITernCompletionCollector() {

			@Override
			public void addProposal(String name, String type, String origin,
					Object doc, int pos, Object completion) {
				modules.add(new Module(name, getScriptPath()));
			}
		};
		super.execute(query, collector);
		return modules.toArray();
	}
}
