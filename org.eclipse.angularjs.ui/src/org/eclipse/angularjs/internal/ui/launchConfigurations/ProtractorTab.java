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

import org.eclipse.angularjs.core.AngularCorePreferencesSupport;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.resources.IFile;

import tern.eclipse.ide.server.nodejs.ui.debugger.launchConfigurations.AbstractNodejsCliFileLaunchConfigurationTab;

/**
 * Protractor lib/cli.js configuration.
 *
 */
public class ProtractorTab extends AbstractNodejsCliFileLaunchConfigurationTab {

	@Override
	public String getName() {
		return AngularUIMessages.Protractor_ProtractorTab_name;
	}

	@Override
	protected String getCliFileLabel() {
		return AngularUIMessages.Protractor_ProtractorTab_cliFile;
	}

	@Override
	protected IFile getDefaultCliFile() {
		try {
			return AngularCorePreferencesSupport.getInstance().getDefaultProtractorCliFile();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected String getDefaultDebugger() {
		return AngularCorePreferencesSupport.getInstance().getDefaultProtractorDebugger();
	}

	@Override
	protected String getDefaultNodeInstall() {
		return AngularCorePreferencesSupport.getInstance().getDefaultProtractorNodeInstall();
	}

	@Override
	protected String getDefaultNodePath() {
		return AngularCorePreferencesSupport.getInstance().getDefaultProtractorNodePath();
	}

}
