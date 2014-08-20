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
package org.eclipse.angularjs.jsp.core.validation;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.validation.ValidatorUtils;
import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.HTMLValidationReporter;
import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
//import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

//import org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;

/**
 * This class extends {@link JSPContentValidator} which validates HTML content
 * (attributes which doesn't exists, etc) when "Validate" action is executed, to
 * ignore error for Angular attribute/element (ng-app, custom directives, etc).
 *
 */
public class JSPAngularContentValidator extends JSPContentValidator {

	@Override
	protected HTMLValidationReporter getReporter(IReporter reporter,
			IFile file, IDOMModel model) {
		return new HTMLAngularValidationReporter(this, reporter, file, model);
	}

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		ValidationResult result = super
				.validate(resource, kind, state, monitor);
		if (result != null) {
			IReporter reporter = result.getReporter(monitor);
			ValidatorUtils.validateFile(reporter, (IFile) resource,
					result, this);
		}
		return result;
	}

}
