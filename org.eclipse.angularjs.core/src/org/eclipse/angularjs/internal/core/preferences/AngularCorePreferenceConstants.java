/*******************************************************************************
 * Copyright (c) 2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.core.preferences;

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class AngularCorePreferenceConstants {

	/**
	 * Initializes the given preference store with the default values.
	 * 
	 * @param store
	 *            the preference store to be initialized
	 */
	public static void initializeDefaultValues() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(AngularCorePlugin.PLUGIN_ID);
		// directive syntax
		node.putBoolean(AngularCoreConstants.DIRECTIVE_STARTS_WITH_NOTHING, true);
		node.putBoolean(AngularCoreConstants.DIRECTIVE_MINUS_DELIMITER, true);
	}

	// Don't instantiate
	private AngularCorePreferenceConstants() {
	}
}
