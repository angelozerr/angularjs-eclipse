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

import org.eclipse.angularjs.core.launchConfigurations.IProtractorLaunchConfigurationConstants;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsLaunchConfigurationMessages;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;

public class ProtractorTab extends AbstractLaunchConfigurationTab {

	private Text locationField;
	private Button workspaceLocationButton;
	private Button variablesLocationButton;

	protected boolean fInitializing = false;
	private boolean userEdited = false;
	protected WidgetListener fListener = new WidgetListener();

	/**
	 * A listener to update for text modification and widget selection.
	 */
	protected class WidgetListener extends SelectionAdapter implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			if (!fInitializing) {
				setDirty(true);
				userEdited = true;
				updateLaunchConfigurationDialog();
			}
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			setDirty(true);
			Object source = e.getSource();
			if (source == workspaceLocationButton) {
				handleWorkspaceLocationButtonSelected();
			}
			/*
			 * else if (source == fileLocationButton) {
			 * handleFileLocationButtonSelected(); } else if (source ==
			 * workspaceWorkingDirectoryButton) {
			 * handleWorkspaceWorkingDirectoryButtonSelected(); } else if
			 * (source == fileWorkingDirectoryButton) {
			 * handleFileWorkingDirectoryButtonSelected(); } else if (source ==
			 * argumentVariablesButton) {
			 * handleVariablesButtonSelected(argumentField); } else if (source
			 * == variablesLocationButton) {
			 * handleVariablesButtonSelected(locationField); } else if (source
			 * == variablesWorkingDirectoryButton) {
			 * handleVariablesButtonSelected(workDirectoryField); }
			 */
		}

	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		mainComposite.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gridData);

		createProtractorCliFileComponent(mainComposite);
	}

	private void createProtractorCliFileComponent(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		String locationLabel = AngularUIMessages.Protractor_ProtractorTab_cliFile;
		group.setText(locationLabel);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayout(layout);
		group.setLayoutData(gridData);

		locationField = new Text(group, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		locationField.setLayoutData(gridData);
		locationField.addModifyListener(fListener);
		addControlAccessibleListener(locationField, group.getText());

		Composite buttonComposite = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(gridData);
		buttonComposite.setFont(parent.getFont());

		workspaceLocationButton = createPushButton(buttonComposite,
				ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab__Browse_Workspace____3, null);
		workspaceLocationButton.addSelectionListener(fListener);
		addControlAccessibleListener(workspaceLocationButton,
				group.getText() + " " + workspaceLocationButton.getText()); //$NON-NLS-1$

		// fileLocationButton= createPushButton(buttonComposite,
		// ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_Brows_e_File_System____4,
		// null);
		// fileLocationButton.addSelectionListener(fListener);
		// addControlAccessibleListener(fileLocationButton, group.getText() + "
		// " + fileLocationButton.getText()); //$NON-NLS-1$

		variablesLocationButton = createPushButton(buttonComposite,
				ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_31, null);
		variablesLocationButton.addSelectionListener(fListener);
		addControlAccessibleListener(variablesLocationButton,
				group.getText() + " " + variablesLocationButton.getText()); //$NON-NLS-1$

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		fInitializing = true;
		updateProtractorCliFile(configuration);
		fInitializing = false;
		setDirty(false);
	}

	private void updateProtractorCliFile(ILaunchConfiguration configuration) {
		String location = IExternalToolConstants.EMPTY_STRING;
		try {
			location = configuration.getAttribute(IProtractorLaunchConfigurationConstants.ATTR_PROTRACTOR_CLI_FILE,
					IExternalToolConstants.EMPTY_STRING);
		} catch (CoreException ce) {
			ExternalToolsPlugin.getDefault().log(
					ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_Error_reading_configuration_10, ce);
		}
		locationField.setText(location);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String location = locationField.getText().trim();
		if (location.length() == 0) {
			configuration.setAttribute(IProtractorLaunchConfigurationConstants.ATTR_PROTRACTOR_CLI_FILE, (String) null);
		} else {
			configuration.setAttribute(IProtractorLaunchConfigurationConstants.ATTR_PROTRACTOR_CLI_FILE, location);
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

	}

	@Override
	public String getName() {
		return AngularUIMessages.Protractor_ProtractorTab_name;
	}

	/**
	 * Prompts the user for a workspace location within the workspace and sets
	 * the location as a String containing the workspace_loc variable or
	 * <code>null</code> if no location was obtained from the user.
	 */
	protected void handleWorkspaceLocationButtonSelected() {
		ResourceSelectionDialog dialog;
		dialog = new ResourceSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(),
				ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_Select_a_resource_22);
		dialog.open();
		Object[] results = dialog.getResult();
		if (results == null || results.length < 1) {
			return;
		}
		IResource resource = (IResource) results[0];
		locationField.setText(newVariableExpression("workspace_loc", resource.getFullPath().toString())); //$NON-NLS-1$
	}

	/**
	 * Returns a new variable expression with the given variable and the given
	 * argument.
	 * 
	 * @see IStringVariableManager#generateVariableExpression(String, String)
	 */
	protected String newVariableExpression(String varName, String arg) {
		return VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression(varName, arg);
	}

	/*
	 * Fix for Bug 60163 Accessibility: New Builder Dialog missing object info
	 * for textInput controls
	 */
	public void addControlAccessibleListener(Control control, String controlName) {
		// strip mnemonic (&)
		String[] strs = controlName.split("&"); //$NON-NLS-1$
		StringBuffer stripped = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			stripped.append(strs[i]);
		}
		control.getAccessible().addAccessibleListener(new ControlAccessibleListener(stripped.toString()));
	}

	private class ControlAccessibleListener extends AccessibleAdapter {
		private String controlName;

		ControlAccessibleListener(String name) {
			controlName = name;
		}

		@Override
		public void getName(AccessibleEvent e) {
			e.result = controlName;
		}

	}

}
