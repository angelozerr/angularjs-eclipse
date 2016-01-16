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
package org.eclipse.angularjs.internal.core.launchConfigurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.angularjs.core.launchConfigurations.IProtractorLaunchConfigurationConstants;
import org.eclipse.angularjs.core.launchConfigurations.ProtractorConfigException;
import org.eclipse.angularjs.core.launchConfigurations.ProtractorLaunchHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import tern.TernException;
import tern.eclipse.ide.server.nodejs.core.debugger.INodejsDebugger;
import tern.server.nodejs.process.INodejsLaunchConfiguration;
import tern.server.nodejs.process.INodejsProcess;

/**
 * Launch protractor with tern debugger {@link INodejsDebugger}.
 *
 */
public class ProtractorLauncher implements INodejsLaunchConfiguration {

	private final IFile protractorConfigFile;
	private final IFile protractorCliFile;
	private final INodejsDebugger debugger;
	private final File nodeInstallPath;

	private String mode;

	public ProtractorLauncher(IFile protractorConfigFile, IFile protractorCliFile, INodejsDebugger debugger,
			File nodeInstallPath, String mode) {
		this.protractorConfigFile = protractorConfigFile;
		this.protractorCliFile = protractorCliFile;
		this.debugger = debugger;
		this.nodeInstallPath = nodeInstallPath;
		this.mode = mode;
	}

	public ProtractorLauncher(ILaunchConfiguration configuration, String mode)
			throws CoreException, ProtractorConfigException {
		this(getProtractorConfigFile(configuration), getProtractorCliFile(configuration), getDebugger(configuration),
				getNodeInstallPath(configuration), mode);
	}

	private static IFile getProtractorConfigFile(ILaunchConfiguration configuration)
			throws ProtractorConfigException, CoreException {
		String param = configuration.getAttribute(IProtractorLaunchConfigurationConstants.ATTR_PROTRACTOR_CONFIG_FILE,
				(String) null);
		return ProtractorLaunchHelper.getProtractorConfigFile(param);
	}

	private static IFile getProtractorCliFile(ILaunchConfiguration configuration)
			throws ProtractorConfigException, CoreException {
		String param = configuration.getAttribute(IProtractorLaunchConfigurationConstants.ATTR_PROTRACTOR_CLI_FILE,
				(String) null);
		return ProtractorLaunchHelper.getProtractorCliFile(param);
	}

	private static File getNodeInstallPath(ILaunchConfiguration configuration)
			throws ProtractorConfigException, CoreException {
		String param = configuration.getAttribute(IProtractorLaunchConfigurationConstants.ATTR_NODE_INSTALL_PATH,
				(String) null);
		return ProtractorLaunchHelper.getNodeInstallPath(param);
	}

	private static INodejsDebugger getDebugger(ILaunchConfiguration configuration)
			throws CoreException, ProtractorConfigException {
		String debuggerId = configuration.getAttribute(IProtractorLaunchConfigurationConstants.ATTR_DEBUGGER,
				(String) null);
		return ProtractorLaunchHelper.getDebugger(debuggerId);
	}

	public void start() throws TernException {
		INodejsProcess process = debugger.createProcess(protractorCliFile, protractorConfigFile.getProject(),
				nodeInstallPath);
		process.setLaunchConfiguration(this);
		process.start();
	}

	@Override
	public List<String> createNodeArgs() {
		List<String> args = new ArrayList<String>();
		// here we need to generate file system path because Webclispe cannot
		// support
		// program args like ${workspace_loc:\test-protractor\spec.js}
		args.add(protractorConfigFile.getLocation().toOSString());
		// Uncomment that once Webclipse can support it.
		// args.add(VariableHelper.getWorkspaceLoc(protractorConfigFile));
		return args;
	}

	@Override
	public String generateLaunchConfigurationName() {
		return "Protractor for " + protractorConfigFile.getFullPath().toString();
	}

	@Override
	public String getLaunchMode() {
		return mode;
	}

	@Override
	public boolean isSaveLaunch() {
		return false;
	}

	@Override
	public boolean isWaitOnPort() {
		return false;
	}

}
