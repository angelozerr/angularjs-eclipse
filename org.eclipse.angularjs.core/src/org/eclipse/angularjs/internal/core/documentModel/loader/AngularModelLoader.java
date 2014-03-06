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
package org.eclipse.angularjs.internal.core.documentModel.loader;

import org.eclipse.angularjs.internal.core.documentModel.DOMModelForAngular;
import org.eclipse.wst.html.core.internal.encoding.HTMLModelLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class AngularModelLoader extends HTMLModelLoader {

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new AngularDocumentLoader();
		}
		return documentLoaderInstance;
	}

	public IModelLoader newInstance() {
		return new AngularModelLoader();
	}

	/*@Override
	public List getAdapterFactories() {
		List factories = super.getAdapterFactories();
		factories.add(new ModelQueryAdapterFactoryForAngular());
		return factories;
	}*/

	// Creating the FMModel
	public IStructuredModel newModel() {
		return new DOMModelForAngular();
	}

}
