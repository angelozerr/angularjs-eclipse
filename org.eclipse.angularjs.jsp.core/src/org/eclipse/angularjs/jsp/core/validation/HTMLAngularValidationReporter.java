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

import org.eclipse.angularjs.core.validation.ValidatorUtils;
import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.HTMLValidationReporter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * This class extends {@link HTMLValidationReporter} which reports HTML errors (attributes
 * which doesn't exists, etc) to ignore error for Angular attribute/element
 * (ng-app, custom directives, etc).
 *
 */
public class HTMLAngularValidationReporter extends HTMLValidationReporter {

	private final IProject project;

	public HTMLAngularValidationReporter(IValidator owner, IReporter reporter,
			IFile file, IStructuredModel model) {
		super(owner, reporter, file, model);
		this.project = file.getProject();
	}

	@Override
	public void report(ErrorInfo info) {
		if (!ValidatorUtils.isIgnoreError(info, project)) {
			// report the error
			super.report(info);
		}
	}

}
