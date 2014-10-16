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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tern.eclipse.ide.core.IIDETernProject;
import tern.scriptpath.ITernScriptPath;

public class AngularLinkResource {

	private static final String SEPARATOR = ";";

	private List<AngularLink> elementLinks;

	private AngularLink resourceLink;

	private final IResource resource;

	public AngularLinkResource(IResource resource, String resourceInfo)
			throws CoreException {
		this.resource = resource;
		if (resourceInfo != null) {
			String[] infos = resourceInfo.split(SEPARATOR);
			AngularLink link = null;
			ITernScriptPath scriptPath = null;
			String module = null;
			String controller = null;
			String elementId = null;
			IIDETernProject ternProject = AngularProject
					.getTernProject(resource.getProject());
			for (int i = 0; i < infos.length; i++) {
				switch (i % 4) {
				case 0:
					scriptPath = ternProject.getScriptPath(infos[i]);
					break;
				case 1:
					module = infos[i].trim();
					break;
				case 2:
					controller = infos[i].trim();
					break;
				case 3:
					elementId = infos[i].trim();
					addLink(scriptPath, module, controller, elementId);
					break;
				}
			}
		}
	}

	public void addLink(ITernScriptPath scriptPath, String module,
			String controller, String elementId) {
		if (scriptPath == null) {
			return;
		}
		if (!StringUtils.isEmpty(elementId)) {
			if (elementLinks == null) {
				elementLinks = new ArrayList<AngularLink>();
			}
			elementLinks.add(new AngularLink(elementId, scriptPath, module,
					controller));
		} else {
			this.resourceLink = new AngularLink(elementId, scriptPath, module,
					controller);
		}
	}

	public IResource getResource() {
		return resource;
	}

	public AngularLink getResourceLink() {
		return resourceLink;
	}

	public List<AngularLink> getElementLinks() {
		return elementLinks;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (resourceLink != null) {
			write(resourceLink, s);
		}
		if (elementLinks != null) {
			for (AngularLink link : elementLinks) {
				write(link, s);
			}
		}
		return s.toString();
	}

	private void write(AngularLink link, StringBuilder s) {
		s.append(link.getScriptPath().getPath());
		s.append(SEPARATOR);
		write(link.getModule(), s);
		s.append(SEPARATOR);
		write(link.getController(), s);
		s.append(SEPARATOR);
		write(link.getElementId(), s);
	}

	private void write(String value, StringBuilder s) {
		if (StringUtils.isEmpty(value)) {
			s.append(" ");
		} else {
			s.append(value);
		}
	}

	public void save() throws CoreException {
		resource.setPersistentProperty(AngularLinkHelper.CONTROLLER_INFO,
				this.toString());
		resource.setSessionProperty(AngularLinkHelper.CONTROLLER_INFO, this);
	}
}
