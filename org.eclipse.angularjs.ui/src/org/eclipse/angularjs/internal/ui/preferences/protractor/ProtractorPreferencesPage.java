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
package org.eclipse.angularjs.internal.ui.preferences.protractor;

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import tern.eclipse.ide.server.nodejs.ui.debugger.preferences.AbstractNodejsCliFilePreferencesPage;

/**
 * Protractor preferences page.
 *
 */
public class ProtractorPreferencesPage extends AbstractNodejsCliFilePreferencesPage {

	public static final String PAGE_ID = "org.eclipse.angularjs.preferences.protractor";

	public ProtractorPreferencesPage() {
		super(GRID);
		setDescription(AngularUIMessages.ProtractorPreferencesPage_desc);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_PROTRACTOR));
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, AngularCorePlugin.PLUGIN_ID);
	}

	@Override
	protected String getCliFilePreferenceName() {
		return AngularCoreConstants.PROTRACTOR_DEFAULT_CLI_FILE;
	}

	@Override
	protected String getCliFileLabel() {
		return AngularUIMessages.ProtractorPreferencesPage_debugger_label;
	}

	@Override
	protected String getDebuggerPreferenceName() {
		return AngularCoreConstants.PROTRACTOR_NODEJS_DEBUGGER;
	}

	@Override
	protected String getDebuggerLabel() {
		return AngularUIMessages.ProtractorPreferencesPage_debugger_label;
	}

	@Override
	protected String getNodeJSInstallPreferenceName() {
		return AngularCoreConstants.PROTRACTOR_NODEJS_INSTALL;
	}

	@Override
	protected String getNodeJSPathlPreferenceName() {
		return AngularCoreConstants.PROTRACTOR_NODEJS_PATH;
	}
}
