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
package org.eclipse.angularjs.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import tern.eclipse.ide.core.preferences.PreferencesSupport;
import tern.eclipse.ide.server.nodejs.core.INodejsInstall;
import tern.eclipse.ide.server.nodejs.core.TernNodejsCorePlugin;
import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.NodejsCliFileConfigException;
import tern.eclipse.ide.server.nodejs.core.debugger.launchConfigurations.NodejsCliFileHelper;
import tern.utils.StringUtils;

public class AngularCorePreferencesSupport {

	private static final Preferences store = AngularCorePlugin.getDefault().getPluginPreferences();
	private PreferencesSupport preferencesSupport;

	private AngularCorePreferencesSupport() {
		preferencesSupport = new PreferencesSupport(AngularCorePlugin.PLUGIN_ID, store);
	}

	private static AngularCorePreferencesSupport instance = null;

	public static AngularCorePreferencesSupport getInstance() {
		if (instance == null) {
			instance = new AngularCorePreferencesSupport();
		}
		return instance;
	}

	public boolean isDirectiveUseOriginalName(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_USE_ORIGINAL_NAME);
	}

	public boolean isDirectiveStartsWithNothing(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_STARTS_WITH_NOTHING);
	}

	public boolean isDirectiveStartsWithX(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_STARTS_WITH_X);
	}

	public boolean isDirectiveStartsWithData(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_STARTS_WITH_DATA);
	}

	public boolean isDirectiveColonDelimiter(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_COLON_DELIMITER);
	}

	public boolean isDirectiveMinusDelimiter(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_MINUS_DELIMITER);
	}

	public boolean isDirectiveUnderscoreDelimiter(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_UNDERSCORE_DELIMITER);
	}

	public boolean getBool(IProject project, String key) {
		String result = preferencesSupport.getPreferencesValue(key, null, project);
		return StringUtils.asBoolean(result, false);
	}

	/**
	 * Return the {@link IEclipsePreferences} from the given project.
	 * 
	 * @param project
	 * @return the {@link IEclipsePreferences} from the given project.
	 */
	public IEclipsePreferences getEclipsePreferences(IProject project) {
		return preferencesSupport.getEclipsePreferences(project);
	}

	/**
	 * Returns the start symbol to use for angular expression inside HTML for
	 * the given project.
	 * 
	 * @param project
	 * @return the start symbol to use for angular expression inside HTML for
	 *         the given project.
	 */
	public String getStartSymbol(IProject project) {
		return preferencesSupport.getPreferencesValue(AngularCoreConstants.EXPRESSION_START_SYMBOL,
				AngularProject.DEFAULT_START_SYMBOL, project);
	}

	/**
	 * Returns the end symbol to use for angular expression inside HTML for the
	 * given project.
	 * 
	 * @param project
	 * @return the end symbol to use for angular expression inside HTML for the
	 *         given project.
	 */
	public String getEndSymbol(IProject project) {
		return preferencesSupport.getPreferencesValue(AngularCoreConstants.EXPRESSION_END_SYMBOL,
				AngularProject.DEFAULT_END_SYMBOL, project);
	}

	// ----------------------- Protractor

	public String getDefaultProtractorNodeInstall() {
		return preferencesSupport.getWorkspacePreferencesValue(AngularCoreConstants.PROTRACTOR_NODEJS_INSTALL);
	}

	public String getDefaultProtractorNodePath() {
		return preferencesSupport.getWorkspacePreferencesValue(AngularCoreConstants.PROTRACTOR_NODEJS_PATH);
	}

	public String getDefaultProtractorDebugger() {
		return preferencesSupport.getWorkspacePreferencesValue(AngularCoreConstants.PROTRACTOR_NODEJS_DEBUGGER);
	}

	public IFile getDefaultProtractorCliFile() throws NodejsCliFileConfigException, CoreException {
		String protractorCliFile = preferencesSupport
				.getWorkspacePreferencesValue(AngularCoreConstants.PROTRACTOR_DEFAULT_CLI_FILE);
		return NodejsCliFileHelper.getCliFile(protractorCliFile);
	}

}
