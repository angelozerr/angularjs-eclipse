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
package org.eclipse.angularjs.internal.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Angular Core Plugin.
 * 
 */
public class AngularCoreNeonPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.angularjs.core.neon"; //$NON-NLS-1$

	// The shared instance.
	private static AngularCoreNeonPlugin plugin;

	/**
	 * The constructor.
	 */
	public AngularCoreNeonPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AngularCoreNeonPlugin getDefault() {
		return plugin;
	}
}
