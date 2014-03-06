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

import java.util.List;

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import tern.angular.modules.DirectiveHelper;
import tern.angular.modules.IDirectiveSyntax;

/**
 * Angular Directive page for project properties.
 * 
 */
public class DirectivesPropertyPage extends
		AbstractAngularFieldEditorPropertyPage implements IDirectiveSyntax {

	public static final String PROP_ID = "org.eclipse.angularjs.internal.ui.properties.DirectivesPropertyPage";
	private Text directiveName;
	private StyledText directives;
	private BooleanFieldEditor useOriginalName;
	private BooleanFieldEditor startsWithNothing;
	private BooleanFieldEditor startsWithX;
	private BooleanFieldEditor startsWithData;
	private BooleanFieldEditor colonDelimiter;
	private BooleanFieldEditor minusDelimiter;
	private BooleanFieldEditor underscoreDelimiter;

	public DirectivesPropertyPage() {
		super(GRID);
		setDescription(AngularUIMessages.DirectivesPropertyPage_desc);
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_DIRECTIVE));
	}

	@Override
	protected void createFieldEditors() {

		Composite parent = super.getFieldEditorParent();

		// use original name?
		useOriginalName = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_USE_ORIGINAL_NAME,
				AngularUIMessages.DirectivesPropertyPage_useOriginalName_label,
				parent);
		addSeparator(parent);

		// starts with panel
		createStartsWithPanel(parent);
		addSeparator(parent);

		// delimiters panel
		createDelimitersPanel(parent);
		addSeparator(parent);

		// test panel
		createTestPanel(parent);
	}

	@Override
	protected void initialize() {
		super.initialize();

		// Update test result
		updateTestResult();
	}

	/**
	 * Create starts with panel.
	 * 
	 * @param parent
	 */
	public void createStartsWithPanel(Composite parent) {

		Label startsWithLabel = new Label(parent, SWT.NONE);
		startsWithLabel
				.setText(AngularUIMessages.DirectivesPropertyPage_startsWithLabel_text);

		startsWithNothing = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_STARTS_WITH_NOTHING,
				AngularUIMessages.DirectivesPropertyPage_startsWithNothing_label,
				parent);

		startsWithX = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_STARTS_WITH_X,
				AngularUIMessages.DirectivesPropertyPage_startsWithX_label,
				parent);

		startsWithData = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_STARTS_WITH_DATA,
				AngularUIMessages.DirectivesPropertyPage_startsWithData_label,
				parent);
	}

	/**
	 * Create delimiters panel.
	 * 
	 * @param parent
	 */
	public void createDelimitersPanel(Composite parent) {

		Label delimitersLabel = new Label(parent, SWT.NONE);
		delimitersLabel
				.setText(AngularUIMessages.DirectivesPropertyPage_delimiterLabel_text);

		colonDelimiter = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_COLON_DELIMITER,
				AngularUIMessages.DirectivesPropertyPage_colonDelimiter_label,
				parent);

		minusDelimiter = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_MINUS_DELIMITER,
				AngularUIMessages.DirectivesPropertyPage_minusDelimiter_label,
				parent);

		underscoreDelimiter = createBooleanFieldEditor(
				AngularCoreConstants.DIRECTIVE_UNDERSCORE_DELIMITER,
				AngularUIMessages.DirectivesPropertyPage_underscoreDelimiter_label,
				parent);
	}

	/**
	 * Create test panel.
	 * 
	 * @param parent
	 */
	private void createTestPanel(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		panel.setLayout(new GridLayout(3, false));

		Label directiveTestLabel = new Label(panel, SWT.NONE);
		directiveTestLabel
				.setText(AngularUIMessages.DirectivesPropertyPage_directiveTestLabel_text);

		directiveName = new Text(panel, SWT.BORDER);
		directiveName.setText("ngBind");
		directiveName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		directiveName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				updateTestResult();
			}
		});

		directives = new StyledText(parent, SWT.BORDER | SWT.READ_ONLY
				| SWT.V_SCROLL);
		directives.setLayoutData(new GridData(GridData.FILL_BOTH));
		directives.setText("");
	}

	/**
	 * Add separator.
	 * 
	 * @param parent
	 */
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * Create boolean field editor.
	 * 
	 * @param name
	 * @param label
	 * @param parent
	 * @return
	 */
	private BooleanFieldEditor createBooleanFieldEditor(String name,
			String label, Composite parent) {
		BooleanFieldEditor fieldEditor = new BooleanFieldEditor(name, label,
				parent) {
			@Override
			protected void valueChanged(boolean oldValue, boolean newValue) {
				super.valueChanged(oldValue, newValue);
				updateTestResult();
			}
		};
		addField(fieldEditor);
		return fieldEditor;
	}

	/**
	 * Update test result.
	 */
	private void updateTestResult() {
		directives.setText("");
		StringBuilder s = new StringBuilder();
		List<String> names = DirectiveHelper.getDirectiveNames(directiveName
				.getText());
		String name = null;
		for (int i = 0; i < names.size(); i++) {
			if (DirectiveHelper.isSupport(this, i)) {
				name = names.get(i);
				if (s.length() > 0) {
					s.append("\n");
				}
				s.append(name);
			}
		}
		directives.setText(s.toString());
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		updateTestResult();
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		IProject project = null;
		try {
			project = getAngularProject().getProject();
		} catch (CoreException e) {
		}
		IScopeContext projectScope = new ProjectScope(project);
		return new ScopedPreferenceStore(projectScope,
				AngularCorePlugin.PLUGIN_ID);
	}

	@Override
	public boolean isUseOriginalName() {
		return useOriginalName.getBooleanValue();
	}

	@Override
	public boolean isStartsWithNothing() {
		return startsWithNothing.getBooleanValue();
	}

	@Override
	public boolean isStartsWithX() {
		return startsWithX.getBooleanValue();
	}

	@Override
	public boolean isStartsWithData() {
		return startsWithData.getBooleanValue();
	}

	@Override
	public boolean isColonDelimiter() {
		return colonDelimiter.getBooleanValue();
	}

	@Override
	public boolean isMinusDelimiter() {
		return minusDelimiter.getBooleanValue();
	}

	@Override
	public boolean isUnderscoreDelimiter() {
		return underscoreDelimiter.getBooleanValue();
	}
}
