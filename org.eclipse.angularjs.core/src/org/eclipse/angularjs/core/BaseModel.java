/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import tern.ITernProject;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.scriptpath.ITernScriptPath;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class BaseModel {

	public enum Type {
		ScriptsFolder, ModulesFolder, Module, AngularElement
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

	protected IIDETernProject getTernProject() throws CoreException {
		return (IIDETernProject) getProject();
	}

	public ITernProject getProject() {
		return scriptPath.getOwnerProject();
	}

	protected void execute(TernAngularCompletionsQuery query,
			ITernCompletionCollector collector) {
		try {
			IIDETernProject ternProject = getTernProject();
			ternProject.request(query, query.getFiles(), scriptPath, null, null, collector);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void execute(TernAngularDefinitionQuery query,
			ITernDefinitionCollector collector) {
		try {
			IIDETernProject ternProject = getTernProject();
			ternProject.request(query, query.getFiles(), scriptPath, null, null, collector);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
