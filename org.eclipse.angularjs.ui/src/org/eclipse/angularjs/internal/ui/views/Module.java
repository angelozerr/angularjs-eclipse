package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.angularjs.internal.ui.hyperlink.EditorUtils;
import org.eclipse.core.resources.IFile;

import tern.angular.AngularType;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class Module extends BaseModel implements ITernCompletionCollector,
		ITernDefinitionCollector, IOpenableInEditor {

	private final List<Controller> controllers;

	public Module(String name, ITernScriptPath scriptPath) {
		super(name, Type.Module, scriptPath);
		this.controllers = new ArrayList<Controller>();
	}

	public Object[] getControllers() {
		this.controllers.clear();
		// load all controllers of the given module
		TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
				AngularType.controller);
		query.getScope().setModule(super.getName());
		query.setExpression("");
		super.execute(query, this);
		return controllers.toArray();
	}

	@Override
	public void addProposal(String name, String type, String origin,
			Object doc, int pos, Object completion) {
		controllers.add(new Controller(name, Module.this));
	}

	public void openInEditor() {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.module);
		query.setExpression(super.getName());
		super.execute(query, this);
	}

	@Override
	public void setDefinition(String filename, Long start, Long end) {
		IFile file = super.getProject().getFile(filename);
		if (file.exists()) {
			EditorUtils.openInEditor(file, start.intValue(), end.intValue()
					- start.intValue(), true);
		}
	}
}
