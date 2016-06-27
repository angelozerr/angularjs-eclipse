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
package org.eclipse.angularjs.internal.ui.taginfo;

import org.eclipse.angularjs.internal.ui.utils.HTMLAngularPrinter;

import tern.angular.AngularType;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.ui.hover.HTMLTernTypeCollector;
import tern.server.ITernServer;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.type.ITernTypeCollector;

/**
 * {@link ITernTypeCollector} implementation for HTML Angular type collector.
 * 
 */
public class HTMLAngularTernTypeCollector extends HTMLTernTypeCollector {

	public HTMLAngularTernTypeCollector(IIDETernProject ternProject) {
		super(ternProject);
	}

	private String info;

	@Override
	public void setType(String type, boolean guess, String name,
			String exprName, String doc, String url, String origin,
			Object object, IJSONObjectHelper objectHelper) {
		if (name != null) {
			String module = objectHelper.getText(object, "module");
			String controller = objectHelper.getText(object, "controller");
			AngularType angularType = AngularType.get(objectHelper.getText(
					object, "angularType"));
			this.info = HTMLAngularPrinter.getAngularInfo(type, name, module,
					controller, angularType, doc, origin);
		}
	}

	/**
	 * Returns the HTML of the Angular tern type.
	 * 
	 * @return the HTML of the Angular tern type.
	 */
	public String getInfo() {
		return info;
	}
}
