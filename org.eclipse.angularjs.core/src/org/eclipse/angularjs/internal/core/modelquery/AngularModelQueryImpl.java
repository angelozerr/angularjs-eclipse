package org.eclipse.angularjs.internal.core.modelquery;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.html.core.internal.modelquery.HTMLModelQueryImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;

@Deprecated
public class AngularModelQueryImpl extends HTMLModelQueryImpl {

	public AngularModelQueryImpl(CMDocumentCache cache, URIResolver idResolver) {
		super(cache, idResolver);
	}

}
