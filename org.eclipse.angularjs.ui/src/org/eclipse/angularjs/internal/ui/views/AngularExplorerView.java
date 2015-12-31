package org.eclipse.angularjs.internal.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import tern.eclipse.ide.ui.views.AbstractTernOutlineView;

public class AngularExplorerView extends AbstractTernOutlineView {

	private final Map<IProject, AngularContentOutlinePage> pageProjects;
	private final Map<IProject, PageRec> pageRecProjects;

	public AngularExplorerView() {
		this.pageProjects = new HashMap<IProject, AngularContentOutlinePage>();
		this.pageRecProjects = new HashMap<IProject, PageRec>();
	}

	@Override
	protected boolean isAdaptFor(IFile file) {
		return AngularProject.hasAngularNature(file.getProject());
	}

	@Override
	protected IContentOutlinePage createOutlinePage(IFile file) {
		IProject project = file.getProject();
		AngularContentOutlinePage page = pageProjects.get(project);
		if (page == null) {
			page = new AngularContentOutlinePage(project);
			pageProjects.put(project, page);
		}
		page.setCurrentFile(file);
		return page;
	}

	@Override
	protected PageRec getPageRec(IWorkbenchPart part, IFile file) {
		return pageRecProjects.get(file.getProject());
	}

	@Override
	protected PageRec createPageRec(IWorkbenchPart part, IContentOutlinePage page, IFile file) {
		PageRec pageRec = super.createPageRec(part, page, file);
		pageRecProjects.put(file.getProject(), pageRec);
		return pageRec;
	}

}
