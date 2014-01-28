package org.eclipse.angularjs.internal.ui;

import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.core.link.AngularLink;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.core.link.AngularLinkResource;
import org.eclipse.core.resources.IFile;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.modules.IDirectiveProvider;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

public class AngularScopeHelper {

	public static ITernScriptPath populateScope(Node element, IFile file,
			AngularType angularType, TernAngularQuery query) throws Exception {
		ITernScriptPath scriptPath = null;
		HTMLTernAngularHelper.populateScope(element,
				DOMSSEDirectiveProvider.getInstance(), query);
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
							if (!query.hasControllers()) {
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
