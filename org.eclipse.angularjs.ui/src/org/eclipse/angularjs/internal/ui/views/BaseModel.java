package org.eclipse.angularjs.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
		IProject project = scriptPath.getResource().getProject();
		return IDETernProject.getTernProject(project);
	}

	protected void execute(TernAngularQuery query,
			ITernCompletionCollector collector) {
		try {
			IDETernProject ternProject = getTernProject();
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
			if (doc != null) {
				// Execute Tern completion
				final ITernServer ternServer = ternProject.getTernServer();
				ternServer.request(doc, collector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
