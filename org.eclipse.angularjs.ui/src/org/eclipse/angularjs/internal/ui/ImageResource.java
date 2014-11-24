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
package org.eclipse.angularjs.internal.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Utility class to handle image resources.
 */
public class ImageResource {
	// the image registry
	private static ImageRegistry imageRegistry;

	// map of image descriptors since these
	// will be lost by the image registry
	private static Map<String, ImageDescriptor> imageDescriptors;

	// base urls for images
	private static URL ICON_BASE_URL;

	private static final String URL_OBJ = "full/obj16/";
	private static final String ELCL_OBJ = "full/elcl16/";

	// General Object Images
	public static final String IMG_ANGULARJS = "angularjs";
	public static final String IMG_ANGULARJS_CHECKED = "angularjs_checked";
	public static final String IMG_DIRECTIVE = "directive";
	public static final String IMG_DIRECTIVE_PARAM = "directive_param";
	public static final String IMG_CONTROLLER = "controller";
	public static final String IMG_CONTROLLER_CHECKED = "controller_checked";
	public static final String IMG_FACTORY = "factory";
	public static final String IMG_PROVIDER = "provider";
	public static final String IMG_SERVICE = "service";
	public static final String IMG_FILTER = "filter";
	public static final String IMG_FOLDER = "folder";

	public static final String IMG_ELCL_REFRESH = "refresh";
	public static final String IMG_ELCL_GOTO_DEF = "goto_def";
	public static final String IMG_ELCL_LINK_TO_CTRL = "link_to_ctrl";
	public static final String IMG_ELCL_UNLINK_TO_CTRL = "unlink_to_ctrl";

	static {
		try {
			String pathSuffix = "icons/";
			ICON_BASE_URL = AngularUIPlugin.getDefault().getBundle()
					.getEntry(pathSuffix);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Images error", e);
		}
	}

	/**
	 * Cannot construct an ImageResource. Use static methods only.
	 */
	private ImageResource() {
		// do nothing
	}

	/**
	 * Dispose of element images that were created.
	 */
	protected static void dispose() {
		// do nothing
	}

	/**
	 * Return the image with the given key.
	 * 
	 * @param key
	 *            java.lang.String
	 * @return org.eclipse.swt.graphics.Image
	 */
	public static Image getImage(String key) {
		return getImage(key, null);
	}

	/**
	 * Return the image with the given key.
	 * 
	 * @param key
	 *            java.lang.String
	 * @return org.eclipse.swt.graphics.Image
	 */
	public static Image getImage(String key, String keyIfImageNull) {
		if (imageRegistry == null)
			initializeImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			if (keyIfImageNull != null) {
				return getImage(keyIfImageNull, null);
			}
			imageRegistry.put(key, ImageDescriptor.getMissingImageDescriptor());
			image = imageRegistry.get(key);
		}
		return image;
	}

	/**
	 * Return the image descriptor with the given key.
	 * 
	 * @param key
	 *            java.lang.String
	 * @return org.eclipse.jface.resource.ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		ImageDescriptor id = imageDescriptors.get(key);
		if (id != null)
			return id;

		return ImageDescriptor.getMissingImageDescriptor();
	}

	/**
	 * Initialize the image resources.
	 */
	protected static void initializeImageRegistry() {
		imageRegistry = AngularUIPlugin.getDefault().getImageRegistry();
		imageDescriptors = new HashMap<String, ImageDescriptor>();

		// load general object images
		registerImage(IMG_ANGULARJS, URL_OBJ + IMG_ANGULARJS + ".png");
		registerImage(IMG_ANGULARJS_CHECKED, URL_OBJ + IMG_ANGULARJS_CHECKED
				+ ".png");
		registerImage(IMG_DIRECTIVE, URL_OBJ + IMG_DIRECTIVE + ".png");
		registerImage(IMG_DIRECTIVE_PARAM, URL_OBJ + IMG_DIRECTIVE_PARAM
				+ ".png");
		registerImage(IMG_CONTROLLER, URL_OBJ + IMG_CONTROLLER + ".png");
		registerImage(IMG_CONTROLLER_CHECKED, URL_OBJ + IMG_CONTROLLER_CHECKED
				+ ".png");
		registerImage(IMG_FACTORY, URL_OBJ + IMG_FACTORY + ".png");
		registerImage(IMG_FILTER, URL_OBJ + IMG_FILTER + ".png");
		registerImage(IMG_PROVIDER, URL_OBJ + IMG_PROVIDER + ".png");
		registerImage(IMG_SERVICE, URL_OBJ + IMG_SERVICE + ".png");
		registerImage(IMG_FOLDER, URL_OBJ + IMG_FOLDER + ".gif");

		registerImage(IMG_ELCL_REFRESH, ELCL_OBJ + IMG_ELCL_REFRESH + ".gif");
		registerImage(IMG_ELCL_GOTO_DEF, ELCL_OBJ + IMG_ELCL_GOTO_DEF + ".gif");
		registerImage(IMG_ELCL_LINK_TO_CTRL, ELCL_OBJ + IMG_ELCL_LINK_TO_CTRL
				+ ".gif");
		registerImage(IMG_ELCL_UNLINK_TO_CTRL, ELCL_OBJ
				+ IMG_ELCL_UNLINK_TO_CTRL + ".gif");
	}

	/**
	 * Register an image with the registry.
	 * 
	 * @param key
	 *            java.lang.String
	 * @param partialURL
	 *            java.lang.String
	 */
	private static void registerImage(String key, String partialURL) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(
					ICON_BASE_URL, partialURL));
			imageRegistry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error registering image " + key
					+ " from " + partialURL, e);
		}
	}

}