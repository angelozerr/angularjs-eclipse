package org.eclipse.angularjs.core;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.core.documentModel.dom.IAngularDirectiveProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

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
		if ((attr instanceof IAngularDirectiveProvider)) {
			return ((IAngularDirectiveProvider) attr).getAngularDirective();
		}
		return super.getAngularDirective(attr);
	}

	@Override
	public Directive getAngularDirective(Element element) {
		if (element == null) {
			return null;
		}
		if ((element instanceof IAngularDirectiveProvider)) {
			return ((IAngularDirectiveProvider) element).getAngularDirective();
		}
		return super.getAngularDirective(element);
	}
}
