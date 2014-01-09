package org.eclipse.angularjs.internal.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import tern.eclipse.ide.core.IDETernProject;

public class AngularExplorerView extends ViewPart implements
		ISelectionListener {

	private IWorkbenchPart currentEditor;
	private IDETernProject currentTernProject;
	private TreeViewer viewer;

	private IPartListener2 partListener = new IPartListener2() {
		public void partVisible(IWorkbenchPartReference ref) {
			if (ref.getId().equals(getSite().getId())) {
				IWorkbenchPart activePart = ref.getPage().getActivePart();
				if (activePart != null)
					selectionChanged(activePart, ref.getPage().getSelection());
				startListeningForSelectionChanges();
			}
		}

		public void partHidden(IWorkbenchPartReference ref) {
			if (ref.getId().equals(getSite().getId()))
				stopListeningForSelectionChanges();
		}

		public void partInputChanged(IWorkbenchPartReference ref) {

		}

		public void partActivated(IWorkbenchPartReference ref) {
		}

		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}

		public void partClosed(IWorkbenchPartReference ref) {
		}

		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		public void partOpened(IWorkbenchPartReference ref) {
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);

		viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.setContentProvider(new AngularExplorerContentProvider(this));
		viewer.setLabelProvider(new AngularExplorerLabelProvider());

		getSite().getWorkbenchWindow().getPartService()
				.addPartListener(partListener);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	/**
	 * Start to listen for selection changes.
	 */
	protected void startListeningForSelectionChanges() {
		getSite().getPage().addPostSelectionListener(this);
	}

	/**
	 * Stop to listen for selection changes.
	 */
	protected void stopListeningForSelectionChanges() {
		getSite().getPage().removePostSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part.equals(this) || part.equals(currentEditor))
			return;
		currentEditor = part;

		if (part != null && part instanceof IEditorPart) {
			Object sel = ((IEditorPart) part).getEditorInput();
			if (sel instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) sel)
						.getAdapter(IResource.class);
				if (resource != null) {
					IProject project = resource.getProject();
					if (IDETernProject.hasTernNature(project)) {
						try {
							IDETernProject ternProject = IDETernProject
									.getTernProject(project);
							boolean refresh = !ternProject
									.equals(currentTernProject);
							currentTernProject = ternProject;
							if (refresh) {
								// refresh
								viewer.setInput(currentTernProject
										.getScriptPaths());
							}
						} catch (CoreException e) {
							e.printStackTrace();
						}

					}
				}

			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getWorkbenchWindow().getPartService()
				.removePartListener(partListener);
	}

}
