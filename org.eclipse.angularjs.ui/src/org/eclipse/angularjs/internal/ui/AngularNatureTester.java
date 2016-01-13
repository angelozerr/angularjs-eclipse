/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;

import tern.TernResourcesManager;

/**
 * Property Tester for a IProject receiver object
 * 
 * Property to be tested: "isAngularProject"
 * 
 * @author Victor Rubezhny
 */
public class AngularNatureTester extends org.eclipse.core.expressions.PropertyTester {
	private static final String IS_ANGULAR_PROJECT_PROPERTY = "isAngularProject";
	private static final String IS_PROTRACTOR_FILE_PROPERTY = "isProtractorFile";

	public AngularNatureTester() {
		// Default constructor is required for property tester
	}

	/**
	 * Tests if the receiver object is a project is a Tern project
	 * 
	 * @return true if the receiver object is a Project that has a nature that
	 *         is treated as Tern nature, otherwise false is returned
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if (IS_ANGULAR_PROJECT_PROPERTY.equals(property))
			return testIsAngularProject(receiver);
		else if (IS_PROTRACTOR_FILE_PROPERTY.equals(property))
			return testIsProtractorFile(receiver);

		return false;
	}

	/**
	 * Returns true if the given receiver belongs to angular project and false
	 * otherwise.
	 * 
	 * @param receiver
	 * @return true if the given receiver belongs to angular project and false
	 *         otherwise.
	 */
	private boolean testIsAngularProject(Object receiver) {
		if (receiver instanceof IAdaptable) {
			IProject project = (IProject) ((IAdaptable) receiver).getAdapter(IProject.class);
			if (project != null) {
				return AngularProject.hasAngularNature(project);
			}
		}
		return false;
	}

	/**
	 * 
	 * Returns true if the given receiver is a JavaScript protractor file and
	 * false otherwise.
	 * 
	 * @param receiver
	 * @return true if the given receiver is a JavaScript protractor file and
	 *         false otherwise.
	 */
	private boolean testIsProtractorFile(Object receiver) {
		if (receiver instanceof IAdaptable) {
			IFile file = (IFile) ((IAdaptable) receiver).getAdapter(IFile.class);
			if (file != null) {
				if (AngularProject.hasAngularNature(file.getProject())) {
					return TernResourcesManager.isJSFile(file);
				}
			}
		}
		return false;
	}

}
