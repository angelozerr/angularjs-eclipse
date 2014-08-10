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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.angularjs.internal.core.preferences.AngularCorePreferencesSupport;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.IDirectiveCollector;
import tern.angular.modules.IDirectiveSyntax;
import tern.angular.modules.Restriction;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

/**
 * Angular project.
 * 
 */
public class AngularProject implements IDirectiveSyntax {

	private static final QualifiedName ANGULAR_PROJECT = new QualifiedName(
			AngularCorePlugin.PLUGIN_ID + ".sessionprops", "AngularProject");

	private static final String EXTENSION_ANGULAR_PROJECT_DESCRIBERS = "angularNatureAdapters";

	public static final String START_ANGULAR_EXPRESSION_TOKEN = "{{";
	public static final String END_ANGULAR_EXPRESSION_TOKEN = "}}";

	private final IProject project;

	private final Map<ITernScriptPath, List<BaseModel>> folders;

	private final CustomAngularModulesRegistry customDirectives;

	private static List<String> angularNatureAdapters;

	AngularProject(IProject project) throws CoreException {
		this.project = project;
		this.folders = new HashMap<ITernScriptPath, List<BaseModel>>();
		this.customDirectives = new CustomAngularModulesRegistry(project);
		AngularModulesManager.getInstance().addRegistry(this, customDirectives);
		project.setSessionProperty(ANGULAR_PROJECT, this);
		ensureNatureIsConfigured();
	}

	public static AngularProject getAngularProject(IProject project)
			throws CoreException {
		if (!hasAngularNature(project)) {
			throw new CoreException(
					new Status(IStatus.ERROR, AngularCorePlugin.PLUGIN_ID,
							"The project " + project.getName()
									+ " is not an angular project."));
		}
		AngularProject angularProject = (AngularProject) project
				.getSessionProperty(ANGULAR_PROJECT);
		if (angularProject == null) {
			angularProject = new AngularProject(project);
		}
		return angularProject;
	}

	public IProject getProject() {
		return project;
	}

	public static IDETernProject getTernProject(IProject project)
			throws CoreException {
		return IDETernProject.getTernProject(project);
	}

	/**
	 * Return true if the given project have angular nature
	 * "org.eclipse.angularjs.core.angularnature" and false otherwise.
	 * 
	 * @param project
	 *            Eclipse project.
	 * @return true if the given project have angular nature
	 *         "org.eclipse.angularjs.core.angularnature" and false otherwise.
	 */
	public static boolean hasAngularNature(IProject project) {
		if (project.isAccessible()) {
			try {
				if (project.hasNature(AngularNature.ID))
					return true;

				List<String> angularNatureAdapters = getAngularNatureAdapters();
				for (String adaptToNature : angularNatureAdapters) {
					if (project.hasNature(adaptToNature)) {
						return true;
					}
				}
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Error angular nature", e);
			}
		}
		return false;
	}

	public Collection<BaseModel> getFolders(ITernScriptPath scriptPath) {
		List<BaseModel> folders = this.folders.get(scriptPath);
		if (folders == null) {
			folders = new ArrayList<BaseModel>();
			this.folders.put(scriptPath, folders);
			folders.add(new ScriptsFolder(scriptPath));
			folders.add(new ModulesFolder(scriptPath));
		}
		return folders;
	}

	public void cleanModel() {
		this.folders.clear();
	}

	public Directive getDirective(String tagName, String name,
			Restriction restriction) {
		return AngularModulesManager.getInstance().getDirective(project,
				tagName, name, restriction);
	}

	public void collectDirectives(String tagName, String directiveName,
			List<Directive> existingDirectives, Restriction restriction,
			IDirectiveCollector collector) {
		AngularModulesManager.getInstance()
				.collectDirectives(project, tagName, directiveName, this,
						existingDirectives, restriction, collector);

	}

	@Override
	public boolean isUseOriginalName() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveUseOriginalName(project);
	}

	@Override
	public boolean isStartsWithNothing() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveStartsWithNothing(project);
	}

	@Override
	public boolean isStartsWithX() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveStartsWithX(project);
	}

	@Override
	public boolean isStartsWithData() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveStartsWithData(project);
	}

	@Override
	public boolean isColonDelimiter() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveColonDelimiter(project);
	}

	@Override
	public boolean isMinusDelimiter() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveMinusDelimiter(project);
	}

	@Override
	public boolean isUnderscoreDelimiter() {
		return AngularCorePreferencesSupport.getInstance()
				.isDirectiveUnderscoreDelimiter(project);
	}

	private synchronized static void loadAngularProjectDescribers() {
		if (angularNatureAdapters != null)
			return;

		Trace.trace(Trace.EXTENSION_POINT,
				"->- Loading .angularProjectDescribers extension point ->-");

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				AngularCorePlugin.PLUGIN_ID,
				EXTENSION_ANGULAR_PROJECT_DESCRIBERS);
		List<String> list = new ArrayList<String>(cf.length);
		addAngularNatureAdapters(cf, list);
		angularNatureAdapters = list;

		Trace.trace(Trace.EXTENSION_POINT,
				"-<- Done loading .angularProjectDescribers extension point -<-");
	}

	/**
	 * Load the angular project describers.
	 */
	private static synchronized void addAngularNatureAdapters(
			IConfigurationElement[] cf, List<String> list) {
		for (IConfigurationElement ce : cf) {
			try {
				list.add(ce.getAttribute("id"));
				Trace.trace(Trace.EXTENSION_POINT,
						"  Loaded project describer: " + ce.getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(
						Trace.SEVERE,
						"  Could not load project describers: "
								+ ce.getAttribute("id"), t);
			}
		}
	}

	private static List<String> getAngularNatureAdapters() {
		if (angularNatureAdapters == null) {
			loadAngularProjectDescribers();
		}
		return angularNatureAdapters;
	}

	private void ensureNatureIsConfigured() throws CoreException {
		// Check if .tern-project is correctly configured for adapted nature
		final AngularNature tempAngularNature = new AngularNature();
		tempAngularNature.setProject(project);
		if (!tempAngularNature.isConfigured()) {
			tempAngularNature.configure();
		}
	}

}
