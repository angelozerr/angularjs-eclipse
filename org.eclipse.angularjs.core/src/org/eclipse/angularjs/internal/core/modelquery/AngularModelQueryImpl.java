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
package org.eclipse.angularjs.internal.core.modelquery;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.html.core.internal.modelquery.HTMLModelQueryImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularModelQueryImpl extends HTMLModelQueryImpl {

	public AngularModelQueryImpl(CMDocumentCache cache, URIResolver idResolver) {
		super(cache, idResolver);
	}

}
