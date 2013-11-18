/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.preferences;

import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;

public class AngularEditorPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private Composite createComposite(Composite parent, int numColumns) {
		noDefaultAndApplyButton();

		Composite composite = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected Control createContents(Composite parent) {
		Composite composite = createScrolledComposite(parent);

		String description = AngularUIMessages.AngularEditorPreferencesPage_desc; 
		Text text = new Text(composite, SWT.READ_ONLY);
		// some themes on GTK have different background colors for Text and Labels
		text.setBackground(composite.getBackground());
		text.setText(description);

		setSize(composite);
		return composite;
	}

	private Composite createScrolledComposite(Composite parent) {
		// create scrollbars for this parent when needed
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = createComposite(sc1, 1);
		sc1.setContent(composite);

		// not calling setSize for composite will result in a blank composite,
		// so calling it here initially
		// setSize actually needs to be called after all controls are created,
		// so scrolledComposite
		// has correct minSize
		setSize(composite);
		return composite;
	}

	public void init(IWorkbench workbench) {
	}

	private void setSize(Composite composite) {
		if (composite != null) {
			// Note: The font is set here in anticipation that the class inheriting
			//       this base class may add widgets to the dialog.   setSize
			//       is assumed to be called just before we go live.
			applyDialogFont(composite);
			Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			composite.setSize(minSize);
			// set scrollbar composite's min size so page is expandable but
			// has scrollbars when needed
			if (composite.getParent() instanceof ScrolledComposite) {
				ScrolledComposite sc1 = (ScrolledComposite) composite.getParent();
				sc1.setMinSize(minSize);
				sc1.setExpandHorizontal(true);
				sc1.setExpandVertical(true);
			}
		}
	}
}
