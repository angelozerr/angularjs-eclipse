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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import tern.utils.StringUtils;

public class AngularLinkResource {

	private static final String SEPARATOR = ";";

	private List<AngularLink> elementLinks;

	private AngularLink resourceLink;

	private final IResource resource;

	public AngularLinkResource(IResource resource, String resourceInfo) throws CoreException {
		this.resource = resource;
		if (resourceInfo != null) {
			String[] infos = resourceInfo.split(SEPARATOR);
			AngularLink link = null;
			String module = null;
			String controller = null;
			String elementId = null;
			for (int i = 0; i < infos.length; i++) {
				switch (i % 3) {
				case 0:
					module = infos[i].trim();
					break;
				case 1:
					controller = infos[i].trim();
					break;
				case 2:
					elementId = infos[i].trim();
					addLink(module, controller, elementId);
					break;
				}
			}
		}
	}

	public void addLink(String module, String controller, String elementId) {
		if (!StringUtils.isEmpty(elementId)) {
			if (elementLinks == null) {
				elementLinks = new ArrayList<AngularLink>();
			}
			elementLinks.add(new AngularLink(elementId, module, controller));
		} else {
			this.resourceLink = new AngularLink(elementId, module, controller);
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
		resource.setPersistentProperty(AngularLinkHelper.CONTROLLER_INFO, this.toString());
		resource.setSessionProperty(AngularLinkHelper.CONTROLLER_INFO, this);
	}
}
