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
package org.eclipse.angularjs.internal.core.documentModel.dom;

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelUpdater;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularDOMModelUpdater extends XMLModelUpdater {
	public AngularDOMModelUpdater(DOMModelImpl model) {
		super(model);
	}

	protected boolean isNestedTagClose(String regionType) {
		return regionType == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE;
	}
}
