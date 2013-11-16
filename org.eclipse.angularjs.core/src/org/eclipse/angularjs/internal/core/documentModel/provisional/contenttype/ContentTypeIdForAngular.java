package org.eclipse.angularjs.internal.core.documentModel.provisional.contenttype;

import org.eclipse.angularjs.internal.core.AngularCorePlugin;

public class ContentTypeIdForAngular {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_Angular = getConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForAngular() {
		super();
	}

	static String getConstantString() {
		return AngularCorePlugin.ID + ".angularsource"; //$NON-NLS-1$
	}
}
