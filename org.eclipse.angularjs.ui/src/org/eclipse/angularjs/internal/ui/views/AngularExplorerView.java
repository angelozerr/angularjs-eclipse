package org.eclipse.angularjs.internal.ui.views;

import org.eclipse.angularjs.internal.ui.views.actions.GoToDefinitionAction;
import org.eclipse.angularjs.internal.ui.views.actions.LinkToControllerAction;
import org.eclipse.angularjs.internal.ui.views.actions.RefreshExplorerAction;
import org.eclipse.angularjs.internal.ui.views.actions.UnLinkToControllerAction;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import tern.eclipse.ide.core.IDETernProject;

public class AngularExplorerView extends ViewPart implements ISelectionListener {

	private IWorkbenchPart currentEditor;
	private IDETernProject currentTernProject;
	private TreeViewer viewer;

	private LinkToControllerAction linkAction;
	private UnLinkToControllerAction unLinkAction;
	private GoToDefinitionAction openAction;
	private RefreshExplorerAction refreshAction;

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
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateEnabledActions();
			}

		});

		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Tree tree = (Tree) e.getSource();
				if (tree.getSelectionCount() > 0) {
					Object firstSelection = tree.getSelection()[0].getData();
					if (firstSelection instanceof IOpenableInEditor) {
						((IOpenableInEditor) firstSelection).openInEditor();
					}
				}
			}

		});

		registerActions();
		registerContextMenu();

		getSite().getWorkbenchWindow().getPartService()
				.addPartListener(partListener);

		updateEnabledActions();
	}

	private void updateEnabledActions() {
		this.linkAction.setEnabled(false);
		this.unLinkAction.setEnabled(false);
		this.openAction.setEnabled(false);
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (!selection.isEmpty()) {
			Object firstSelection = selection.getFirstElement();
			this.openAction
					.setEnabled(firstSelection instanceof IOpenableInEditor);
		}
	}

	public void registerActions() {
		IToolBarManager manager = getViewSite().getActionBars()
				.getToolBarManager();

		this.linkAction = new LinkToControllerAction(viewer);
		manager.add(linkAction);
		this.unLinkAction = new UnLinkToControllerAction(viewer);
		manager.add(unLinkAction);

		this.openAction = new GoToDefinitionAction(viewer);
		manager.add(openAction);
		this.refreshAction = new RefreshExplorerAction(viewer);
		manager.add(refreshAction);
	}

	private void registerContextMenu() {
		MenuManager contextMenu = new MenuManager();
		// contextMenu.setRemoveAllWhenShown(true);
		// getSite().registerContextMenu(contextMenu, this.viewer);
		contextMenu.add(refreshAction);
		contextMenu.add(openAction);

		Control control = this.viewer.getControl();
		Menu menu = contextMenu.createContextMenu(control);
		control.setMenu(menu);
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
