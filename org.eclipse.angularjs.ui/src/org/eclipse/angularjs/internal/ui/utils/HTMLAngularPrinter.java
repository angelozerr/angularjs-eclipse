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
package org.eclipse.angularjs.internal.ui.utils;

import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.eclipse.ide.ui.TernUIPlugin;
import tern.eclipse.ide.ui.utils.HTMLTernPrinter;
import tern.server.protocol.completions.TernCompletionItem;
import tern.utils.StringUtils;

/**
 * Provides a set of convenience methods for creating Angular HTML info for
 * content assist and hover.
 *
 */
public class HTMLAngularPrinter {

	/**
	 * Returns the HTML information of the given module/controller.
	 * 
	 * @param type
	 * @param name
	 * @param module
	 * @param controller
	 * @param angularType
	 * @param doc
	 * @param origin
	 * @return
	 */
	public static String getAngularInfo(String type, String name,
			String module, String controller, AngularType angularType,
			String doc, String origin) {
		StringBuffer buffer = new StringBuffer();
		// title
		String title = getTitle(name, module, controller, angularType);
		ImageDescriptor descriptor = getImageDescriptor(angularType);
		HTMLTernPrinter.startPage(buffer, title, descriptor);
		// doc
		HTMLTernPrinter.addDocContent(buffer, doc);
		HTMLTernPrinter.startDefinitionList(buffer);
		// signature
		HTMLTernPrinter.addDefinitionListItem(buffer, "Signature", type);
		// origin
		HTMLTernPrinter.addOriginContent(buffer, origin);
		HTMLTernPrinter.endDefinitionList(buffer);
		HTMLTernPrinter.endPage(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the title of the module/controller.
	 * 
	 * @param name
	 * @param module
	 * @param controller
	 * @param angularType
	 * @return
	 */
	private static String getTitle(String name, String module,
			String controller, AngularType angularType) {
		switch (angularType) {
		case module:
			return new StringBuilder("<b>").append(name).append("</b>")
					.append(" module").toString();
		case controller:
			StringBuilder title = new StringBuilder("<b>").append(name)
					.append("</b>").append(" controller");
			if (!StringUtils.isEmpty(module)) {
				title.append(" in module <b>").append(module).append("</b>")
						.toString();
			}
			return title.toString();
		}
		return null;
	}

	/**
	 * Returns the HTML information of the given directive.
	 * 
	 * @param directive
	 * @return
	 */
	public static String getDirectiveInfo(Directive directive) {
		StringBuffer buffer = new StringBuffer();
		String title = getTitle(directive);
		ImageDescriptor descriptor = getImageDescriptor(AngularType.directive);
		HTMLTernPrinter.startPage(buffer, title, descriptor);
		// directive description
		HTMLTernPrinter.addDocContent(buffer, directive.getDescription());
		HTMLTernPrinter.startDefinitionList(buffer);
		// restrict
		HTMLTernPrinter.addDefinitionListItem(buffer, "restrict",
				directive.getRestrict());
		// tags
		HTMLTernPrinter.addDefinitionListItem(buffer, "tags",
				directive.getTagNames());
		// parameters
		HTMLTernPrinter.addDefinitionListItem(buffer, "parameters",
				directive.getParameterNames());
		HTMLTernPrinter.endDefinitionList(buffer);
		// url
		HTMLTernPrinter.addURLContent(buffer, directive.getURL());
		HTMLTernPrinter.endPage(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the title of the directive.
	 * 
	 * @param directive
	 * @return
	 */
	private static String getTitle(Directive directive) {
		StringBuilder title = new StringBuilder("");
		title.append("<b>");
		title.append(directive.getName());
		title.append("</b>");
		title.append(" directive in module ");
		title.append("<b>");
		title.append(directive.getModule().getName());
		title.append("</b>");
		return title.toString();
	}

	public static String getAngularInfo(TernCompletionItem item, Boolean guess,
			String module, String controller) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = TernUIPlugin.getTernDescriptorManager()
				.getImageDescriptor(item);
		HTMLTernPrinter.startPage(buffer, HTMLTernPrinter.getTitle(item),
				descriptor);
		// doc
		HTMLTernPrinter.addDocContent(buffer, item.getDoc());
		HTMLTernPrinter.startDefinitionList(buffer);
		// module
		HTMLTernPrinter.addDefinitionListItem(buffer, "Module", module);
		// controller
		HTMLTernPrinter.addDefinitionListItem(buffer, "Controller", controller);
		// parameters
		HTMLTernPrinter.addParametersContent(buffer, item.getParameters());
		// return type
		HTMLTernPrinter.addReturnTypeContent(buffer, item.getJsType());
		// origin
		HTMLTernPrinter.addOriginContent(buffer, item.getOrigin());
		// guess?
		HTMLTernPrinter.addGuessContent(buffer, guess);
		// url
		HTMLTernPrinter.addURLContent(buffer, item.getURL());
		HTMLTernPrinter.endDefinitionList(buffer);
		HTMLTernPrinter.endPage(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the image key from the given angular type.
	 * 
	 * @param angularType
	 * @return
	 */
	public static Image getImage(AngularType angularType) {
		String imageKey = getImageKey(angularType);
		if (imageKey != null) {
			return ImageResource.getImage(imageKey);
		}
		return null;
	}

	/**
	 * Returns the image descriptor from the given angular type.
	 * 
	 * @param angularType
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(AngularType angularType) {
		String imageKey = getImageKey(angularType);
		if (imageKey != null) {
			return ImageResource.getImageDescriptor(imageKey);
		}
		return null;
	}

	/**
	 * Returns the image key from the given angular type.
	 * 
	 * @param angularType
	 * @return
	 */
	private static String getImageKey(AngularType angularType) {
		switch (angularType) {
		case module:
			return ImageResource.IMG_ANGULARJS;
		case controller:
			return ImageResource.IMG_CONTROLLER;
		case directive:
			return ImageResource.IMG_DIRECTIVE;
		default:
			return null;
		}
	}
}
