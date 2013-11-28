package org.eclipse.angularjs.internal.ui.preferences;

import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.angularjs.internal.ui.style.IStyleConstantsForAngular;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.ui.internal.preferences.HTMLUIPreferenceNames;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

public class PreferenceConstants {

	public static IPreferenceStore getPreferenceStore() {
		return AngularUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Initializes the given preference store with the default values.
	 */
	public static void initializeDefaultValues() {
		IPreferenceStore store = getPreferenceStore();
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager()
				.getCurrentTheme().getColorRegistry();

		// HTML Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String JUSTBOLD = " | null | true | false"; //$NON-NLS-1$

		// SyntaxColoringPage
		String styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER, 0, 0, 128)
				+ JUSTBOLD;
		store.setDefault(IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER,
				styleValue);

		styleValue = " null|"
				+ ColorHelper.findRGBString(registry,
						IStyleConstantsForAngular.ANGULAR_EXPRESSION, 232, 235,
						255) + " | false | false";
		store.setDefault(IStyleConstantsForAngular.ANGULAR_EXPRESSION,
				styleValue);

		styleValue = ColorHelper.findRGBString(registry,
				IStyleConstantsForAngular.ANGULAR_DIRECTIVE_NAME, 0, 0, 128)
				+ JUSTBOLD;
		store.setDefault(IStyleConstantsForAngular.ANGULAR_DIRECTIVE_NAME,
				styleValue);

		// Defaults for the Typing preference page
		store.setDefault(AngularUIPreferenceNames.TYPING_COMPLETE_END_EL, true);

	}
}
