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
package org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;


/**
 * Utility class for reading fragment validation preferences/properties.
 * JSPFContentProperties does not respect the project override preference.
 * 
 */
class FragmentValidationTools {
	/**
	 * @param resource
	 * @return whether to perform validation on a fragment, returning the
	 *         project-specific preference only of project-specific values are
	 *         enabled
	 */
	static boolean shouldValidateFragment(IResource resource) {
		String qualifier = JSPCorePlugin.getDefault().getBundle().getSymbolicName();

		IProject project = null;
		if (resource.getType() == IResource.PROJECT) {
			project = (IProject) resource;
		}
		else {
			project = resource.getProject();
		}
		if (project != null) {
			IEclipsePreferences node = new ProjectScope(project).getNode(qualifier);
			// first, check whether project specific settings are to be used
			boolean useProjectSettings = node.getBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, false);
			if (useProjectSettings) {
				// only if so, return that value
				return node.getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
			}
			// if not, return the workspace value
		}
		return new InstanceScope().getNode(qualifier).getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
	}
}
