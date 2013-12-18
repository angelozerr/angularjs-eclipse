package org.eclipse.angularjs.internal.ui.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.html.internal.validation.HTMLSourceValidator;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class HTMLAngularSourceValidator extends HTMLSourceValidator {

	@Override
	protected HTMLValidationReporter getReporter(IReporter reporter,
			IFile file, IDOMModel model) {
		return new HTMLAngularValidationReporter(this, reporter, file, model);
	}
	
}
