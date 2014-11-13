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
package org.eclipse.angularjs.internal.ui.views;

import java.util.Collections;

import org.eclipse.angularjs.core.AngularElement;
import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.IDefinitionAware;
import org.eclipse.angularjs.core.Module;
import org.eclipse.angularjs.core.link.AngularLinkHelper;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.views.actions.GoToDefinitionAction;
import org.eclipse.angularjs.internal.ui.views.actions.LinkToControllerAction;
import org.eclipse.angularjs.internal.ui.views.actions.RefreshExplorerAction;
import org.eclipse.angularjs.internal.ui.views.actions.TerminateTernServerAction;
import org.eclipse.angularjs.internal.ui.views.actions.UnLinkToControllerAction;
import org.eclipse.angularjs.internal.ui.views.viewers.AngularExplorerContentProvider;
import org.eclipse.angularjs.internal.ui.views.viewers.AngularExplorerLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import tern.ITernFile;
import tern.angular.AngularType;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.ITernProjectLifecycleListener;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.ui.utils.EditorUtils;
import tern.scriptpath.ITernScriptPath;
import tern.scriptpath.ITernScriptResource;
import tern.server.ITernServer;
import tern.server.ITernServerListener;
import tern.server.protocol.definition.ITernDefinitionCollector;

/**
 * Angular explorer view.
 * 
 */
