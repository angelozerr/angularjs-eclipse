package org.eclipse.angularjs.internal.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
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
	protected IContentOutlinePage createOutlinePage(IProject project) {
		AngularContentOutlinePage page = new AngularContentOutlinePage(project, this);
		pageProjects.put(project, page);
		return page;
	}

	@Override
	protected IContentOutlinePage getOutlinePage(IProject project) {
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
	public TernOutlineCollector loadOutline(IFile file, IDocument document) throws Exception {
		IProject project = file.getProject();
		IIDETernProject ternProject = TernCorePlugin.getTernProject(project);
		if (ternProject == null || !ternProject.hasPlugin(TernPlugin.angular1)) {
			return null;
		}
		TernDocumentFile ternFile = new TernDocumentFile(file, document);
		return AngularProject.getAngularProject(project).getOutlineProvider(ternFile);
	}

	@Override
	public boolean isOutlineAvailable(IFile file) {
		return true;
	}

}
