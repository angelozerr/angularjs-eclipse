/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.ui.dialogs;

import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Shell;

/**
 * An Angular Element selection dialog used for opening Angular Elements.
 */
public class OpenAngularElementSelectionDialog extends FilteredAngularElementsSelectionDialog {

	private static final String DIALOG_SETTINGS = "org.eclipse.angularjs.internal.ui.dialogs.OpenAngularElementSelectionDialog"; //$NON-NLS-1$

	public OpenAngularElementSelectionDialog(Shell shell, boolean multi) {
		super(shell, multi);
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = AngularUIPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);

		if (settings == null) {
			settings = AngularUIPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}

		return settings;
	}

}
