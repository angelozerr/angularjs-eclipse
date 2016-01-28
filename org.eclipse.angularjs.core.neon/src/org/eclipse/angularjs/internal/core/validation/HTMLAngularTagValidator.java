package org.eclipse.angularjs.internal.core.validation;

import org.eclipse.wst.html.core.validate.extension.IHTMLCustomTagValidator;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

import tern.angular.modules.Restriction;

public class HTMLAngularTagValidator extends AbstractHTMLAngularValidator implements IHTMLCustomTagValidator {

	@Override
	protected boolean doCanValidate(IDOMElement target) {
		String tagName = target.getTagName();
		// return true if the tag element is an known angular directive (ex :
		// <ng-pluralize) and false otherwise.
		return (getDirective(null, tagName, Restriction.E) != null);
	}

	@Override
	public ValidationMessage validateTag(IDOMElement target) {
		return IGNORE_VALIDATION_MESSAGE;
	}

}
