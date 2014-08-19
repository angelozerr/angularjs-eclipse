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
package org.eclipse.angularjs.core.validation;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.w3c.dom.Node;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Restriction;

/**
 * Utilities class used to validate Angular elements/attributes.
 *
 */
public class AngularValidatorUtils {

	/**
	 * Return true if the given error must be ignored and false otherwise.
	 * 
	 * @param info
	 * @param project
	 * @return true if the given error must be ignored and false otherwise.
	 */
	public static boolean isIgnoreError(ErrorInfo info, IProject project) {
		int targetType = info.getTargetType();
		// org.eclipse.wst.html.core.internal.validate.ErrorState.UNDEFINED_NAME_ERROR
		// = 11 is private -(
		if ((targetType == Node.ATTRIBUTE_NODE || targetType == Node.ELEMENT_NODE)
				&& info.getState() == 11) {
			// It's an error about attribute name, check if it's an Angular
			// Attribute (ex : ng-app)
			String name = info.getHint();
			if (isDirective(project, name, targetType)) {
				// it's an angular directive, ignore the error
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given name is a directive for the current node
	 * (attribute, element) and false otherwise.
	 * 
	 * @param project
	 * @param name
	 * @param targetType
	 * @return
	 */
	private static boolean isDirective(IProject project, String name,
			int targetType) {
		try {
			if (AngularProject.hasAngularNature(project)) {
				AngularProject angularProject = AngularProject
						.getAngularProject(project);
				return AngularModulesManager.getInstance().getDirective(
						angularProject, null, name, getRestriction(targetType)) != null;
			}
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return false;
	}

	private static Restriction getRestriction(int targetType) {
		switch (targetType) {
		case Node.ATTRIBUTE_NODE:
			return Restriction.A;
		case Node.ELEMENT_NODE:
			return Restriction.E;
		}
		return null;
	}
}
