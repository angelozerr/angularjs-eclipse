package org.eclipse.angularjs.internal.ui.editor.highlighter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.angularjs.internal.ui.preferences.PreferenceConstants;
import org.eclipse.angularjs.internal.ui.style.IStyleConstantsForAngular;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Coloring mechanism for Freemarker partitions
 */
public class LineStyleProviderForAngular extends LineStyleProviderForHTML
		implements LineStyleProvider {

	private IPreferenceStore fColorPreferences;

	private IDOMModel model;

	public LineStyleProviderForAngular(IDOMModel model) {
		this.model = model;
	}

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
	public TextAttribute getAttributeFor(ITextRegion region) {
		TextAttribute result = null;

		if (region != null) {
			final String type = region.getType();
			if (type == AngularRegionContext.ANGULAR_EXPRESSION_OPEN) {
				result = (TextAttribute) getTextAttributes().get(
						IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
			} else if (type == AngularRegionContext.ANGULAR_EXPRESSION_CLOSE) {
				result = (TextAttribute) getTextAttributes().get(
						IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
			} else if (type == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
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
		if (result != null) {
			return result;
			// result = (TextAttribute) getTextAttributes().get(
			// PreferenceConstants.EDITOR_NORMAL_COLOR);
		}
		return super.getAttributeFor(region);
	}

	@Override
	protected TextAttribute getAttributeFor(ITextRegionCollection collection,
			ITextRegion region) {
		if (region != null) {
			final String type = region.getType();
			if ((type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				IDOMNode node = DOMUtils.getNodeByOffset(model,
						collection.getStart());
				if (node != null) {
					IDOMAttr attr = DOMUtils.getAttrByOffset(node,
							collection.getStart() + region.getStart());
					if (DOMUtils.isAngularDirective(attr)) {
						return (TextAttribute) getTextAttributes()
								.get(IStyleConstantsForAngular.ANGULAR_DIRECTIVE_NAME);
					}
				}
			}
		}
		return super.getAttributeFor(collection, region);
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

	// @Override
	public IPreferenceStore getAngularColorPreferences() {
		if (fColorPreferences != null) {
			return fColorPreferences;
		}
		return PreferenceConstants.getPreferenceStore();
	}

	@Override
	protected void loadColors() {
		addTextAttribute(IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER);
		addTextAttribute(IStyleConstantsForAngular.ANGULAR_EXPRESSION);
		addTextAttribute(IStyleConstantsForAngular.ANGULAR_DIRECTIVE_NAME);
		super.loadColors();
	}

	/**
	 * Looks up the colorKey in the preference store and adds the style
	 * information to list of TextAttributes
	 * 
	 * @param colorKey
	 */
	@Override
	protected void addTextAttribute(String colorKey) {
		IPreferenceStore colorPreferences = null;
		if (IStyleConstantsForAngular.ANGULAR_EXPRESSION_BORDER
				.equals(colorKey)
				|| IStyleConstantsForAngular.ANGULAR_EXPRESSION
						.equals(colorKey)
				|| IStyleConstantsForAngular.ANGULAR_DIRECTIVE_NAME
						.equals(colorKey)) {
			colorPreferences = getAngularColorPreferences();

		} else {
			colorPreferences = super.getColorPreferences();
		}
		if (colorPreferences != null) {
			String prefString = colorPreferences.getString(colorKey);
			String[] stylePrefs = ColorHelper
					.unpackStylePreferences(prefString);
			if (stylePrefs != null) {
				RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
				RGB background = ColorHelper.toRGB(stylePrefs[1]);
				boolean bold = Boolean.valueOf(stylePrefs[2]).booleanValue();
				boolean italic = Boolean.valueOf(stylePrefs[3]).booleanValue();
				boolean strikethrough = Boolean.valueOf(stylePrefs[4])
						.booleanValue();
				boolean underline = Boolean.valueOf(stylePrefs[5])
						.booleanValue();
				int style = SWT.NORMAL;
				if (bold) {
					style = style | SWT.BOLD;
				}
				if (italic) {
					style = style | SWT.ITALIC;
				}
				if (strikethrough) {
					style = style | TextAttribute.STRIKETHROUGH;
				}
				if (underline) {
					style = style | TextAttribute.UNDERLINE;
				}

				TextAttribute createTextAttribute = createTextAttribute(
						foreground, background, style);
				getTextAttributes().put(colorKey, createTextAttribute);
			}
		}
	}

	@Override
	protected void registerPreferenceManager() {
		super.registerPreferenceManager();
		IPreferenceStore pref = getAngularColorPreferences();
		if (pref != null) {
			pref.addPropertyChangeListener(fPreferenceListener);
		}
	}

	@Override
	protected void unRegisterPreferenceManager() {
		super.unRegisterPreferenceManager();
		IPreferenceStore pref = getAngularColorPreferences();
		if (pref != null) {
			pref.removePropertyChangeListener(fPreferenceListener);
		}
	}
}
