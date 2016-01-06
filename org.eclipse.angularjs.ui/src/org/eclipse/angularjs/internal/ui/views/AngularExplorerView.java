package org.eclipse.angularjs.internal.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.eclipse.ide.ui.views.AbstractTernOutlineView;
import tern.server.TernPlugin;
import tern.server.protocol.outline.TernOutlineCollector;

public class AngularExplorerView extends AbstractTernOutlineView {

	private final Map<IProject, AngularContentOutlinePage> pageProjects;

	public AngularExplorerView() {
		this.pageProjects = new HashMap<IProject, AngularContentOutlinePage>();
	}

	@Override
	protected boolean isAdaptFor(IFile file) {
		return AngularProject.hasAngularNature(file.getProject());
	}

	@Override
	protected IContentOutlinePage createOutlinePage(IWorkbenchPart part, IFile file) {
		IProject project = file.getProject();
		AngularContentOutlinePage page = new AngularContentOutlinePage(project, this);
		pageProjects.put(project, page);
		return page;
	}

	@Override
	protected IContentOutlinePage getOutlinePage(IWorkbenchPart part, IFile file) {
		IProject project = file.getProject();
		IContentOutlinePage page = pageProjects.get(project);
		if (page != null && page.getControl() != null && !page.getControl().isDisposed()) {
			return page;
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		pageProjects.clear();
	}

	@Override
	protected TernOutlineCollector loadOutline() throws Exception {
		IPage page = getCurrentPage();
		if (!(page instanceof AngularContentOutlinePage)) {
			return null;
		}
		TernDocumentFile document = ((AngularContentOutlinePage) page).getTernFile();
		IProject project = document.getFile().getProject();
		IIDETernProject ternProject = TernCorePlugin.getTernProject(project);
		if (ternProject == null || !ternProject.hasPlugin(TernPlugin.angular1)) {
			return null;
		}
		return AngularProject.getAngularProject(project).getOutlineProvider(document);
	}

}
