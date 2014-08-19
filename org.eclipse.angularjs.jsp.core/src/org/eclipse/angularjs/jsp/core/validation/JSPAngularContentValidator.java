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

import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.HTMLValidationReporter;
import org.eclipse.angularjs.jsp.org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.core.resources.IFile;
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
}
