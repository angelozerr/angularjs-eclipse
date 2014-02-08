/*******************************************************************************
 * Copyright (c) 2013 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.properties;

import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tern.eclipse.ide.core.TernCoreConstants;

/**
 * Angular Directive page for project properties.
 * 
 */
public class DirectivesPropertyPage extends
		AbstractAngularFieldEditorPropertyPage {

	public static final String PROP_ID = "org.eclipse.angularjs.internal.ui.properties.DirectivesPropertyPage";

	public DirectivesPropertyPage() {
		super(GRID);
		// setDescription(AngularUIMessages.TernConsolePropertyPage_desc);
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_DIRECTIVE));
	}

	@Override
	protected void createFieldEditors() {

		Composite parent = super.getFieldEditorParent();
		
		BooleanFieldEditor startsWithNothing = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "use origin name", parent);
		addField(startsWithNothing);
		
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createStartsWithPanel(parent);
		separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDelimitersPanel(parent);
		separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite t = new Composite(parent, SWT.NONE);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setLayout(new GridLayout(3, false));
		
		Label l = new Label(t, SWT.NONE);
		l.setText("dede");
		
		Text directiveName = new Text(t, SWT.BORDER);
		directiveName.setText("ngBind");
		directiveName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(t, SWT.NONE);
		l.setText("dede");
		
		StyledText directives = new StyledText(parent, SWT.BORDER | SWT.READ_ONLY);
		directives.setLayoutData(new GridData(GridData.FILL_BOTH));
		directives.setText("ab\nv");
	}

	public void createStartsWithPanel(Composite parent) {
		Label startsWithLabel = new Label(parent, SWT.NONE);
		startsWithLabel.setText("Starts with:");

		BooleanFieldEditor startsWithNothing = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "nothing", parent);
		addField(startsWithNothing);

		BooleanFieldEditor startsWithX = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "'x-'", parent);
		addField(startsWithX);

		BooleanFieldEditor startsWithData = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "'data-'", parent);
		addField(startsWithData);
	}

	public void createDelimitersPanel(Composite parent) {
		Label delimitersLabel = new Label(parent, SWT.NONE);
		delimitersLabel.setText("Delimiter:");

		BooleanFieldEditor startsWithNothing = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "':'", parent);
		addField(startsWithNothing);

		BooleanFieldEditor startsWithX = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "'-'", parent);
		addField(startsWithX);

		BooleanFieldEditor startsWithData = new BooleanFieldEditor(
				TernCoreConstants.TRACE_ON_CONSOLE, "'_'", parent);
		addField(startsWithData);
	}

	protected Group createGroup(Composite parent, int numColumns) {
		Group group = new Group(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		group.setLayout(layout);

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		group.setLayoutData(data);

		return group;
	}
}
