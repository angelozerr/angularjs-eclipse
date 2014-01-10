/*******************************************************************************
 * Copyright (c) 2013 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Angular UI Messages.
 * 
 */
public final class AngularUIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.angularjs.internal.ui.AngularUIMessages"; //$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

	public static String ConvertProjectToAngular_converting_project_job_title;

	public static String AngularGlobalPreferencesPage_desc;
	public static String HTMLAngularGlobalPreferencesPage_desc;
	public static String HTMLAngularEditorPreferencesPage_desc;

	public static String Sample_HTMLAngular_doc;

	public static String AngularTyping_Auto_Complete;
	public static String AngularTyping_Close_EL;

	// Validation
	public static String Validation_AngularElementNotFound;

	// Angular Explorer View
	public static String RefreshExplorerAction_text;
	public static String RefreshExplorerAction_tooltip;
	public static String GoToDefinitionAction_text;
	public static String GoToDefinitionAction_tooltip;
	public static String LinkToControllerAction_text;
	public static String LinkToControllerAction_tooltip;
	public static String UnLinkToControllerAction_text;
	public static String UnLinkToControllerAction_tooltip;

	private AngularUIMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, AngularUIMessages.class);
	}
}
