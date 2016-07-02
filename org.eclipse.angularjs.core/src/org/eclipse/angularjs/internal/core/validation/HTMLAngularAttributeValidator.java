/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.validation;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.internal.core.AngularCoreMessages;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.core.internal.validate.Segment;
import org.eclipse.wst.html.core.validate.extension.CustomValidatorUtil;
import org.eclipse.wst.html.core.validate.extension.IHTMLCustomAttributeValidator;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import tern.ITernFile;
import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.scriptpath.ITernScriptPath;
import tern.server.protocol.type.ValidationTernTypeCollector;

/**
 * Custom HTML angular attribute validator to:
 * <ul>
 * <li>ignore error like "Undefined attribute name (ng-app)." for attribute
 * which is an angular directives which have 'A' restriction (like ng-app)</li>
 * <li>mark error for wrong directive parameter like ng-pluralize/@xxxx</li>
 * </ul>
 * 
 * <pre>
 *   <body ng-app="" /> // ng-app is not marked as error.
 *   <ng-pluralize src="" xxxx=
"" /> // xxxx is marked as error, because xxxx is not a valid directive parameter.
 * </pre>
 */
public class HTMLAngularAttributeValidator extends AbstractHTMLAngularValidator
		implements IHTMLCustomAttributeValidator {

	private Directive directive;
	private Restriction restriction;

	@Override
	public boolean canValidate(IDOMElement target, String attrName) {
		this.directive = null;
		this.restriction = null;
		if (super.hasAngularNature()) {
			// the project has angular nature, the attribute must be validated
			// if:
			// - 1) attribute is an angular attribute directive like @ng-app
			// - 2) attribute is an angular parameter directive like
			// ng-pluralize/@src

			// 1) check if it is an angular attribute directive like @ng-app
			this.restriction = Restriction.A;
			this.directive = getDirective(null, attrName, restriction);
			if (directive == null) {
				// 2) check if it an angular parameter directive like
				// ng-pluralize/@src
				String tagName = target.getTagName();
				this.restriction = Restriction.E;
				this.directive = getDirective(null, tagName, restriction);
			}
			if (directive != null) {
				return true;
			} else {
				this.restriction = null;
				return false;
			}
		}
		return false;
	}

	@Override
	public ValidationMessage validateAttribute(IDOMElement target, String attrName) {
		// Attribute is a directive or directive parameter.
		if (restriction == Restriction.A) {
			// - 1) attribute is an angular attribute directive like @ng-app the
			// attribute is valid.
			return validateAttributeValue(target, attrName);
		}

		// - 2) attribute is an angular parameter directive like
		// ng-pluralize/@src, check if it's a valid directive parameter
		if (directive.getParameter(attrName) == null) {
			String tagName = target.getTagName();
			Segment segment = CustomValidatorUtil.getAttributeSegment((IDOMNode) target.getAttributeNode(attrName),
					CustomValidatorUtil.ATTR_REGION_NAME);
			return new ValidationMessage("Unknown directive parameter for directive " + tagName, segment.getOffset(),
					segment.getLength(), ValidationMessage.ERROR);
		}
		return null;
	}

	private ValidationMessage validateAttributeValue(IDOMElement target, String attrName) {
		AngularType type = directive.getDirectiveType();
		switch (type) {
		case module:
		case controller:
			try {
				IFile file = getFile();
				IDocument document = getDocument();
				IIDETernProject ternProject = AngularProject.getTernProject(file.getProject());
				IDOMAttr attr = (IDOMAttr) target.getAttributeNode(attrName);
				boolean exists = isAngularElementExists(attr, file, document, ternProject, type);
				if (!exists) {
					Segment segment = CustomValidatorUtil.getAttributeSegment(
							(IDOMNode) target.getAttributeNode(attrName), CustomValidatorUtil.ATTR_REGION_VALUE);
					return new ValidationMessage(
							NLS.bind(AngularCoreMessages.Validation_AngularElementNotFound, type.name(),
									attr.getValue()),
							segment.getOffset(), segment.getLength(), ValidationMessage.ERROR);
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error while Angular validator.", e);
			}
			break;
		}
		return null;
	}

	private static boolean isAngularElementExists(IDOMAttr attr, IFile file, IDocument document,
			IIDETernProject ternProject, final AngularType angularType) throws CoreException, IOException, Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(AngularScopeHelper.getAngularValue(attr, angularType));

		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(attr.getOwnerElement(), file, angularType, query);

		ValidationTernTypeCollector collector = new ValidationTernTypeCollector();

		if (scriptPath != null) {
			ternProject.request(query, null, scriptPath, null, null, collector);
		} else {
			ITernFile tf = new TernDocumentFile(file, document);
			ternProject.request(query, null, null, attr, tf, collector);
		}
		return collector.isExists();
	}
}
