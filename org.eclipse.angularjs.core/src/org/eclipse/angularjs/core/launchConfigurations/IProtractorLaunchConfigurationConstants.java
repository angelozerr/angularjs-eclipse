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
package org.eclipse.angularjs.core.launchConfigurations;

/**
 * Constants for Protractor launch.
 *
 */
public interface IProtractorLaunchConfigurationConstants {

	String ID_PROTRACTOR_LAUNCH_CONFIGURATION_TYPE = "org.eclipse.angularjs.core.protractor";

	String ATTR_NODE_INSTALL_PATH = "nodeinstallpath";

	String ATTR_DEBUGGER = "debugger";

	String ATTR_PROTRACTOR_CLI_FILE = "protractor_cli_file";
}
