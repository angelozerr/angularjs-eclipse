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
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.ui.handlers.AbstractConvertProjectCommandHandler;
import tern.server.ITernDef;
import tern.server.ITernPlugin;
import tern.server.TernDef;
import tern.server.TernPlugin;

/**
 * Convert selected project to Angular project.
 * 
 */
public class ConvertProjectToAngularCommandHandler extends
		AbstractConvertProjectCommandHandler {

	@Override
	protected String getConvertingProjectJobTitle(IProject project) {
		return NLS
				.bind(AngularUIMessages.ConvertProjectToAngular_converting_project_job_title,
						project.getName());
	}

	@Override
	protected ITernPlugin[] getPlugins(IScopeContext[] fLookupOrder) {
		return new ITernPlugin[] { TernPlugin.angular };
	}

	@Override
	protected ITernDef[] getDefs(IScopeContext[] fLookupOrder) {
		return new ITernDef[] { TernDef.browser, TernDef.ecma5 };
	}

}
