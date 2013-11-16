package org.eclipse.angularjs.internal.core.documentModel.handler;

import org.eclipse.angularjs.internal.core.documentModel.encoding.FMDocumentCharsetDetector;
import org.eclipse.angularjs.internal.core.documentModel.loader.AngularDocumentLoader;
import org.eclipse.angularjs.internal.core.documentModel.loader.AngularModelLoader;
import org.eclipse.angularjs.internal.core.documentModel.provisional.contenttype.ContentTypeIdForAngular;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;

public class AngularModelHandler extends AbstractModelHandler {

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

	public IModelLoader getModelLoader() {
		return new AngularModelLoader();
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new FMDocumentCharsetDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		return new AngularDocumentLoader();
	}
}
