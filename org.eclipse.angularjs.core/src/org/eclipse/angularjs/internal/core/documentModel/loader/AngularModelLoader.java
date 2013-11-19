package org.eclipse.angularjs.internal.core.documentModel.loader;

import java.util.List;

import org.eclipse.angularjs.internal.core.documentModel.DOMModelForAngular;
import org.eclipse.angularjs.internal.core.modelquery.ModelQueryAdapterFactoryForAngular;
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
