package org.eclipse.angularjs.core.launchConfigurations;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import tern.eclipse.ide.server.nodejs.core.debugger.INodejsDebugger;
import tern.eclipse.ide.server.nodejs.core.debugger.NodejsDebuggersManager;
import tern.eclipse.ide.server.nodejs.core.debugger.VariableHelper;
import tern.utils.StringUtils;

public class ProtractorLaunchHelper {

	public static IFile getProtractorConfigFile(String param) throws ProtractorConfigException, CoreException {
		if (StringUtils.isEmpty(param)) {
			throw new ProtractorConfigException("Protractor config file cannot be empty.");
		}
		IFile configFile = VariableHelper.getResource(param);
		if (configFile != null && configFile.exists()) {
			return configFile;
		}
		throw new ProtractorConfigException("Cannot find protractor config file");
	}

	public static IFile getProtractorCliFile(String param) throws ProtractorConfigException, CoreException {
		if (StringUtils.isEmpty(param)) {
			throw new ProtractorConfigException("Protractor cli file cannot be empty.");
		}
		IFile configFile = VariableHelper.getResource(param);
		if (configFile != null && configFile.exists()) {
			return configFile;
		}
		throw new ProtractorConfigException("Cannot find protractor/lib/cli.js");
	}

	public static File getNodeInstallPath(String param) throws ProtractorConfigException, CoreException {
		if (StringUtils.isEmpty(param)) {
			throw new ProtractorConfigException("Node.js install path cannot be empty.");
		}
		File nodeInstallPath = new File(param);
		if (nodeInstallPath.exists()) {
			return nodeInstallPath;
		}
		throw new ProtractorConfigException("Cannot find node install path " + nodeInstallPath.toString());
	}

	public static INodejsDebugger getDebugger(String debuggerId) throws CoreException, ProtractorConfigException {
		if (StringUtils.isEmpty(debuggerId)) {
			throw new ProtractorConfigException("Tern debugger cannot be empty.");
		}
		INodejsDebugger debugger = NodejsDebuggersManager.getDebugger(debuggerId);
		if (debugger != null) {
			return debugger;
		}
		throw new ProtractorConfigException("Cannot find debugger with id" + debuggerId);
	}

}
