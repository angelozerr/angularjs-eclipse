package org.eclipse.angularjs.core.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;

public class PersistentUtils {

	private static final QualifiedName CONTROLLER_INFO = new QualifiedName(
			TernCorePlugin.PLUGIN_ID + ".resourceprops", "ControllerInfo");

	public static class ControllerInfo {

		private static final String SEPARATOR = "____";
		private ITernScriptPath scriptPath;
		private String module;
		private String controller;

		private ControllerInfo(IResource resource, String resourceInfo)
				throws Exception {
			String[] infos = resourceInfo.split(SEPARATOR);
			if (infos.length != 3) {
				throw new Exception("Cannot find 3 separators " + SEPARATOR);
			}
			IDETernProject ternProject = IDETernProject.getTernProject(resource
					.getProject());
			this.scriptPath = ternProject.getScriptPath(infos[0]);
			if (this.scriptPath == null) {
				throw new Exception("Cannot find script path " + infos[0]);
			}
			this.module = infos[1];
			this.controller = infos[2];
			resource.setSessionProperty(CONTROLLER_INFO, this);
		}

		private ControllerInfo(IResource resource, ITernScriptPath scriptPath,
				String module, String controller) throws Exception {
			this.scriptPath = scriptPath;
			this.module = module;
			this.controller = controller;
			resource.setSessionProperty(CONTROLLER_INFO, this);
			resource.setPersistentProperty(CONTROLLER_INFO, getProperty(this));
		}

		private String getProperty(ControllerInfo controllerInfo) {
			return new StringBuilder(controllerInfo.getScriptPath().getPath())
					.append(SEPARATOR).append(controllerInfo.getModule())
					.append(SEPARATOR).append(controller).toString();
		}

		public ITernScriptPath getScriptPath() {
			return scriptPath;
		}

		public String getModule() {
			return module;
		}

		public String getController() {
			return controller;
		}
	}

	public static ControllerInfo setController(ITernScriptPath scriptPath,
			String module, String controller, IResource resource)
			throws Exception {
		return new ControllerInfo(resource, scriptPath, module, controller);
	}

	public static ControllerInfo getControllerInfo(IResource resource)
			throws Exception {
		ControllerInfo info = (ControllerInfo) resource
				.getSessionProperty(CONTROLLER_INFO);
		if (info == null) {
			String resourceInfo = resource
					.getPersistentProperty(CONTROLLER_INFO);
			if (!StringUtils.isEmpty(resourceInfo)) {
				info = new ControllerInfo(resource, resourceInfo);
			}
		}
		return info;
	}

	public static void removeController(IResource resource)
			throws CoreException {
		resource.setSessionProperty(CONTROLLER_INFO, null);
		resource.setPersistentProperty(CONTROLLER_INFO, null);
	}

	public static boolean hasController(IResource resource) {
		try {
			return getControllerInfo(resource) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isSameController(IResource resource,
			ITernScriptPath scriptPath, String module, String controller) {
		try {
			ControllerInfo info = getControllerInfo(resource);
			if (info != null) {
				if (!info.getScriptPath().getResource().getProject()
						.equals(scriptPath.getResource().getProject())) {
					// not the same project
					return false;
				}
				if (!info.getModule().equals(module)) {
					// not the same module
					return false;
				}
				if (!info.getController().equals(controller)) {
					// not the same controller
					return false;
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
