package org.eclipse.angularjs.internal.ui.preferences;

import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

public class PreferenceConstants {

	/**
	 * A named preference that holds the color for the numbers
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String EDITOR_NUMBER_COLOR = "editorColorNumber"; //$NON-NLS-1$

	/**
	 * A named preference that holds the default color for the numbers
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String EDITOR_NUMBER_DEFAULT_COLOR = ColorHelper
			.getColorString(126, 0, 0);

	
	public static IPreferenceStore getPreferenceStore() {
		return AngularUIPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Initializes the given preference store with the default values.
	 */
	public static void initializeDefaultValues() {
		IPreferenceStore store = getPreferenceStore();


		// SyntaxColoringPage
		store.setDefault(EDITOR_NUMBER_COLOR, EDITOR_NUMBER_DEFAULT_COLOR);

	}
}
