package tern.eclipse.ide.internal.core;

import org.eclipse.core.runtime.Plugin;

public class TernCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "tern.eclipse.ide.core"; //$NON-NLS-1$

	// The shared instance.
	private static TernCorePlugin plugin;

	/**
	 * The constructor.
	 */
	public TernCorePlugin() {
		super();
		plugin = this;
	}

}
