/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public final class AngularUIMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.angularjs.internal.ui.AngularUIMessages"; //$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

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

	public static String ConvertProjectToAngular_converting_project_job_title;

	static {
		NLS.initializeMessages(BUNDLE_NAME, AngularUIMessages.class);
	}
}
