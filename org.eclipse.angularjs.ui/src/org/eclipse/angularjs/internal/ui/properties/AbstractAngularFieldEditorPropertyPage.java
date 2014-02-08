package org.eclipse.angularjs.internal.ui.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPropertyPage;

import tern.eclipse.ide.core.IDETernProject;

public abstract class AbstractAngularFieldEditorPropertyPage extends
		FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	private IAdaptable element;

	public AbstractAngularFieldEditorPropertyPage() {
		super();
	}

	protected AbstractAngularFieldEditorPropertyPage(int style) {
		super(style);
	}

	protected AbstractAngularFieldEditorPropertyPage(String title, int style) {
		super(title, style);
	}

	protected AbstractAngularFieldEditorPropertyPage(String title,
			ImageDescriptor image, int style) {
		super(title, image, style);
	}

	public IAdaptable getElement() {
		return this.element;
	}

	public void setElement(IAdaptable element) {
		this.element = element;
	}

	public IDETernProject getTernProject() throws CoreException {
		return IDETernProject.getTernProject(getResource().getProject());
	}

	private IResource getResource() {
		IResource resource = null;
		IAdaptable adaptable = getElement();
		if (adaptable instanceof IResource) {
			resource = (IResource) adaptable;
		} else if (adaptable != null) {
			Object o = adaptable.getAdapter(IResource.class);
			if (o instanceof IResource) {
				resource = (IResource) o;
			}
		}
		return resource;
	}

}
