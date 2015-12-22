/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.dialogs;

import java.util.Comparator;
import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.StringUtils;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.views.viewers.AngularElementLabelProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import tern.angular.protocol.outline.AngularOutline;
import tern.server.protocol.outline.IJSNode;

/**
 * Shows a list of Angular elements (modules, controllers, directives, etc) to
 * the user with a text entry field for a string pattern used to filter the list
 * of angular element.
 *
 */
public class FilteredAngularElementsSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String DIALOG_SETTINGS = "org.eclipse.angularjs.internal.ui.dialogs.FilteredAngularElementsSelectionDialog"; //$NON-NLS-1$

	private final AngularItemsComparator itemsComparator;

	public FilteredAngularElementsSelectionDialog(Shell shell, boolean multi) {
		super(shell, multi);
		super.setImage(ImageResource.getImage(ImageResource.IMG_ANGULARJS));
		this.itemsComparator = new AngularItemsComparator();

		super.setListLabelProvider(AngularElementLabelProvider.INSTANCE);
		super.setListSelectionLabelDecorator(AngularElementLabelProvider.INSTANCE);
		super.setDetailsLabelProvider(AngularElementLabelProvider.INSTANCE);

	}

	@Override
	protected ItemsFilter createFilter() {
		return new AngularItemsFilter();
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider provider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		IProject project = null;
		AngularProject angularProject;
		progressMonitor.beginTask("Searching", projects.length); //$NON-NLS-1$
		for (int i = 0; i < projects.length; i++) {
			project = projects[i];
			if (AngularProject.hasAngularNature(project)) {
				angularProject = AngularProject.getAngularProject(project);
				try {
					AngularOutline outline = angularProject.getAngularOutline();
					List<IJSNode> modules = outline.getChildren();
					for (IJSNode module : modules) {
						// loop for modules
						provider.add(module, itemsFilter);
						// loop for controllers, directives, etc
						for (IJSNode node : module.getChildren()) {
							provider.add(node, itemsFilter);
						}
					}
				} catch (Exception e) {
					IStatus status = new Status(IStatus.ERROR, AngularUIPlugin.PLUGIN_ID,
							"Error while getting angular model", e);
					throw new CoreException(status);
				}
			}
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	@Override
	public String getElementName(Object item) {
		if (item instanceof IJSNode) {
			return ((IJSNode) item).getName();
		}
		return null;
	}

	@Override
	protected Comparator<IJSNode> getItemsComparator() {
		return itemsComparator;
	}

	private class AngularItemsComparator implements Comparator<IJSNode> {

		@Override
		public int compare(IJSNode leftInfo, IJSNode rightInfo) {
			int result = compareName(leftInfo.getName(), rightInfo.getName());
			// if (result != 0)
			return result;
		}

		private int compareName(String leftString, String rightString) {
			int result = leftString.compareToIgnoreCase(rightString);
			if (result != 0 || rightString.length() == 0) {
				return result;
			} else if (isLowerCase(leftString.charAt(0)) && !isLowerCase(rightString.charAt(0))) {
				return +1;
			} else if (isLowerCase(rightString.charAt(0)) && !isLowerCase(leftString.charAt(0))) {
				return -1;
			} else {
				return leftString.compareTo(rightString);
			}
		}

		private boolean isLowerCase(char ch) {
			return Character.toLowerCase(ch) == ch;
		}
	}

	@Override
	protected IStatus validateItem(Object item) {
		if (item == null) {
			return new Status(IStatus.ERROR, AngularUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null); //$NON-NLS-1$
		}
		return new Status(IStatus.OK, AngularUIPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
	}

	/**
	 * Filters types using pattern, scope, element kind and filter extension.
	 */
	private class AngularItemsFilter extends ItemsFilter {

		@Override
		public boolean isConsistentItem(Object item) {
			return true;
		}

		@Override
		public boolean matchItem(Object item) {
			if (item instanceof IJSNode) {
				String name = ((IJSNode) item).getName();
				if (StringUtils.isEmpty(name)) {
					return false;
				}
				return matches(name);
			}
			return matches(item.toString());
		}

	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	public int open() {
		// initialize pattern, when angular elements is selected
		if (getInitialPattern() == null) {
			IWorkbenchWindow window = AngularUIPlugin.getActiveWorkbenchWindow();
			if (window != null) {
				ISelection selection = window.getSelectionService().getSelection();
				if (selection instanceof ITextSelection) {
					String text = ((ITextSelection) selection).getText();
					if (text != null) {
						text = text.trim();
						if (text.length() > 0) {
							// remove quotes
							if (text.startsWith("'") || text.startsWith("\"")) {
								text = text.substring(1, text.length());
							}
							if (text.endsWith("'") || text.endsWith("\"")) {
								text = text.substring(0, text.length() - 1);
							}
							setInitialPattern(text, FULL_SELECTION);
						}
					}
				}
			}
		}
		return super.open();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = AngularUIPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);

		if (settings == null) {
			settings = AngularUIPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}

		return settings;
	}
}
