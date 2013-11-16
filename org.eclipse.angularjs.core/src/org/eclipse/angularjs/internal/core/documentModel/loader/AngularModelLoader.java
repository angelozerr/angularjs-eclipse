package org.eclipse.angularjs.internal.core.documentModel.loader;

import java.util.List;

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

	public List getAdapterFactories() {

		// @GINO: Might want to add new adapter factories here
		return super.getAdapterFactories();
	}

	// Creating the FMModel
	public IStructuredModel newModel() {
		return new DOMModelForAngular();
	}

}
