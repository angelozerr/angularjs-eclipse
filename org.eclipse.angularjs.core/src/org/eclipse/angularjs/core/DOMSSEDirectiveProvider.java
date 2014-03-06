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
package org.eclipse.angularjs.core;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDirectiveProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;

public class DOMSSEDirectiveProvider extends DOMDirectiveProvider {

	private static final DOMSSEDirectiveProvider INSTANCE = new DOMSSEDirectiveProvider();

	public static DOMSSEDirectiveProvider getInstance() {
		return INSTANCE;
	}

	@Override
	public Directive getAngularDirective(Object project, Attr attr) {
		if (attr == null) {
			return null;
		}
		if ((attr instanceof IAngularDirectiveProvider)) {
			return ((IAngularDirectiveProvider) attr).getAngularDirective();
		}
		return super.getAngularDirective(project, attr);
	}

	@Override
	public Directive getAngularDirective(Object project, Element element) {
		if (element == null) {
			return null;
		}
		if ((element instanceof IAngularDirectiveProvider)) {
			return ((IAngularDirectiveProvider) element).getAngularDirective();
		}
		return super.getAngularDirective(project, element);
	}

	@Override
	public DirectiveParameter getAngularDirectiveParameter(Object project,
			Attr attr) {
		if (attr == null) {
			return null;
		}
		if ((attr instanceof IAngularDirectiveProvider)) {
			return ((IAngularDirectiveProvider) attr)
					.getAngularDirectiveParameter();
		}
		return super.getAngularDirectiveParameter(project, attr);
	}
}
