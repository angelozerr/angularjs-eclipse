/**
 *  Copyright (c) 2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 * 		Victor Rubezhny - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.internal.core.content.xpl.ContentDescriberUtilities;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

import tern.eclipse.ide.core.IDETernProject;

/**
 * @deprecated see https://github.com/angelozerr/angularjs-eclipse/issues/84
 *
 */
@Deprecated
public class AngularSourceContentDescriber implements ITextContentDescriber {
	private static final QualifiedName[] EMPTY_OPTIONS = new QualifiedName[0];
	
	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		IProject project = resolveProject(contents);
		if (project == null) 
			return IContentDescriber.INVALID;

		if (!IDETernProject.hasTernNature(project))
			return IContentDescriber.INVALID;
		if (!AngularProject.hasAngularNature(project)) 
			return IContentDescriber.INVALID;
		
		return IContentDescriber.VALID;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return EMPTY_OPTIONS;
	}

	@Override
	public int describe(Reader contents, IContentDescription description)
			throws IOException {
		IProject project = resolveProject(contents);
		if (project == null) 
			return IContentDescriber.INVALID;
		
		if (!IDETernProject.hasTernNature(project))
			return IContentDescriber.INVALID;
		if (!AngularProject.hasAngularNature(project)) 
			return IContentDescriber.INVALID;
		
		return IContentDescriber.VALID;
	}

	private IProject resolveProject(InputStream content) {
		IFile file = ContentDescriberUtilities.resolveFileFromInputStream(content);
		return (file != null ? file.getProject() : null);
	}

	private IProject resolveProject(Reader reader) {
		IFile file = ContentDescriberUtilities.resolveFileFromReader(reader);
		return (file != null ? file.getProject() : null);
	}
}
