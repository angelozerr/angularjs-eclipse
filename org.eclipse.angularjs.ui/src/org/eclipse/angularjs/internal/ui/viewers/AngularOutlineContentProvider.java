/**
 *  Copyright (c) 2013-2015 Angelo ZERR, and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Mickael Istria (Red Hat Inc.) - reduce coupling to TernOutlineView
 */
package org.eclipse.angularjs.internal.ui.viewers;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;

import tern.angular.protocol.outline.AngularOutline;
import tern.angular.protocol.outline.IAngularOutlineListener;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.ui.views.AbstractTernOutlineContentProvider;
import tern.server.TernPlugin;
import tern.server.protocol.outline.TernOutlineCollector;

public class AngularOutlineContentProvider extends AbstractTernOutlineContentProvider
		implements IAngularOutlineListener {

	private static final int UPDATE_DELAY = 500;

	private IProject project;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this.project != null) {
			try {
				AngularProject angularProject = AngularProject.getAngularProject(project);
				angularProject.removeAngularOutlineListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (newInput instanceof IProject) {
			this.project = (IProject) newInput;
		} else if (newInput instanceof IAdaptable) {
			this.project = (IProject) ((IAdaptable) newInput).getAdapter(IProject.class);
		}
		if (this.project != null) {
			try {
				AngularProject angularProject = AngularProject.getAngularProject(project);
				angularProject.addAngularOutlineListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.inputChanged(viewer, oldInput, newInput);
	}

	@Override
	protected TernOutlineCollector loadOutline() throws Exception {
		IIDETernProject ternProject = TernCorePlugin.getTernProject(project);
		if (ternProject == null || !ternProject.hasPlugin(TernPlugin.angular1)) {
			return null;
		}
		return AngularProject.getAngularProject(project).getOutlineProvider();
	}

	@Override
	public void changed(AngularOutline outline) {
		if (this.refreshJob.getState() != Job.NONE) {
			this.refreshJob.cancel();
		}
		this.refreshJob.schedule(UPDATE_DELAY);
	}

}
