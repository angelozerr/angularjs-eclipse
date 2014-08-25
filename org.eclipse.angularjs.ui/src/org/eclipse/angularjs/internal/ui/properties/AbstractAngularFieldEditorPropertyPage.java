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
package org.eclipse.angularjs.internal.ui.properties;

import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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

	public AngularProject getAngularProject() throws CoreException {
		return AngularProject.getAngularProject(getResource().getProject());
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

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		IProject project = null;
		try {
			project = getAngularProject().getProject();
		} catch (CoreException e) {
		}
		IScopeContext projectScope = new ProjectScope(project);
		return new ScopedPreferenceStore(projectScope,
				AngularCorePlugin.PLUGIN_ID);
	}
}