public class AngularExplorerView extends ViewPart implements
		ISelectionListener, ITernDefinitionCollector, ITernServerListener,
		ITernProjectLifecycleListener {

	private IWorkbenchPart currentEditor;
	private IIDETernProject currentTernProject;
	private IResource currentResource;
	private TreeViewer viewer;

	private TerminateTernServerAction terminateAction;
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
		viewer.setContentProvider(new AngularExplorerContentProvider());
		viewer.setLabelProvider(new AngularExplorerLabelProvider(this));
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
					tryOpenInEditor(firstSelection);
				}
			}
		});

		registerActions();
		registerContextMenu();

		getSite().getWorkbenchWindow().getPartService()
				.addPartListener(partListener);
		TernCorePlugin.addTernProjectLifeCycleListener(this);
		updateEnabledActions();
	}

	/**
	 * Update enabled of actions.
	 */
	private void updateEnabledActions() {
		this.linkAction.setEnabled(false);
		this.unLinkAction.setEnabled(false);
		this.openAction.setEnabled(false);
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (!selection.isEmpty()) {
			Object firstSelection = selection.getFirstElement();
			// Open action (Go To Definition available if teh selected element
			// can be opened in a editor).
			this.openAction
					.setEnabled(firstSelection instanceof ITernScriptResource
							|| firstSelection instanceof IDefinitionAware);
			// Link/Unlink actions
			Module module = null;
			AngularElement controller = null;
			String elementId = null;
			if (firstSelection instanceof Module) {
				// The selected element is a module.
				module = (Module) firstSelection;
			} else if (firstSelection instanceof AngularElement
					&& AngularType.controller == ((AngularElement) firstSelection)
							.getAngularType()) {
				// The selected element is a controller.
				controller = (AngularElement) firstSelection;
				module = controller.getModule();
			}
			IResource resource = getCurrentResource();
			if (resource != null && module != null) {
				boolean isLinked = AngularLinkHelper.isSameController(resource,
						module.getScriptPath(), module.getName(),
						controller != null ? controller.getName() : null, null);
				updateEnabledLinkActions(isLinked);
			}
		}
	}

	public void registerActions() {
		IToolBarManager manager = getViewSite().getActionBars()
				.getToolBarManager();
		this.terminateAction = new TerminateTernServerAction(this);
		manager.add(terminateAction);
		this.linkAction = new LinkToControllerAction(this);
		manager.add(linkAction);
		this.unLinkAction = new UnLinkToControllerAction(this);
		manager.add(unLinkAction);
		this.openAction = new GoToDefinitionAction(this);
		manager.add(openAction);
		this.refreshAction = new RefreshExplorerAction(this);
		manager.add(refreshAction);
	}

	private void registerContextMenu() {
		MenuManager contextMenu = new MenuManager();
		contextMenu.add(linkAction);
		contextMenu.add(unLinkAction);
		contextMenu.add(openAction);
		contextMenu.add(refreshAction);

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
		this.terminateAction.setEnabled(false);
		currentEditor = part;
		if (part != null && part instanceof IEditorPart) {
			currentResource = null;
			Object sel = ((IEditorPart) part).getEditorInput();
			if (sel instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) sel)
						.getAdapter(IResource.class);
				if (resource != null) {
					IProject project = resource.getProject();
					if (AngularProject.hasAngularNature(project)) {
						try {
							IIDETernProject ternProject = TernCorePlugin
									.getTernProject(project);
							boolean projectChanged = !ternProject
									.equals(currentTernProject);
							if (projectChanged) {
								ternProject.addServerListener(this);
								if (currentTernProject != null) {
									currentTernProject
											.removeServerListener(this);
								}
							}
							currentTernProject = ternProject;
							this.terminateAction.setEnabled(!currentTernProject
									.isServerDisposed());
							currentResource = resource;
							if (projectChanged) {
								// refresh
								viewer.setInput(currentTernProject
										.getScriptPaths());
							} else {
								refreshTree(true);
							}
						} catch (CoreException e) {
							e.printStackTrace();
						}

					}
				}

			}
		}
		if (currentResource == null) {
			initializeExplorer();
		}
	}

	protected void initializeExplorer() {
		// The current resources doesn't belong to an angular project,
		// disable angular explorer.
		if (currentTernProject != null) {
			currentTernProject.removeServerListener(this);
		}
		currentTernProject = null;
		currentResource = null;
		viewer.setInput(Collections.emptyList());
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getWorkbenchWindow().getPartService()
				.removePartListener(partListener);
		TernCorePlugin.removeTernProjectLifeCycleListener(this);
		if (currentTernProject != null) {
			currentTernProject.removeServerListener(this);
		}
	}

	public IResource getCurrentResource() {
		return currentResource;
	}

	public IIDETernProject getCurrentTernProject() {
		return currentTernProject;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Refresh the tree of the view.
	 * 
	 * @param updateLabels
	 *            true if only labels of each tree item must be refreshed and
	 *            false otherwise.
	 */
	public void refreshTree(boolean updateLabels) {
		if (!updateLabels) {
			try {
				if (getCurrentTernProject() != null) {
					AngularProject angularProject = AngularProject
							.getAngularProject(getCurrentTernProject()
									.getProject());
					angularProject.cleanModel();
				}
			} catch (CoreException e) {

			}
		}
		Object[] expandedElements = viewer.getExpandedElements();
		TreePath[] expandedTreePaths = viewer.getExpandedTreePaths();
		viewer.refresh(updateLabels);
		viewer.setExpandedElements(expandedElements);
		viewer.setExpandedTreePaths(expandedTreePaths);
	}

	public void updateEnabledLinkActions(boolean isLinked) {
		this.linkAction.setEnabled(!isLinked);
		this.unLinkAction.setEnabled(isLinked);
	}

	public void tryOpenInEditor(Object firstSelection) {
		if (firstSelection instanceof ITernScriptPath) {
			ITernScriptPath scriptPath = (ITernScriptPath) firstSelection;
			ITernFile file = (ITernFile) scriptPath.getAdapter(ITernFile.class);
			if (file != null) {
				tryToOpenFile(file, file.getFullName(currentTernProject), null,
						null);
			}
		} else if (firstSelection instanceof ITernScriptResource) {
			ITernScriptResource scriptResource = (ITernScriptResource) firstSelection;
			ITernFile file = scriptResource.getFile();
			if (file != null) {
				tryToOpenFile(file, file.getFullName(currentTernProject), null,
						null);
			}
		} else if (firstSelection instanceof IDefinitionAware) {
			((IDefinitionAware) firstSelection)
					.findDefinition(AngularExplorerView.this);
		}
	}

	/**
	 * Try to open file.
	 * 
	 * @param file
	 */
	private void tryToOpenFile(ITernFile file, String filename, Long start,
			Long end) {
		IStatus status = openFile(file, filename, start, end);
		if (!status.isOK()) {
			ErrorDialog.openError(getSite().getShell(),
					AngularUIMessages.AngularExplorerView_openFileDialog_title,
					status.getMessage(), status);
		}
	}

	/**
	 * Open the file in an editor if file exists.
	 * 
	 * @param file
	 */
	private IStatus openFile(ITernFile tFile, String filename, Long start,
			Long end) {
		if (tFile == null && filename != null) {
			tFile = getCurrentTernProject().getFile(filename);
		}
		IFile file = (IFile) tFile.getAdapter(IFile.class);
		if (file == null) {
			return new Status(
					IStatus.ERROR,
					AngularUIPlugin.PLUGIN_ID,
					NLS.bind(
							AngularUIMessages.AngularExplorerView_openFile_error,
							filename != null ? filename : "null"));
		}
		if (!file.exists()) {
			return new Status(
					IStatus.ERROR,
					AngularUIPlugin.PLUGIN_ID,
					NLS.bind(
							AngularUIMessages.AngularExplorerView_openFile_error,
							file.getFullPath()));
		}
		EditorUtils.openInEditor(file, start != null ? start.intValue() : 0,
				end != null ? end.intValue() - start.intValue() : 0, true);
		return Status.OK_STATUS;
	}

	@Override
	public void setDefinition(String filename, Long start, Long end) {
		tryToOpenFile(null, filename, start, end);
	}

	@Override
	public void onStart(ITernServer server) {
		terminateAction.setEnabled(true);
	}

	@Override
	public void onStop(ITernServer server) {
		terminateAction.setEnabled(false);
	}

	@Override
	public void handleEvent(IIDETernProject project, LifecycleEventType state) {
		if (project.equals(currentTernProject)) {
			switch (state) {
			case onDisposeAfter:
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						initializeExplorer();
					}
				});
				break;
			case onLoadAfter:
				// refresh
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						viewer.setInput(currentTernProject.getScriptPaths());
					}
				});
				break;
			default:
				break;
			}

		}
	}
}
