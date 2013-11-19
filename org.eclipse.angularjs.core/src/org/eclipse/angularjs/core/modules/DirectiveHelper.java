package org.eclipse.angularjs.core.modules;

import java.util.ArrayList;
import java.util.List;

public class DirectiveHelper {

	public static final List<String> STARTS_WITH;
	public static final List<Character> DELIMITERS;

	static {
		STARTS_WITH = new ArrayList<String>();
		STARTS_WITH.add("");
		STARTS_WITH.add("x-");
		STARTS_WITH.add("data-");

		DELIMITERS = new ArrayList<Character>();
		DELIMITERS.add(':');
		DELIMITERS.add('-');
		DELIMITERS.add('_');
	}

	/**
	 * 
	 * Angular normalizes an element's tag and attribute name to determine which
	 * elements match which directives. We typically refer to directives by
	 * their case-sensitive camelCase normalized name (e.g. ngModel). However,
	 * since HTML is case-insensitive, we refer to directives in the DOM by
	 * lower-case forms, typically using dash-delimited attributes on DOM
	 * elements (e.g. ng-model).
	 * 
	 * The normalization process is as follows:
	 * 
	 * <ol>
	 * <li>Strip x- and data- from the front of the element/attributes.</li>
	 * <li>Convert the :, -, or _-delimited name to camelCase.</li>
	 * </ol>
	 * 
	 * See http://docs.angularjs.org/guide/directive
	 * 
	 * @return
	 */
	public static String normalize(String name) {
		if (name == null) {
			return null;
		}

		// 1) Strip x- and data- from the front of the element/attributes
		int startIndex = 0;
		char[] chs = name.toCharArray();
		int length = chs.length;
		if (length >= 2) {
			if (chs[0] == 'x' && chs[1] == '-') {
				// starts with x-
				startIndex = 2;
			} else {
				if (length >= 5) {
					// starts with data-
					if (chs[0] == 'd' && chs[1] == 'a' && chs[2] == 't'
							&& chs[3] == 'a' && chs[4] == '-') {
						startIndex = 5;
					}
				}
			}
		}

		// 2) Convert the :, -, or _-delimited name to camelCase.
		StringBuilder normalizedName = new StringBuilder();
		boolean delimiterFound = false;
		boolean forceToUpper = false;
		char c = 0;
		for (int i = startIndex; i < chs.length; i++) {
			c = chs[i];
			if (delimiterFound) {
				if (forceToUpper) {
					normalizedName.append(Character.toUpperCase(c));
				} else {
					normalizedName.append(c);
				}
				forceToUpper = false;
			} else {
				if (DELIMITERS.contains(c)) {
					delimiterFound = true;
					forceToUpper = true;
				} else {
					normalizedName.append(c);
				}
			}
		}
		return normalizedName.toString();
	}

}
