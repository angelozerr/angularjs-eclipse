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
package org.eclipse.angularjs.jsp.internal.ui.org.eclipse.jst.jsp.ui.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.HTMLValidationReporter;
import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.Logger;
//import org.eclipse.jst.jsp.ui.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Text;

/**
 * Source validator for JSP content. This validator currently only validates
 * html content in jsp.
 * 
 * AZERR changes : this class is a copy/paste of org.eclipse.jst.jsp.ui.internal.validation.JSPContentSourceValidator
 *  to extends JSPContentValidator which set getReporter as protected.
 */
public class JSPContentSourceValidator extends JSPContentValidator implements ISourceValidator {
	/*
	 * Most of this class was copied from the ISourceValidator aspects of
	 * html.ui's HTMLValidator
	 */
	private IDocument fDocument;
	private boolean fEnableSourceValidation;
	private IContentType fJSPFContentType = null;

	public void connect(IDocument document) {
		fDocument = document;

		// special checks to see source validation should really execute
		IFile file = null;
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				String baseLocation = model.getBaseLocation();
				// The baseLocation may be a path on disk or relative to the
				// workspace root. Don't translate on-disk paths to
				// in-workspace resources.
				IPath basePath = new Path(baseLocation);
				if (basePath.segmentCount() > 1) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
					/*
					 * If the IFile doesn't exist, make sure it's not returned
					 */
					if (!file.exists())
						file = null;
				}
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		fEnableSourceValidation = (file != null && isBatchValidatorPreferenceEnabled(file) && shouldValidate(file) && fragmentCheck(file));
	}

	public void disconnect(IDocument document) {
		fDocument = null;
	}

	/**
	 * This validate call is for the ISourceValidator partial document
	 * validation approach
	 * 
	 * @param dirtyRegion
	 * @param helper
	 * @param reporter
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
	 */
	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if (helper == null || fDocument == null || !fEnableSourceValidation)
			return;

		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}

		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
		if (model == null)
			return; // error

		try {

			IDOMDocument document = null;
			if (model instanceof IDOMModel) {
				document = ((IDOMModel) model).getDocument();
			}

			if (document == null || !hasHTMLFeature(document))
				return; // ignore

			ITextFileBuffer fb = FileBufferModelManager.getInstance().getBuffer(fDocument);
			if (fb == null)
				return;

			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fb.getLocation());
			if (file == null || !file.exists())
				return;

			// this will be the wrong region if it's Text (instead of Element)
			// we don't know how to validate Text
			IndexedRegion ir = getCoveringNode(dirtyRegion); // model.getIndexedRegion(dirtyRegion.getOffset());
			if (ir instanceof Text) {
				while (ir != null && ir instanceof Text) {
					// it's assumed that this gets the IndexedRegion to
					// the right of the end offset
					ir = model.getIndexedRegion(ir.getEndOffset());
				}
			}

			if (ir instanceof INodeNotifier) {

				INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
				ValidationAdapter adapter = (ValidationAdapter) factory.adapt((INodeNotifier) ir);
				if (adapter == null)
					return; // error

				if (reporter != null) {
					HTMLValidationReporter rep = null;
					rep = getReporter(reporter, file, (IDOMModel) model);
					rep.clear();
					adapter.setReporter(rep);

					Message mess = new LocalizedMessage(IMessage.LOW_SEVERITY, file.getFullPath().toString().substring(1));
					reporter.displaySubtask(this, mess);
				}
				adapter.validate(ir);
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private IndexedRegion getCoveringNode(IRegion dirtyRegion) {

		IndexedRegion largestRegion = null;
		if (fDocument instanceof IStructuredDocument) {
			IStructuredDocumentRegion[] regions = ((IStructuredDocument) fDocument).getStructuredDocumentRegions(dirtyRegion.getOffset(), dirtyRegion.getLength());
			largestRegion = getLargest(regions);
		}
		return largestRegion;
	}

	private IndexedRegion getLargest(IStructuredDocumentRegion[] sdRegions) {

		if (sdRegions == null || sdRegions.length == 0)
			return null;

		IndexedRegion currentLargest = getCorrespondingNode(sdRegions[0]);
		for (int i = 0; i < sdRegions.length; i++) {
			if (!sdRegions[i].isDeleted()) {
				IndexedRegion corresponding = getCorrespondingNode(sdRegions[i]);

				if (currentLargest instanceof Text)
					currentLargest = corresponding;

				if (corresponding != null) {
					if (!(corresponding instanceof Text)) {
						if (corresponding.getStartOffset() <= currentLargest.getStartOffset() && corresponding.getEndOffset() >= currentLargest.getEndOffset())
							currentLargest = corresponding;
					}
				}

			}
		}
		return currentLargest;
	}

	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
		IndexedRegion indexedRegion = null;
		try {
			if (sModel != null)
				indexedRegion = sModel.getIndexedRegion(sdRegion.getStart());
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return indexedRegion;
	}

	private boolean hasHTMLFeature(IDOMDocument document) {
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return false;
		return adapter.hasFeature(HTMLDocumentTypeConstants.HTML);
	}

	/*private HTMLValidationReporter getReporter(IReporter reporter, IFile file, IDOMModel model) {
		return new HTMLValidationReporter(this, reporter, file, model);
	}*/

	/**
	 * Gets current validation configuration based on current project (which
	 * is based on current document) or global configuration if project does
	 * not override
	 * 
	 * @return ValidationConfiguration
	 */
	private ValidationConfiguration getValidationConfiguration(IFile file) {
		ValidationConfiguration configuration = null;
		if (file != null) {
			IProject project = file.getProject();
			if (project != null) {
				try {
					ProjectConfiguration projectConfiguration = ConfigurationManager.getManager().getProjectConfiguration(project);
					configuration = projectConfiguration;
					if (projectConfiguration == null || projectConfiguration.useGlobalPreference()) {
						configuration = ConfigurationManager.getManager().getGlobalConfiguration();
					}
				}
				catch (InvocationTargetException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
		}

		return configuration;
	}

	/**
	 * Checks if validator is enabled according in Validation preferences
	 * 
	 * @param vmd
	 * @return
	 */
	private boolean isBatchValidatorPreferenceEnabled(IFile file) {
		if (file == null) {
			return true;
		}

		boolean enabled = true;
		ValidationConfiguration configuration = getValidationConfiguration(file);
		if (configuration != null) {
			org.eclipse.wst.validation.internal.ValidatorMetaData metadata = ValidationRegistryReader.getReader().getValidatorMetaData(JSPContentValidator.class.getName());
			if (metadata != null) {
				if (!configuration.isBuildEnabled(metadata) && !configuration.isManualEnabled(metadata))
					enabled = false;
			}
		}
		return enabled;
	}

	/**
	 * Checks if file is a jsp fragment or not. If so, check if the fragment
	 * should be validated or not.
	 * 
	 * @param file
	 *            Assumes shouldValidate was already called on file so it
	 *            should not be null and does exist
	 * @return false if file is a fragment and it should not be validated,
	 *         true otherwise
	 */
	private boolean fragmentCheck(IFile file) {
		// copied from JSPValidator
		boolean shouldValidate = true;
		// quick check to see if this is possibly a jsp fragment
		if (getJSPFContentType().isAssociatedWith(file.getName())) {
			// get preference for validate jsp fragments
			boolean shouldValidateFragments = Boolean.valueOf(JSPFContentProperties.getProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, true)).booleanValue();
			/*
			 * if jsp fragments should not be validated, check if file is
			 * really jsp fragment
			 */
			if (!shouldValidateFragments) {
				boolean isFragment = isFragment(file);
				shouldValidate = !isFragment;
			}
		}
		return shouldValidate;
	}

	/**
	 * Determines if file is jsp fragment or not (does a deep, indepth check,
	 * looking into contents of file)
	 * 
	 * @param file
	 *            assumes file is not null and exists
	 * @return true if file is jsp fragment, false otherwise
	 */
	private boolean isFragment(IFile file) {
		// copied from JSPValidator
		boolean isFragment = false;
		InputStream is = null;
		try {
			IContentDescription contentDescription = file.getContentDescription();
			// it can be null
			if (contentDescription == null) {
				is = file.getContents();
				contentDescription = Platform.getContentTypeManager().getDescriptionFor(is, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
			}
			if (contentDescription != null) {
				String fileCtId = contentDescription.getContentType().getId();
				isFragment = (fileCtId != null && ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT.equals(fileCtId));
			}
		}
		catch (IOException e) {
			// ignore, assume it's invalid JSP
		}
		catch (CoreException e) {
			// ignore, assume it's invalid JSP
		}
		finally {
			// must close input stream in case others need it
			if (is != null)
				try {
					is.close();
				}
				catch (Exception e) {
					// not sure how to recover at this point
				}
		}
		return isFragment;
	}

	private boolean shouldValidate(IFile file) {
		// copied from JSPValidator
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	/**
	 * Returns JSP fragment content type
	 * 
	 * @return jspf content type
	 */
	private IContentType getJSPFContentType() {
		// copied from JSPValidator
		if (fJSPFContentType == null) {
			fJSPFContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
		}
		return fJSPFContentType;
	}
}
