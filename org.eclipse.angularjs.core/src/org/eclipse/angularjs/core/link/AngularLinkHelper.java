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
package org.eclipse.angularjs.core.link;

import org.eclipse.angularjs.core.utils.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import tern.eclipse.ide.core.TernCorePlugin;
import tern.scriptpath.ITernScriptPath;

public class AngularLinkHelper {

	static final QualifiedName CONTROLLER_INFO = new QualifiedName(
			TernCorePlugin.PLUGIN_ID + ".resourceprops", "ControllerInfo");

	public static void setController(ITernScriptPath scriptPath, String module,
			String controller, IResource resource, String elementId)
			throws Exception {
		AngularLinkResource linkResource = getControllerInfo(resource);
		if (linkResource == null) {
			linkResource = new AngularLinkResource(resource, null);
		}
		linkResource.addLink(scriptPath, module, controller, elementId);
		linkResource.save();
	}

	public static AngularLinkResource getControllerInfo(IResource resource)
			throws Exception {
		AngularLinkResource info = (AngularLinkResource) resource
				.getSessionProperty(CONTROLLER_INFO);
		if (info == null) {
			String resourceInfo = resource
					.getPersistentProperty(CONTROLLER_INFO);
			if (!StringUtils.isEmpty(resourceInfo)) {
				info = new AngularLinkResource(resource, resourceInfo);
				info.save();
			}
		}
		return info;
	}

	public static void removeController(IResource resource,
			ITernScriptPath scriptPath, String module, String controller,
			String elementId) throws CoreException {
		resource.setSessionProperty(CONTROLLER_INFO, null);
		resource.setPersistentProperty(CONTROLLER_INFO, null);
	}

	public static boolean hasController(IResource resource) {
		try {
			return getControllerInfo(resource) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isSameController(IResource resource,
			ITernScriptPath scriptPath, String module, String controller,
			String elementId) {
		try {
			AngularLinkResource info = getControllerInfo(resource);
			if (info != null) {
				if (elementId != null) {
					if (info.getElementLinks() != null) {
						for (AngularLink elementLink : info.getElementLinks()) {
							if (isSameController(elementLink, scriptPath,
									module, controller, elementId)) {
								return true;
							}
						}
					}
				}
				if (info.getResourceLink() != null) {
					return isSameController(info.getResourceLink(), scriptPath,
							module, controller, elementId);
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean isSameController(AngularLink link,
			ITernScriptPath scriptPath, String module, String controller,
			String elementId) {
		IResource r1 = (IResource) link.getScriptPath().getAdapter(IResource.class);
		IResource r2 = (IResource) scriptPath.getAdapter(IResource.class);
		if (r1 == null || r2 == null ||
				!r1.getProject().equals(r2.getProject())) {
			// not the same project
			return false;
		}
		if (!link.getModule().equals(module)) {
			// not the same module
			return false;
		}
		if (!StringUtils.isEmpty(link.getController())) {
			if (!link.getController().equals(controller)) {
				// not the same controller
				return false;
			}
		} else {
			if (controller != null) {
				return false;
			}
		}
		if (!StringUtils.isEmpty(link.getElementId())) {
			if (!link.getElementId().equals(elementId)) {
				// not the same elementId
				return false;
			}
		} else {
			if (elementId != null) {
				return false;
			}
		}
		return true;

	}

}
