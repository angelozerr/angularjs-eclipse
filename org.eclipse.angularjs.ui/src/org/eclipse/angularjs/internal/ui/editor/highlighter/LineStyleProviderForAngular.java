package org.eclipse.angularjs.internal.ui.editor.highlighter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.angularjs.internal.ui.preferences.PreferenceConstants;
import org.eclipse.angularjs.internal.ui.style.IStyleConstantsForAngular;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * Coloring mechanism for Freemarker partitions
 */
public class LineStyleProviderForAngular extends AbstractLineStyleProvider
		implements LineStyleProvider {

	private IPreferenceStore fColorPreferences;

	/** Contains region to style mapping */
	protected static final Map<String, String> fColorTypes = new HashMap<String, String>(); // String

	static {
		// Normal text:
		/*
		 * fColorTypes.put(PHPRegionTypes.PHP_STRING,
		 * PreferenceConstants.EDITOR_NORMAL_COLOR);
		 * fColorTypes.put(PHPRegionTypes.PHP_TOKEN,
		 * PreferenceConstants.EDITOR_NORMAL_COLOR);
		 * fColorTypes.put(PHPRegionTypes.PHP_SEMICOLON,
		 * PreferenceConstants.EDITOR_NORMAL_COLOR);
		 * fColorTypes.put(PHPRegionTypes.PHP_OPERATOR,
		 * PreferenceConstants.EDITOR_NORMAL_COLOR);
		 */
	}

	/**
	 * Returns the attribute for simple php regions (open /close) not
	 * PHP_CONTENT regions
	 * 
	 * @param region
	 * @return the text attribute
	 */
	@Override
	protected TextAttribute getAttributeFor(ITextRegion region) {
		TextAttribute result = null;

		if (region != null) {
			final String type = region.getType();
			if (type == AngularRegionContext.ANGULAR_EXPRESSION_OPEN) {
				result = (TextAttribute) getTextAttributes().get(
						IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
			} else if (type == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE) {
				result = (TextAttribute) getTextAttributes().get(
						IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
			}
			if (type == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
				result = (TextAttribute) getTextAttributes().get(
						IStyleConstantsForAngular.ANGULAR_EXPRESSION);
			}
			/*
			 * else if (type == PHPRegionContext.PHP_CLOSE) { result =
			 * getAttributeFor(PHPRegionTypes.PHP_CLOSETAG); } else { result =
			 * getAttributeFor(region.getType()); }
			 */
		}

		// return the defalt attributes if there is not highlight color for the
		// region
		if (result == null) {
			// result = (TextAttribute) getTextAttributes().get(
			// PreferenceConstants.EDITOR_NORMAL_COLOR);
		}
		return result;
	}

	/**
	 * Look up the TextAttribute for the given region context. Might return null
	 * for unusual text.
	 * 
	 * @param type
	 * @return
	 */
	protected TextAttribute getAttributeFor(String type) {
		return (TextAttribute) getTextAttributes().get(fColorTypes.get(type));
	}

	public void setColorPreferences(IPreferenceStore preferenceStore) {
		fColorPreferences = preferenceStore;
	}

	@Override
	public IPreferenceStore getColorPreferences() {
		if (fColorPreferences != null) {
			return fColorPreferences;
		}
		return PreferenceConstants.getPreferenceStore();
	}

	@Override
	protected void loadColors() {
		addTextAttribute(IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
		addTextAttribute(IStyleConstantsForAngular.ANGULAR_EXPRESSION);
	}
}
