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
package org.eclipse.angularjs.ui.validation;

import org.eclipse.angularjs.core.validation.ValidatorUtils;
import org.eclipse.angularjs.internal.ui.validation.AbstractValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public class HTMLAngularContentValidator extends AbstractValidator {

	@Override
	protected void doValidate(
			IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model) {
		ValidatorUtils.validate(structuredDocumentRegion, reporter,
				file, model, this);
	}
}
