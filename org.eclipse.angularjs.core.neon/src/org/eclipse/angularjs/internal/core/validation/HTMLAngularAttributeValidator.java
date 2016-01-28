package org.eclipse.angularjs.internal.core.validation;

import org.eclipse.wst.html.core.internal.validate.Segment;
import org.eclipse.wst.html.core.validate.extension.CustomValidatorUtil;
import org.eclipse.wst.html.core.validate.extension.IHTMLCustomAttributeValidator;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;

public class HTMLAngularAttributeValidator extends AbstractHTMLAngularValidator
		implements IHTMLCustomAttributeValidator {

	@Override
	protected boolean doCanValidate(IDOMElement target) {
		// any HTML element could have angular attribute:
		// <body ng-app=""
		// <ng-pluralize src=""
		return true;
	}

	@Override
	public ValidationMessage validateAttribute(IDOMElement target, String attrName) {
		// 1) check if it is a ng-attr
		Directive attrDirective = getDirective(null, attrName, Restriction.A);
		if (attrDirective == null) {
			// 2) check if it's a parameter directive
			String tagName = target.getTagName();
			Directive directive = getDirective(null, tagName, Restriction.E);
			if (directive != null) {
				if (directive.getParameter(attrName) == null) {
					Segment segment = CustomValidatorUtil.getAttributeSegment(
							(IDOMNode) target.getAttributeNode(attrName), CustomValidatorUtil.ATTR_REGION_NAME);
					return new ValidationMessage("Unknown directive parameter", segment.getOffset(),
							segment.getLength(), ValidationMessage.ERROR);

				}
			}
		}
		return null;

		/*
		 * String tagName = target.getTagName(); // if (!checkDirective(tagName,
		 * attrName, Restriction.A)) { // //return new
		 * ValidationMessage(message, lineNumber, columnNumber, uri, key,
		 * messageArguments); // }
		 */
		// return IGNORE_VALIDATION_MESSAGE;
	}

}
