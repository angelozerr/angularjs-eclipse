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

import java.io.IOException;

import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import tern.TernProject;
import tern.server.TernDef;
import tern.server.TernPlugin;

/**
 * AngularJS Nature.
 * 
 */
public class AngularNature implements IProjectNature {

	public static final String ID = AngularCorePlugin.PLUGIN_ID
			+ ".angularnature"; //$NON-NLS-1$

	private IProject project;

	public void configure() throws CoreException {
		// Add "angular" plugin
		TernProject ternProject = AngularProject.getTernProject(project);
		ternProject.addPlugin(TernPlugin.angular);
		// Add "browser" + "ecma5" JSON Type Def
		ternProject.addLib(TernDef.browser.name());
		ternProject.addLib(TernDef.ecma5.name());
		try {
			ternProject.save();
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE,
					"Error while configuring angular nature.", e);
		}
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
