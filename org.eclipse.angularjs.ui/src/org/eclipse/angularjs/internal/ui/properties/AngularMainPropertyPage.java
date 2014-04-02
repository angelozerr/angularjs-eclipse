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
package org.eclipse.angularjs.internal.ui.properties;

import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Angular Main page for project properties.
 * 
 */
public class AngularMainPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	public static final String PROP_ID = "org.eclipse.angularjs.internal.ui.properties.AngularMainPropertyPage";

	public AngularMainPropertyPage() {
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_ANGULARJS));
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(4, 4, true, true));
		composite.setLayout(new GridLayout());
		return composite;
	}

}
