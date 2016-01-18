/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.launchConfigurations;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.ui.externaltools.internal.ui.FileSelectionDialog;

import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.NodejsCliFileHelper;

/**
 * Main tab
 *
 */
public class MainTab extends ExternalToolsMainTab {

	private String fCurrentLocation = null;
	private IFile fNewFile;

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		try {
			fCurrentLocation = configuration.getAttribute(IExternalToolConstants.ATTR_LOCATION, (String) null);
		} catch (CoreException e) {
			// do nothing
		}
		updateCheckButtons(configuration);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		try {
			// has the location changed
			String newLocation = configuration.getAttribute(IExternalToolConstants.ATTR_LOCATION, (String) null);
			if (newLocation != null) {
				if (!newLocation.equals(fCurrentLocation)) {
					updateTargetsTab();
					fCurrentLocation = newLocation;
					updateProjectName(configuration);
				}
			} else if (fCurrentLocation != null) {
				updateTargetsTab();
				fCurrentLocation = newLocation;
				updateProjectName(configuration);
			}
		} catch (CoreException e) {
			// do nothing
		}

	}

	private void updateProjectName(ILaunchConfigurationWorkingCopy configuration) {
		IFile file = getIFile(configuration);
		String projectName = ""; // IAntCoreConstants.EMPTY_STRING;
		if (file != null) {
			projectName = file.getProject().getName();
		}
		// configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
		// projectName);
	}

	private IFile getIFile(ILaunchConfigurationWorkingCopy configuration) {
		IFile file = null;
		if (fNewFile != null) {
			file = fNewFile;
			fNewFile = null;
		} else {
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				String location = configuration.getAttribute(IExternalToolConstants.ATTR_LOCATION, (String) null);
				if (location != null) {
					String expandedLocation = manager.performStringSubstitution(location);
					if (expandedLocation != null) {
						file = NodejsCliFileHelper.getFileForLocation(expandedLocation);
					}
				}
			} catch (CoreException e) {
				// do nothing
			}
		}
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.
	 * swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(mainComposite,
		// IAntUIHelpContextIds.ANT_MAIN_TAB);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gridData);
		mainComposite.setFont(parent.getFont());
		createLocationComponent(mainComposite);
		createWorkDirectoryComponent(mainComposite);
		createArgumentComponent(mainComposite);
		createVerticalSpacer(mainComposite, 2);
		Dialog.applyDialogFont(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.externaltools.internal.launchConfigurations.
	 * ExternalToolsMainTab#setDefaults(org.eclipse.debug.core.
	 * ILaunchConfigurationWorkingCopy )
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
		// prevent a new blank configuration from being dirty when first created
		// and not yet edited
		// setMappedResources(configuration);
	}

	private void updateCheckButtons(ILaunchConfiguration configuration) {
		/*
		 * boolean setInputHandler = true; try { setInputHandler =
		 * configuration.getAttribute(IAntUIConstants.SET_INPUTHANDLER, true); }
		 * catch (CoreException ce) {
		 * AntUIPlugin.log(JSBuildFileLaunchConfigurationMessages.AntMainTab_1,
		 * ce); } fSetInputHandlerButton.setSelection(setInputHandler);
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.externaltools.internal.launchConfigurations.
	 * ExternalToolsMainTab#handleWorkspaceLocationButtonSelected()
	 */
	@Override
	protected void handleWorkspaceLocationButtonSelected() {
		FileSelectionDialog dialog;
		dialog = new FileSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(),
				AngularUIMessages.Protractor_MainTab_Select_a_protractor_config_file);
		dialog.open();
		IStructuredSelection result = dialog.getResult();
		if (result == null) {
			return;
		}
		Object file = result.getFirstElement();
		if (file instanceof IFile) {
			fNewFile = (IFile) file;
			locationField.setText(NodejsCliFileHelper.getWorkspaceLoc(fNewFile)); // $NON-NLS-1$
		}
	}

	private void updateTargetsTab() {
		// the location has changed...set the targets tab to
		// need to be recomputed
		ILaunchConfigurationTab[] tabs = getLaunchConfigurationDialog().getTabs();
		for (int i = 0; i < tabs.length; i++) {
			ILaunchConfigurationTab tab = tabs[i];
			/*
			 * if (tab instanceof AntTargetsTab) { ((AntTargetsTab)
			 * tab).setDirty(true); break; }
			 */
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.externaltools.internal.launchConfigurations.
	 * ExternalToolsMainTab#getLocationLabel()
	 */
	@Override
	protected String getLocationLabel() {
		return AngularUIMessages.Protractor_MainTab_location_label;
	}
}
