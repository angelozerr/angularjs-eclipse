package org.eclipse.angularjs.internal.core.preferences;

import org.eclipse.angularjs.core.AngularCoreConstants;
import org.eclipse.angularjs.core.AngularCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Preferences;

import tern.eclipse.ide.core.preferences.PreferencesSupport;
import tern.utils.StringUtils;

public class AngularCorePreferencesSupport {

	private static final Preferences store = AngularCorePlugin.getDefault()
			.getPluginPreferences();
	private PreferencesSupport preferencesSupport;

	private AngularCorePreferencesSupport() {
		preferencesSupport = new PreferencesSupport(
				AngularCorePlugin.PLUGIN_ID, store);
	}

	private static AngularCorePreferencesSupport instance = null;

	public static AngularCorePreferencesSupport getInstance() {
		if (instance == null) {
			instance = new AngularCorePreferencesSupport();
		}
		return instance;
	}

	public boolean isDirectiveUseOriginalName(IProject project) {
		return getBool(project,
				AngularCoreConstants.DIRECTIVE_USE_ORIGINAL_NAME);
	}

	public boolean isDirectiveStartsWithNothing(IProject project) {
		return getBool(project,
				AngularCoreConstants.DIRECTIVE_STARTS_WITH_NOTHING);
	}

	public boolean isDirectiveStartsWithX(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_STARTS_WITH_X);
	}

	public boolean isDirectiveStartsWithData(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_STARTS_WITH_DATA);
	}

	public boolean isDirectiveColonDelimiter(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_COLON_DELIMITER);
	}

	public boolean isDirectiveMinusDelimiter(IProject project) {
		return getBool(project, AngularCoreConstants.DIRECTIVE_MINUS_DELIMITER);
	}

	public boolean isDirectiveUnderscoreDelimiter(IProject project) {
		return getBool(project,
				AngularCoreConstants.DIRECTIVE_UNDERSCORE_DELIMITER);
	}

	public boolean getBool(IProject project, String key) {
		String result = preferencesSupport.getPreferencesValue(key, null,
				project);
		return StringUtils.asBoolean(result, false);
	}

}
