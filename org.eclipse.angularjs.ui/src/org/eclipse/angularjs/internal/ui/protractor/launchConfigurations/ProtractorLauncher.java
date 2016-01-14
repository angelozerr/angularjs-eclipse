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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import tern.TernException;
import tern.eclipse.ide.server.nodejs.core.debugger.INodejsDebugger;
import tern.eclipse.ide.server.nodejs.core.debugger.VariableHelper;
import tern.server.nodejs.process.INodejsLaunchConfiguration;
import tern.server.nodejs.process.INodejsProcess;

/**
 * Protractor launcher.
 *
 */
public class ProtractorLauncher implements INodejsLaunchConfiguration {

	private final IFile protractorConfigFile;
	private final IFile protractorCliFile;
	private final INodejsDebugger debugger;
	private final File nodeInstallPath;

	private String launchMode;
	private boolean saveLaunch;

	public ProtractorLauncher(IFile protractorConfigFile, IFile protractorCliFile, INodejsDebugger debugger,
			File nodeInstallPath) {
		this.protractorConfigFile = protractorConfigFile;
		this.protractorCliFile = protractorCliFile;
		this.debugger = debugger;
		this.nodeInstallPath = nodeInstallPath;
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
		// here we need to generate file system path because Webclispe cannot support
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
		return launchMode;
	}

	public void setLaunchMode(String launchMode) {
		this.launchMode = launchMode;
	}

	@Override
	public boolean isSaveLaunch() {
		return saveLaunch;
	}

	public void setSaveLaunch(boolean saveLaunch) {
		this.saveLaunch = saveLaunch;
	}

	@Override
	public boolean isWaitOnPort() {
		return false;
	}

}
