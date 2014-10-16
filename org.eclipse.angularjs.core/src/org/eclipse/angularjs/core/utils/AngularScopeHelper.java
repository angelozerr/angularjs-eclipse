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
package org.eclipse.angularjs.core.utils;

import org.eclipse.angularjs.core.link.AngularLink;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.core.link.AngularLinkResource;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.protocol.Controller;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.scriptpath.ITernScriptPath;

/**
 * Angular scope helper.
 *
 */
public class AngularScopeHelper {

	public static String getAngularValue(IDOMAttr attr, AngularType angularType) {
		if (angularType == AngularType.controller) {
			return Controller.getName(attr.getValue());
		}
		return attr.getValue();
	}

	public static ITernScriptPath populateScope(Node element, IFile file,
			AngularType angularType, TernAngularQuery query) throws Exception {
		ITernScriptPath scriptPath = null;
		HTMLTernAngularHelper.populateScope(element,
				DOMDirectiveProvider.getInstance(), file.getProject(), query);
		AngularLinkResource info = null;
		if (angularType != AngularType.module) {
			// Check if query has module defined.
			if (!query.hasModule()) {
				info = AngularLinkHelper.getControllerInfo(file);
				if (info != null) {
					AngularLink resourceLink = info.getResourceLink();
					if (resourceLink != null) {
						// Load needed files
						scriptPath = resourceLink.getScriptPath();
						query.getScope().setModule(resourceLink.getModule());
						if (angularType != AngularType.controller) {
							if (!query.hasControllers()
									&& !StringUtils.isEmpty(resourceLink
											.getController())) {
								query.getScope().getControllers()
										.add(resourceLink.getController());
							}
						}
					}
				}
			}
		}
		return scriptPath;
	}
}
