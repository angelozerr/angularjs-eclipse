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
package org.eclipse.angularjs.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.html.core.internal.validation.HTMLValidator;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * This class extends {@link HTMLValidator} which validates HTML content
 * (attributes which doesn't exists, etc) when "Validate" action is executed, to
 * ignore error for Angular attribute/element (ng-app, custom directives, etc).
 *
 */
public class HTMLAngularValidator extends HTMLValidator {

	@Override
	protected HTMLValidationReporter getReporter(IReporter reporter,
			IFile file, IDOMModel model) {
		return new HTMLAngularValidationReporter(this, reporter, file, model);
	}
	
	/*
	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived()
					|| resource.isTeamPrivateMember()
					|| !resource.isAccessible()
					|| (resource.getName().charAt(0) == '.' && resource
							.getType() == IResource.FOLDER)) {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		ValidationResult result = super
				.validate(resource, kind, state, monitor);
		if (result != null) {
			IReporter reporter = result.getReporter(monitor);
			validateFile(reporter, (IFile) resource, result);
		}
		// if (resource.getType() != IResource.FILE)
		// return null;
		// ValidationResult result = new ValidationResult();
		// IReporter reporter = result.getReporter(monitor);
		// validateFile(null, reporter, (IFile) resource, result);
		return result;
	}

	private void validateFile(IReporter reporter, IFile file,
			ValidationResult result) {
		if (AngularProject.hasAngularNature(file.getProject())) {
			if ((reporter != null) && (reporter.isCancelled() == true)) {
				throw new OperationCanceledException();
			}
			if (!shouldValidate(file)) {
				return;
			}
			IDOMModel model = getModel(file.getProject(), file);
			if (model == null)
				return;
			IStructuredDocumentRegion[] regions = ((IStructuredDocument) model
					.getStructuredDocument()).getStructuredDocumentRegions();
			validate(reporter, file, model, regions);
		}
	}

	private void validate(IReporter reporter, IFile file, IDOMModel model,
			IStructuredDocumentRegion[] regions) {
		for (int i = 0; i < regions.length; i++) {
			validate(regions[i], reporter, file, model);
		}
	}

	private void validate(IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IDOMModel model) {
		System.err.println("error");
	}*/
}
