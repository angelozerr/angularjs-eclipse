/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.handlers;

import java.io.IOException;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.server.TernDef;
import tern.server.TernPlugin;

/**
 * Convert selected project to Angular project.
 * 
 */
public class ConvertProjectToAngularCommandHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		final IProject project = getSelectedProject(event);

		if (project == null) {
			return null;
		}

		WorkspaceJob convertJob = new WorkspaceJob(
				AngularUIMessages.ConvertProjectToAngular_converting_project_job_title) {
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				// Add "angular" plugin
				IIDETernProject ternProject = TernCorePlugin.getTernProject(
						project, true);
				ternProject.addPlugin(TernPlugin.angular);

				// Add "browser" + "ecma5" JSON Type Def
				ternProject.addLib(TernDef.browser);
				ternProject.addLib(TernDef.ecma5);

				try {
					ternProject.saveIfNeeded();
				} catch (IOException e) {
					Trace.trace(Trace.SEVERE,
							"Error while configuring angular nature.", e);
				}
				return Status.OK_STATUS;
			}
		};
		convertJob.setUser(true);
		convertJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		convertJob.schedule();

		return null;
	}

	private IProject getSelectedProject(ExecutionEvent event) {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

		if (currentSelection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) currentSelection)
					.getFirstElement();
			return (IProject) Platform.getAdapterManager().getAdapter(element,
					IProject.class);
		}
		return null;
	}

}
