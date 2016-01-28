package org.eclipse.angularjs.internal.core.validation;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;

public abstract class AbstractHTMLAngularValidator {

	protected static final ValidationMessage IGNORE_VALIDATION_MESSAGE = new ValidationMessage("", 0,
			ValidationMessage.IGNORE);

	private AngularProject angularProject;

	public void init(IStructuredDocument doc) {
		this.angularProject = null;
		if (doc instanceof IDocument) {
			IFile file = DOMUtils.getFile((IDocument) doc);
			IProject project = file.getProject();
			if (AngularProject.hasAngularNature(project)) {
				try {
					this.angularProject = AngularProject.getAngularProject(project);
				} catch (CoreException e) {
					Trace.trace(Trace.SEVERE, "Error while getting angular project", e);
				}
			}
		}
	}

	public final boolean canValidate(IDOMElement target) {
		if (this.angularProject != null) {
			return doCanValidate(target);
		}
		return false;
	}

	protected Directive getDirective(String tagName, String attrName, Restriction restriction) {
		return AngularModulesManager.getInstance().getDirective(angularProject, tagName, attrName, restriction);
	}
	
	protected abstract boolean doCanValidate(IDOMElement target);
}
