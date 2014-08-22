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
package org.eclipse.angularjs.internal.core.documentModel.provisional.contenttype;

import org.eclipse.angularjs.core.AngularCorePlugin;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
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
		return AngularCorePlugin.PLUGIN_ID + ".angularsource"; //$NON-NLS-1$
	}
}
