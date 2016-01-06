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
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.Viewer;

import tern.angular.protocol.outline.AngularOutline;
import tern.angular.protocol.outline.IAngularOutlineListener;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.eclipse.ide.ui.views.AbstractTernOutlineContentProvider;
import tern.server.TernPlugin;
import tern.server.protocol.outline.TernOutlineCollector;

public class AngularOutlineContentProvider extends AbstractTernOutlineContentProvider
		implements IAngularOutlineListener, IDocumentListener {

	private static final int UPDATE_DELAY = 500;

	private TernDocumentFile document;

	@Override
	public void dispose() {
		super.dispose();
		if (this.document != null) {
			this.document.getDocument().removeDocumentListener(this);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.setViewer(viewer);
		// Get old document/project
		IDocument oldDocument = null;
		IProject oldProject = null;
		if (this.document != null) {
			oldDocument = document.getDocument();
			oldProject = document.getFile().getProject();
		}
		// Get new document/project
		IDocument newDocument = null;
		IProject newProject = null;
		if (newInput instanceof TernDocumentFile) {
			this.document = (TernDocumentFile) newInput;
		} else if (newInput instanceof IAdaptable) {
			this.document = (TernDocumentFile) ((IAdaptable) newInput).getAdapter(TernDocumentFile.class);
		}
		if (this.document != null) {
			newDocument = document.getDocument();
			newProject = document.getFile().getProject();
		}
		if (oldDocument != newDocument) {
			// document has changed
			if (oldDocument != null) {
				oldDocument.removeDocumentListener(this);
			}
			if (newDocument != null) {
				newDocument.addDocumentListener(this);
			}
		}
		if (oldProject != newProject) {
			// project has changed
			if (oldProject != null) {
				try {
					AngularProject angularProject = AngularProject.getAngularProject(oldProject);
					angularProject.removeAngularOutlineListener(this);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error while getting angular project.", e);
				}
			}
			if (newProject != null) {
				try {
					AngularProject angularProject = AngularProject.getAngularProject(newProject);
					angularProject.addAngularOutlineListener(this);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error while getting angular project.", e);
				}
				// project has changed, refresh the angular outline
				super.refresh();
			}
		}
	}

//	@Override
//	protected TernOutlineCollector loadOutline() throws Exception {
//		IProject project = document.getFile().getProject();
//		IIDETernProject ternProject = TernCorePlugin.getTernProject(project);
//		if (ternProject == null || !ternProject.hasPlugin(TernPlugin.angular1)) {
//			return null;
//		}
//		return AngularProject.getAngularProject(project).getOutlineProvider(document);
//	}

	@Override
	public void changed(AngularOutline outline) {
		Job refreshJob = this.getViewer().getRefreshJob();
		if (refreshJob.getState() != Job.NONE) {
			refreshJob.cancel();
		}
		refreshJob.schedule(UPDATE_DELAY);
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		changed(null);
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}
}
