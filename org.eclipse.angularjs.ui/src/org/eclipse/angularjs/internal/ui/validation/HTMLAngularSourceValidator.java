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
package org.eclipse.angularjs.internal.ui.validation;

import org.eclipse.angularjs.core.validation.HTMLAngularValidationReporter;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.html.core.internal.validation.HTMLValidator;
import org.eclipse.wst.html.internal.validation.HTMLSourceValidator;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * This class extends {@link HTMLSourceValidator} which validates HTML content
 * (attributes which doesn't exists, etc) when user is typing, to ignore error
 * for Angular attribute/element (ng-app, custom directives, etc).
 *
 */
public class HTMLAngularSourceValidator extends HTMLSourceValidator {

	@Override
	protected HTMLValidationReporter getReporter(IReporter reporter,
			IFile file, IDOMModel model) {
		return new HTMLAngularValidationReporter(this, reporter, file, model);
	}

}
