package org.eclipse.angularjs.internal.ui.views;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.json.simple.JSONArray;
import org.w3c.dom.Node;

import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.IPageScriptPath;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath.ScriptPathsType;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class BaseModel {

	public enum Type {
		ScriptsFolder, ModulesFolder, Module, Controller
	}

	private final String name;
	private final Type type;
	private final ITernScriptPath scriptPath;

	public BaseModel(String name, Type type, ITernScriptPath scriptPath) {
		this.type = type;
		this.name = name;
		this.scriptPath = scriptPath;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public ITernScriptPath getScriptPath() {
		return scriptPath;
	}

	protected IDETernProject getTernProject() throws CoreException {
		IProject project = getProject();
		return IDETernProject.getTernProject(project);
	}

	public IProject getProject() {
		return scriptPath.getResource().getProject();
	}

	protected void execute(TernAngularCompletionsQuery query,
			ITernCompletionCollector collector) {
		try {
			IDETernProject ternProject = getTernProject();
			TernDoc doc = createDoc(query, ternProject);
			if (doc != null) {
				// Execute Tern completion
				final ITernServer ternServer = ternProject.getTernServer();
				ternServer.request(doc, collector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void execute(TernAngularDefinitionQuery query,
			ITernDefinitionCollector collector) {
		try {
			IDETernProject ternProject = getTernProject();
			TernDoc doc = createDoc(query, ternProject);
			if (doc != null) {
				// Execute Tern completion
				final ITernServer ternServer = ternProject.getTernServer();
				ternServer.request(doc, collector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TernDoc createDoc(TernAngularQuery query, IDETernProject ternProject)
			throws IOException {
		TernDoc doc = null;
		if (getScriptPath().getType().equals(ScriptPathsType.PAGE)) {
			IPageScriptPath scriptPath = (IPageScriptPath) getScriptPath();
			Node element = scriptPath.getDocument().getDocumentElement();
			doc = new TernDoc(query);
			// Update TernDoc#addFile
			JSONArray files = query.getFiles();
			ternProject.getFileManager().updateFiles(element,
					(IFile) scriptPath.getResource(), doc, files);

		}
		return doc;
	}
}
