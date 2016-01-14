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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import tern.eclipse.ide.server.nodejs.ui.preferences.DebuggerFieldEditor;
import tern.eclipse.ide.server.nodejs.ui.preferences.NodeJSConfigEditor;
import tern.eclipse.ide.ui.preferences.FileComboFieldEditor;

/**
 * Protractor preferences page.
 *
 */
public class ProtractorPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.eclipse.angularjs.preferences.protractor";

	private DebuggerFieldEditor debuggerField;
	private NodeJSConfigEditor nodeJSConfigEditor;

	public ProtractorPreferencesPage() {
		super(GRID);
		setDescription(AngularUIMessages.ProtractorPreferencesPage_desc);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_PROTRACTOR));
	}

	@Override
	protected void createFieldEditors() {
		createDebuggerContent(getFieldEditorParent());
	}

	protected void createDebuggerContent(Composite parent) {
		// Debugger setup
		debuggerField = new DebuggerFieldEditor(AngularCoreConstants.PROTRACTOR_NODEJS_DEBUGGER,
				AngularUIMessages.ProtractorPreferencesPage_debugger_label, parent);
		addField(debuggerField);

		// debugger wiki
		Link debuggerWikiLink = debuggerField.createWikiLink(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false, 5, 1);
		gd.horizontalIndent = 25;
		debuggerWikiLink.setLayoutData(gd);

		// Node configuration panel
		nodeJSConfigEditor = new NodeJSConfigEditor(parent, AngularCoreConstants.PROTRACTOR_NODEJS_INSTALL,
				AngularCoreConstants.PROTRACTOR_NODEJS_PATH);
		ComboFieldEditor nodeJSInstallField = nodeJSConfigEditor.getNodeJSInstallField();
		addField(nodeJSInstallField);
		FileComboFieldEditor nativeNodePath = nodeJSConfigEditor.getNativeNodePath();
		addField(nativeNodePath);

	}

	@Override
	public void init(IWorkbench workbench) {

	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, AngularCorePlugin.PLUGIN_ID);
	}
}
