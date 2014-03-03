/*******************************************************************************
 * Copyright (c) 2013, 2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.handlers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.angularjs.core.AngularNature;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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

import tern.eclipse.ide.core.TernNature;

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

				IProjectDescription projectDescription = project
						.getDescription();

				// Configure builders:
				/*
				 * List newBuildSpec = new LinkedList(); ICommand[] buildSpec =
				 * projectDescription.getBuildSpec(); for (int c = 0; c <
				 * buildSpec.length; ++c) { if
				 * (!buildSpec[c].getBuilderName().equals( PHPECLIPSE_BUILDER))
				 * { newBuildSpec.add(buildSpec[c]); } } ICommand command =
				 * projectDescription.newCommand();
				 * command.setBuilderName(PHPNature.VALIDATION_BUILDER_ID);
				 * newBuildSpec.add(command);
				 * 
				 * command = projectDescription.newCommand();
				 * newBuildSpec.add(command);
				 * 
				 * projectDescription.setBuildSpec((ICommand[]) newBuildSpec
				 * .toArray(new ICommand[newBuildSpec.size()]));
				 */
				// Configure natures:
				List newNatures = new LinkedList();
				String[] natures = projectDescription.getNatureIds();
				for (int c = 0; c < natures.length; ++c) {
					if (!AngularNature.ID.equals(natures[c]) && 
							!TernNature.ID.equals(natures[c])) {
						newNatures.add(natures[c]);
					}
				}
				newNatures.add(AngularNature.ID);
				newNatures.add(TernNature.ID);

				projectDescription.setNatureIds((String[]) newNatures
						.toArray(new String[newNatures.size()]));

				// Save project description:
				project.setDescription(projectDescription, monitor);
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
