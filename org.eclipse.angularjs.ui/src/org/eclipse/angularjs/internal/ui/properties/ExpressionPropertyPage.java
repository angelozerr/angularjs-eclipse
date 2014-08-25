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

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.internal.ui.AngularUIMessages;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Expression property page.
 *
 */
public class ExpressionPropertyPage extends
		AbstractAngularFieldEditorPropertyPage {

	@Override
	protected void createFieldEditors() {
		Composite parent = super.getFieldEditorParent();

		// start symbol
		StringFieldEditor startSymbolField = new StringFieldEditor(
				AngularCoreConstants.EXPRESSION_START_SYMBOL,
				AngularUIMessages.ExpressionPropertyPage_startSybol_label,
				parent);
		addField(startSymbolField);

		// end symbol
		StringFieldEditor endSymbolField = new StringFieldEditor(
				AngularCoreConstants.EXPRESSION_END_SYMBOL,
				AngularUIMessages.ExpressionPropertyPage_endSybol_label, parent);
		addField(endSymbolField);
	}

}
