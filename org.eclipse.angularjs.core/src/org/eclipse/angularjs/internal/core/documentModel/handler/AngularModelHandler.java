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
package org.eclipse.angularjs.internal.core.documentModel.handler;

import org.eclipse.angularjs.internal.core.documentModel.encoding.FMDocumentCharsetDetector;
import org.eclipse.angularjs.internal.core.documentModel.loader.AngularDocumentLoader;
import org.eclipse.angularjs.internal.core.documentModel.loader.AngularModelLoader;
import org.eclipse.angularjs.internal.core.documentModel.provisional.contenttype.ContentTypeIdForAngular;
import org.eclipse.wst.html.core.internal.modelhandler.ModelHandlerForHTML;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;

/**
 * Angular Model Handler. This handler extends {@link ModelHandlerForHTML}
 * because AdapterFactoryProviderForJSDT#isFor needs this implementation to
 * manage JS completion in HTML file.
 * 
 */
/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularModelHandler extends ModelHandlerForHTML {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten at
	 * run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.angularjs.core.documentModel.handler"; //$NON-NLS-1$

	public AngularModelHandler() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(ContentTypeIdForAngular.ContentTypeID_Angular);
	}

	@Override
	public IModelLoader getModelLoader() {
		return new AngularModelLoader();
	}

	@Override
	public IDocumentCharsetDetector getEncodingDetector() {
		return new FMDocumentCharsetDetector();
	}

	@Override
	public IDocumentLoader getDocumentLoader() {
		return new AngularDocumentLoader();
	}
}
