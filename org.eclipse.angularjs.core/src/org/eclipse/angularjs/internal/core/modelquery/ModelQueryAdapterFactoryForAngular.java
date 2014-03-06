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



import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.html.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.XMLCatalogIdResolver;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapterImpl;

/**
 * Creates a ModelQueryAdapter for HTML models
 */
@Deprecated
public class ModelQueryAdapterFactoryForAngular extends AbstractAdapterFactory {

	ModelQueryAdapterImpl modelQueryAdapter;
	IStructuredModel modelStateNotifier;
	private InternalModelStateListener internalModelStateListener;

	class InternalModelStateListener implements IModelStateListener {

		/**
		 * @see IModelStateListener#modelAboutToBeChanged(IStructuredModel)
		 */
		public void modelAboutToBeChanged(IStructuredModel model) {
			// ISSUE: should we "freeze" state, or anything?
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			// ISSUE: should we "freeze" state, or anything?

		}

		/**
		 * @see IModelStateListener#modelChanged(IStructuredModel)
		 */
		public void modelChanged(IStructuredModel model) {
			// nothing to do?
		}

		/**
		 * @see IModelStateListener#modelDirtyStateChanged(IStructuredModel,
		 *      boolean)
		 */
		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			// nothing to do
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
			updateResolver(structuredModel);
		}

		/**
		 * @see IModelStateListener#modelResourceDeleted(IStructuredModel)
		 */
		public void modelResourceDeleted(IStructuredModel model) {
			// nothing to do?
		}

		/**
		 * @see IModelStateListener#modelResourceMoved(IStructuredModel,
		 *      IStructuredModel)
		 */
		public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
			modelStateNotifier.removeModelStateListener(this);
			modelStateNotifier = newModel;
			updateResolver(modelStateNotifier);
			modelStateNotifier.addModelStateListener(this);
		}

		private void updateResolver(IStructuredModel model) {
			String baseLocation = model.getBaseLocation();
			IFile baseFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(model.getBaseLocation()));
			if (baseFile != null) {
				if (baseFile.getLocation() != null) {
					baseLocation = baseFile.getLocation().toString();
				}
				if (baseLocation == null && baseFile.getLocationURI() != null) {
					baseLocation = baseFile.getLocationURI().toString();
				}
				if (baseLocation == null) {
					baseLocation = baseFile.getFullPath().toString();
				}
			}
			else {
				baseLocation = model.getBaseLocation();
			}
			modelQueryAdapter.setIdResolver(new XMLCatalogIdResolver(baseLocation, model.getResolver()));
		}

	}


	/**
	 * ModelQueryAdapterFactoryForHTML constructor comment. Note: this is a
	 * case there the key is not exactly same as the class we are after.
	 */
	public ModelQueryAdapterFactoryForAngular() {
		super(ModelQueryAdapter.class, true);
	}

	/**
	 * We need this protected version to allow subclasses to pass up standard
	 * behaviour.
	 * 
	 * @param adapterKey
	 * @param registerAdapters
	 */

	protected ModelQueryAdapterFactoryForAngular(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}



	public INodeAdapterFactory copy() {

		return new ModelQueryAdapterFactoryForAngular();
	}

	public void release() {
		super.release();
		if (modelStateNotifier != null) {
			modelStateNotifier.removeModelStateListener(internalModelStateListener);
		}

		modelStateNotifier = null;

		if (modelQueryAdapter != null) {
			modelQueryAdapter.release();
		}
	}

	/**
	 * createAdapter method comment.
	 * 
	 * XXX: we must make this method more independent of 'location' (at least
	 * provide some fall-back method).
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {

		if (Debug.displayInfo) {
			Logger.log(Logger.INFO_DEBUG, "-----------------------ModelQueryAdapterFactoryForHTML.createAdapter" + target); //$NON-NLS-1$
		}
		if (modelQueryAdapter == null) {
			if (target instanceof IDOMNode) {
				IDOMNode xmlNode = (IDOMNode) target;
				modelStateNotifier = xmlNode.getModel();
				modelStateNotifier.addModelStateListener(getInternalModelStateListener());

				IStructuredModel model = xmlNode.getModel();
				org.eclipse.wst.sse.core.internal.util.URIResolver resolver = model.getResolver();
				if (Debug.displayInfo)
					System.out.println("----------------ModelQueryAdapterFactoryForHTML... baseLocation : " + resolver.getFileBaseLocation()); //$NON-NLS-1$

				/**
				 * XMLCatalogIdResolver currently requires a filesystem
				 * location string. Customarily this will be what is in the
				 * deprecated SSE URIResolver and required by the Common URI
				 * Resolver.
				 */
				URIResolver idResolver = null;
				if (resolver != null) {
					idResolver = new XMLCatalogIdResolver(resolver.getFileBaseLocation(), resolver);
				}
				else {
					/*
					 * 203649 - this block may be necessary due to ordering of
					 * setting the resolver into the model
					 */
					String baseLocation = null;
					String modelsBaseLocation = model.getBaseLocation();
					if (modelsBaseLocation != null) {
						File file = new Path(modelsBaseLocation).toFile();
						if (file.exists()) {
							baseLocation = file.getAbsolutePath();
						}
						else {
							IPath basePath = new Path(model.getBaseLocation());
							IResource derivedResource = null;
							if (basePath.segmentCount() > 1)
								derivedResource = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
							else
								derivedResource = ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0));
							IPath derivedPath = derivedResource.getLocation();
							if (derivedPath != null) {
								baseLocation = derivedPath.toString();
							}
							else {
								URI uri = derivedResource.getLocationURI();
								if (uri != null) {
									baseLocation = uri.toString();
								}
							}
						}
						if(baseLocation == null) {
							baseLocation = modelsBaseLocation;
						}
					}
					idResolver = new XMLCatalogIdResolver(baseLocation, null);
				}
				CMDocumentCache documentCache = new CMDocumentCache();
				ModelQuery modelQuery = new AngularModelQueryImpl(documentCache, idResolver);
				modelQuery.setEditMode(ModelQuery.EDIT_MODE_UNCONSTRAINED);
				modelQueryAdapter = new ModelQueryAdapterImpl(documentCache, modelQuery, idResolver);
			}
		}
		return modelQueryAdapter;
	}



	private final InternalModelStateListener getInternalModelStateListener() {
		if (internalModelStateListener == null) {
			internalModelStateListener = new InternalModelStateListener();
		}
		return internalModelStateListener;
	}
}
