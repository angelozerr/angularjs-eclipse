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
package org.eclipse.angularjs.internal.ui.protractor.launchConfigurations;

import java.io.File;

import org.eclipse.angularjs.core.AngularCorePreferencesSupport;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.preferences.protractor.ProtractorPreferencesPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

import tern.eclipse.ide.server.nodejs.core.debugger.INodejsDebugger;
import tern.eclipse.ide.server.nodejs.core.debugger.NodejsDebuggersManager;
import tern.utils.StringUtils;

/**
 * Protractor launch shortcut.
 *
 */
public class ProtractorLaunchShortcut implements ILaunchShortcut2 {

	@Override
	public void launch(ISelection selection, final String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof IAdaptable) {
				final IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
				launch(resource, mode);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		launch(file, mode);
	}

	protected void launch(IResource resource, final String mode) {
		if (resource != null && resource.getType() == IResource.FILE) {
			try {
				// protractor config file to start
				IFile protractorConfigFile = (IFile) resource;

				// debugger to use
				INodejsDebugger debugger = getDebugger();

				// nodejs install path
				File nodeInstallPath = getNodeInstallPath();

				// protractor/lib/cli.js
				IFile protractorCliFile = getProtractorCliFile(protractorConfigFile);
				ProtractorLauncher launcher = new ProtractorLauncher(protractorConfigFile, protractorCliFile, debugger,
						nodeInstallPath);
				launcher.setLaunchMode(mode);
				launcher.setSaveLaunch(isSaveLaunch());

				launcher.start();
			} catch (ProtractorConfigException e) {
				Shell shell = AngularUIPlugin.getActiveWorkbenchWindow().getShell();
				if (MessageDialog.openConfirm(shell, AngularUIMessages.ProtractorLaunchShortcut_Error,
						e.getMessage() + " Do you want to update Protractor preferences?")) {
					if (PreferencesUtil.createPreferenceDialogOn(shell, ProtractorPreferencesPage.PAGE_ID,
							new String[] { ProtractorPreferencesPage.PAGE_ID }, null).open() == Window.OK) {
						launch(resource, mode);
					}
				}
				return;
			} catch (Exception e) {
				reportError("Error while starting protractor", e);
			}
		}
	}

	private boolean isSaveLaunch() {
		return AngularCorePreferencesSupport.getInstance().isProtractorSaveLaunch();
	}

	private IFile getProtractorCliFile(IFile protractorConfigFile) throws ProtractorConfigException {
		IProject project = protractorConfigFile.getProject();
		IFile cliFile = project.getFile("node_modules/protractor/lib/cli.js");
		if (cliFile != null && cliFile.exists()) {
			return cliFile;
		}
		cliFile = AngularCorePreferencesSupport.getInstance().getProtractorCliFile();
		if (cliFile != null && cliFile.exists()) {
			return cliFile;
		}
		throw new ProtractorConfigException("Cannot find protractor/lib/cli.js");
	}

	private INodejsDebugger getDebugger() throws ProtractorConfigException {
		String debuggerId = AngularCorePreferencesSupport.getInstance().getDebugger();
		if (StringUtils.isEmpty(debuggerId)) {
			throw new ProtractorConfigException("Cannot find debugger.");
		}
		INodejsDebugger debugger = NodejsDebuggersManager.getDebugger(debuggerId);
		if (debugger != null) {
			return debugger;
		}
		throw new ProtractorConfigException("Cannot find debugger with id" + debuggerId);
	}

	private File getNodeInstallPath() throws ProtractorConfigException {
		File nodeInstallPath = AngularCorePreferencesSupport.getInstance().getInstallPath();
		if (nodeInstallPath == null) {
			throw new ProtractorConfigException("Cannot find node install path in a preference.");
		}
		if (nodeInstallPath.exists()) {
			return nodeInstallPath;
		}
		throw new ProtractorConfigException("Cannot find node install path " + nodeInstallPath.toString());
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return null;
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
		return null;
	}

	@Override
	public IResource getLaunchableResource(ISelection selection) {
		return null;
	}

	@Override
	public IResource getLaunchableResource(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		return (IResource) input.getAdapter(IFile.class);
	}

	/**
	 * Opens an error dialog presenting the user with the specified message and
	 * throwable
	 * 
	 * @param message
	 * @param throwable
	 */
	protected static void reportError(String message, Throwable throwable) {
		IStatus status = null;
		if (throwable instanceof CoreException) {
			status = ((CoreException) throwable).getStatus();
		} else {
			status = new Status(IStatus.ERROR, AngularUIPlugin.PLUGIN_ID, 0, message, throwable);
		}
		ErrorDialog.openError(AngularUIPlugin.getActiveWorkbenchWindow().getShell(),
				AngularUIMessages.ProtractorLaunchShortcut_Error,
				AngularUIMessages.ProtractorLaunchShortcut_Protractor_Failed, status);
	}
}
