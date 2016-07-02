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
package org.eclipse.angularjs.internal.core.validation;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;

/**
 * Abstract class for WTP custom html validator (coming from Eclipse Neon).
 *
 */
public abstract class AbstractHTMLAngularValidator {

	private IProject project;
	private IFile file;
	private IDocument document;

	/**
	 * Cache the project of the given document if project has angular nature.
	 * 
	 * @param doc
	 */
	public void init(IStructuredDocument doc) {
		this.project = null;
		this.file = null;
		this.document = null;
		if (doc instanceof IDocument) {
			this.document = (IDocument) doc;
			file = DOMUtils.getFile(document);
			IProject project = file.getProject();
			if (AngularProject.hasAngularNature(project)) {
				// project has angular nature, cache the project
				this.project = project;
			}
		}
	}

	/**
	 * Returns true if the project has angular nature and false otherwise.
	 * 
	 * @return true if the project has angular nature and false otherwise.
	 */
	protected boolean hasAngularNature() {
		return project != null && AngularProject.hasAngularNature(project);
	}

	/**
	 * Returns the angular directive
	 * 
	 * @param tagName
	 * @param attrName
	 * @param restriction
	 * @return
	 */
	protected Directive getDirective(String tagName, String attrName, Restriction restriction) {
		try {
			AngularProject angularProject = AngularProject.getAngularProject(project);
			return AngularModulesManager.getInstance().getDirective(angularProject, tagName, attrName, restriction);
		} catch (CoreException e) {
			// should never done
			Trace.trace(Trace.SEVERE, "Error while getting angular project", e);
		}
		return null;
	}

	public IFile getFile() {
		return file;
	}

	public IDocument getDocument() {
		return document;
	}
}
