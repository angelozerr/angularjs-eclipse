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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.AbstractNodejsCliFileLaunchConfigurationDelegate;
import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.AbstractNodejsCliFileLauncher;
import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.NodejsCliFileConfigException;

/**
 * Protractor launch configuration delegate.
 *
 */
public class ProtractorLaunchConfigurationDelegate extends AbstractNodejsCliFileLaunchConfigurationDelegate {

	@Override
	protected AbstractNodejsCliFileLauncher createLauncher(ILaunchConfiguration configuration, String mode)
			throws NodejsCliFileConfigException, CoreException {
		return new ProtractorLauncher(configuration, mode);
	}

}
