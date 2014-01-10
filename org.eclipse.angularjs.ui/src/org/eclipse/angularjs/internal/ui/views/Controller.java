package org.eclipse.angularjs.internal.ui.views;

import org.eclipse.angularjs.internal.ui.hyperlink.EditorUtils;
import org.eclipse.core.resources.IFile;

import tern.angular.AngularType;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class Controller extends BaseModel implements ITernDefinitionCollector,
		IOpenableInEditor {

	private final Module module;

	public Controller(String name, Module module) {
		super(name, Type.Controller, module.getScriptPath());
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	public void openInEditor() {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.controller);
		query.getScope().setModule(getModule().getName());
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
