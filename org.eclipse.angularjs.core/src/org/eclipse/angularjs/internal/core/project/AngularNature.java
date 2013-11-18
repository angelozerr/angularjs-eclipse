package org.eclipse.angularjs.internal.core.project;

import org.eclipse.angularjs.internal.core.AngularCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class AngularNature implements IProjectNature {

	public static final String ID = AngularCorePlugin.ID + ".angularnature"; //$NON-NLS-1$

	private IProject project;

	public void configure() throws CoreException {
		// Add nature-specific information
		// for the project, such as adding a builder
		// to a project's build spec.
	}

	public void deconfigure() throws CoreException {
		// Remove the nature-specific information here.
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject value) {
		project = value;
	}

}
