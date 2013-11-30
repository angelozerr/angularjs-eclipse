package org.eclipse.angularjs.core;

import org.eclipse.angularjs.internal.core.AngularCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class AngularProject {

	private static final QualifiedName ANGULAR_PROJECT = new QualifiedName(
			AngularCorePlugin.PLUGIN_ID + ".sessionprops", "AngularProject");

	AngularProject(IProject project) throws CoreException {
		project.setSessionProperty(ANGULAR_PROJECT, project);
	}

	public static AngularProject getAngularProject(IProject project)
			throws CoreException {
		AngularProject angularProject = (AngularProject) project
				.getSessionProperty(ANGULAR_PROJECT);
		if (angularProject == null) {
			angularProject = new AngularProject(project);
		}
		return angularProject;
	}

}
