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
package org.eclipse.angularjs.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

public class AngularUIPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {

		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		PreferenceConstants.initializeDefaultValues();

	/*	IPreferenceStore dltkStore = DLTKUIPlugin.getDefault()
				.getPreferenceStore();
		dltkStore.setDefault(
				org.eclipse.dltk.ui.PreferenceConstants.CODEASSIST_SORTER,
				"org.eclipse.php.ui.AlphabeticSorter"); //$NON-NLS-1$*/
	}

}
