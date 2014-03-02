package org.eclipse.angularjs.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.core.internal.validation.HTMLValidationReporter;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.w3c.dom.Node;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;

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
		if ((targetType == Node.ATTRIBUTE_NODE || targetType == Node.ELEMENT_NODE)
				&& info.getState() == 11) {
			// It's an error about attribute name, check if it's an Angular
			// Attribute (ex : ng-app)
			String name = info.getHint();
			if (!isDirective(project, name, targetType)) {
				// it's not an angular directive, report the error
				super.report(info);
			}
		} else {
			// report the error
			super.report(info);
		}
	}

	/**
	 * Returns true if the given name is a directive for the current node
	 * (attribute, element) and false otherwise.
	 * 
	 * @param project
	 * @param name
	 * @param targetType
	 * @return
	 */
	private boolean isDirective(IProject project, String name, int targetType) {
		Directive directive = AngularModulesManager.getInstance().getDirective(
				project, null, name, getRestriction(targetType));
		return (directive != null);
	}

	private Restriction getRestriction(int targetType) {
		switch (targetType) {
		case Node.ATTRIBUTE_NODE:
			return Restriction.A;
		case Node.ELEMENT_NODE:
			return Restriction.E;
		}
		return null;
	}

}
