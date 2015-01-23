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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.osgi.util.NLS;

import tern.eclipse.ide.ui.handlers.AbstractConvertProjectCommandHandler;
import tern.server.ITernModule;
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
	protected ITernModule[] getModules(IScopeContext[] fLookupOrder) {
		List<ITernModule> modules = new ArrayList<ITernModule>(
				Arrays.asList(super.getModules(fLookupOrder)));
		if (!modules.contains(TernDef.ecma5)) {
			modules.add(TernDef.ecma5);
		}
		if (!modules.contains(TernDef.browser)) {
			modules.add(TernDef.browser);
		}
		if (!modules.contains(TernPlugin.angular)) {
			modules.add(TernPlugin.angular);
		}
		return modules.toArray(ITernModule.EMPTY_MODULE);
	}
}
