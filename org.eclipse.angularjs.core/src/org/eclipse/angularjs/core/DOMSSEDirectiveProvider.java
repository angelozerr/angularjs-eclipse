package org.eclipse.angularjs.core;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.w3c.dom.Attr;

import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.modules.Directive;
import tern.angular.modules.IDirectiveProvider;

public class DOMSSEDirectiveProvider extends DOMDirectiveProvider {

	private static final IDirectiveProvider INSTANCE = new DOMSSEDirectiveProvider();

	public static IDirectiveProvider getInstance() {
		return INSTANCE;
	}

	@Override
	public Directive getAngularDirective(Attr attr) {
		if (attr == null) {
			return null;
		}
		if ((attr instanceof IAngularDOMAttr)) {
			return ((IAngularDOMAttr) attr).getAngularDirective();
		}
		return super.getAngularDirective(attr);
	}
}
