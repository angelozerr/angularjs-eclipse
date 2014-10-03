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
package org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
//import org.eclipse.jst.jsp.core.internal.validation.FragmentValidationTools;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
//import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.w3c.dom.Element;

/**
 * This validator validates the contents of the content type of the JSP, like
 * the HTML regions in a JSP with content type="text/html"
 * 
 * AZERR changes : this class is a copy/paste of org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator 
 * to modify access of getReporter method from private to protected
 */
public class JSPContentValidator extends JSPValidator {
	private static final String HTTP_JAVA_SUN_COM_JSP_PAGE = "http://java.sun.com/JSP/Page"; //$NON-NLS-1$
	private static final String XMLNS = "xmlns"; //$NON-NLS-1$
	private static final String XMLNS_JSP = "xmlns:jsp"; //$NON-NLS-1$
	private IContentType fJSPFContentType = null;


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
		boolean shouldValidate = true;
		// quick check to see if this is possibly a jsp fragment
		if (getJSPFContentType().isAssociatedWith(file.getName())) {
			// get preference for validate jsp fragments
			boolean shouldValidateFragments = FragmentValidationTools.shouldValidateFragment(file);
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
	 * Returns JSP fragment content type
	 * 
	 * @return jspf content type
	 */
	private IContentType getJSPFContentType() {
		if (fJSPFContentType == null) {
			fJSPFContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
		}
		return fJSPFContentType;
	}

	/*
	 * Copied from HTMLValidator
	 */
	// AZERR : change private to protected
	protected HTMLValidationReporter getReporter(IReporter reporter, IFile file, IDOMModel model) {
		return new HTMLValidationReporter(this, reporter, file, model);
	}

	/*
	 * Copied from HTMLValidator
	 */
	private boolean hasHTMLFeature(IDOMDocument document) {
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return false;
		return adapter.hasFeature(HTMLDocumentTypeConstants.HTML);
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
			/*
			 * must close input stream in case others need it
			 * (IFile.getContents() requirement as well)
			 */
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

	private boolean isXMLJSP(IDOMDocument document) {
		Element root = document.getDocumentElement();
		return root != null && (root.hasAttribute(XMLNS_JSP) || HTTP_JAVA_SUN_COM_JSP_PAGE.equals(root.getAttribute(XMLNS)));
	}

	private void validate(IFile file, int kind, ValidationState state, IProgressMonitor monitor, IDOMModel model, IReporter reporter) {
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		boolean isXMLJSP = isXMLJSP(document);
		boolean hasHTMLFeature = hasHTMLFeature(document);

		if (hasHTMLFeature && !isXMLJSP) {
			INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
			ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
			if (adapter != null) {
				HTMLValidationReporter rep = getReporter(reporter, file, model);
				rep.clear();
				adapter.setReporter(rep);
				adapter.validate(document);
			}
		}
		if (!hasHTMLFeature && isXMLJSP) {
			Validator xmlValidator = new Validator();
			xmlValidator.validate(file, kind, state, monitor);
		}
	}


	/*
	 * Mostly copied from HTMLValidator
	 */
	private void validate(IReporter reporter, IFile file, IDOMModel model) {
		if (file == null || model == null)
			return; // error
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		// This validator currently only handles validating HTML content in
		// JSP
		boolean hasXMLFeature = isXMLJSP(document);
		boolean hasHTMLFeature = hasHTMLFeature(document);
		if (hasHTMLFeature && !hasXMLFeature) {
			INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
			ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
			if (adapter == null)
				return; // error

			HTMLValidationReporter rep = getReporter(reporter, file, model);
			rep.clear();
			adapter.setReporter(rep);
			adapter.validate(document);
		}
	}

	private boolean shouldValidate(IResource resource) {
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	public ValidationResult validate(final IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		if (!shouldValidate(resource))
			return null;
		ValidationResult result = new ValidationResult();
		final IReporter reporter = result.getReporter(monitor);

		if (fragmentCheck((IFile) resource)) {
			IStructuredModel model = null;
			try {
				model = StructuredModelManager.getModelManager().getModelForRead((IFile) resource);
				if (!reporter.isCancelled() && model instanceof IDOMModel) {
					reporter.removeAllMessages(this, resource);
					validate((IFile) resource, kind, state, monitor, (IDOMModel) model, reporter);
				}
			}
			catch (IOException e) {
				Logger.logException(e);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
			finally {
				if (model != null)
					model.releaseFromRead();
			}
		}

		return result;
	}

	protected void validateFile(IFile f, IReporter reporter) {
		IStructuredModel model = null;
		try {
			if (fragmentCheck(f) && !reporter.isCancelled()) {
				model = StructuredModelManager.getModelManager().getModelForRead(f);
				if (!reporter.isCancelled() && model instanceof IDOMModel) {
					reporter.removeAllMessages(this, f);
					validate(reporter, f, (IDOMModel) model);
				}
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

}
