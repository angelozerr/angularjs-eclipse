package org.eclipse.angularjs.internal.ui.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.w3c.dom.Node;

import tern.angular.modules.AngularModulesManager;

public class HTMLAngularValidationReporter extends HTMLValidationReporter {

	private final IProject project;

	public HTMLAngularValidationReporter(IValidator owner, IReporter reporter,
			IFile file, IStructuredModel model) {
		super(owner, reporter, file, model);
		this.project = file.getProject();
	}

	@Override
	public void report(ErrorInfo info) {
		int targetType = info.getTargetType();
		// org.eclipse.wst.html.core.internal.validate.ErrorState.UNDEFINED_NAME_ERROR
		// = 11 is private -(
		if (targetType == Node.ATTRIBUTE_NODE && info.getState() == 11) {
			// It's an error about attribute name, check if it's an Angular
			// Attribute (ex : ng-app)
			String attrName = info.getHint();
			if (AngularModulesManager.getInstance().getDirective(project, null,
					attrName) == null) {
				// it's not an angular directive, report the error
				super.report(info);
			}
		} else {
			// report the error
			super.report(info);
		}
	}

}
