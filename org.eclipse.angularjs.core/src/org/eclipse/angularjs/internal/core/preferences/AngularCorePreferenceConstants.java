/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.preferences;

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import tern.eclipse.ide.server.nodejs.core.IDENodejsProcessHelper;
import tern.eclipse.ide.server.nodejs.core.INodejsInstall;
import tern.eclipse.ide.server.nodejs.core.INodejsInstallManager;
import tern.eclipse.ide.server.nodejs.core.TernNodejsCorePlugin;

/**
 * Angular core preferences constants.
 *
 */
public class AngularCorePreferenceConstants {

	/**
	 * Initializes the given preference store with the default values.
	 * 
	 * @param store
	 *            the preference store to be initialized
	 */
	public static void initializeDefaultValues() {
		IEclipsePreferences node = new DefaultScope().getNode(AngularCorePlugin.PLUGIN_ID);
		// directive syntax
		node.putBoolean(AngularCoreConstants.DIRECTIVE_STARTS_WITH_NOTHING, true);
		node.putBoolean(AngularCoreConstants.DIRECTIVE_MINUS_DELIMITER, true);
		// start/end symbols used in angular expression
		node.put(AngularCoreConstants.EXPRESSION_START_SYMBOL, AngularProject.DEFAULT_START_SYMBOL);
		node.put(AngularCoreConstants.EXPRESSION_END_SYMBOL, AngularProject.DEFAULT_END_SYMBOL);

		// Protractor
		node.put(AngularCoreConstants.PROTRACTOR_NODEJS_DEBUGGER, "ProgramNodejs");
		// By default use the embedded Node.js install (if exists)
		if (!useBundledNodeJsInstall(node)) {
			// Use native node.js install in case there is no embedded install.
			node.put(AngularCoreConstants.PROTRACTOR_NODEJS_INSTALL, INodejsInstall.NODE_NATIVE);
			node.put(AngularCoreConstants.PROTRACTOR_NODEJS_PATH, IDENodejsProcessHelper.getNodejsPath());
		}
	}

	// Don't instantiate
	private AngularCorePreferenceConstants() {
	}

	private static boolean useBundledNodeJsInstall(IEclipsePreferences node) {
		INodejsInstallManager installManager = TernNodejsCorePlugin.getNodejsInstallManager();
		INodejsInstall[] installs = installManager.getNodejsInstalls();
		for (INodejsInstall install : installs) {
			if (!install.isNative()) {
				node.put(AngularCoreConstants.PROTRACTOR_NODEJS_INSTALL, install.getId());
				return true;
			}
		}
		return false;
	}
}
