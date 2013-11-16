package org.eclipse.angularjs.internal.core;

import org.eclipse.core.runtime.Plugin;

public class AngularCorePlugin extends Plugin {

	public static final String ID = "org.eclipse.angularjs.core"; //$NON-NLS-1$

	// The shared instance.
	private static AngularCorePlugin plugin;

	/**
	 * The constructor.
	 */
	public AngularCorePlugin() {
		super();
		plugin = this;
	}

}
