package org.eclipse.angularjs.internal.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import tern.eclipse.ide.ui.views.AbstractTernOutlineView;

public class AngularExplorerView extends AbstractTernOutlineView {

	private final Map<IProject, AngularContentOutlinePage> projects;

	public AngularExplorerView() {
		this.projects = new HashMap<IProject, AngularContentOutlinePage>();
	}

	@Override
	protected boolean isAdaptFor(IFile file) {
		return AngularProject.hasAngularNature(file.getProject());
	}

	@Override
	protected IContentOutlinePage createOutlinePage(IFile file) {
		IProject project = file.getProject();
		AngularContentOutlinePage page = projects.get(project);
		if (page == null) {
			page = new AngularContentOutlinePage(project);
			projects.put(project, page);
		}
		page.setCurrentFile(file);
		return page;
	}

}
