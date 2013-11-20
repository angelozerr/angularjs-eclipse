/*******************************************************************************
 * Copyright (c) 2011 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.core.utils;

/**
 * 
 * Utilities for {@link String}.
 * 
 */
public class StringUtils {

	public static final String[] EMPTY_ARRAY = new String[0];

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean hasLength(CharSequence str) {
		return str != null && str.length() > 0;
	}

	public static boolean hasLength(String str) {
		return hasLength(((CharSequence) (str)));
	}

	public static boolean hasText(CharSequence str) {
		if (!hasLength(str))
			return false;
		int strLen = str.length();
		for (int i = 0; i < strLen; i++)
			if (!Character.isWhitespace(str.charAt(i)))
				return true;

		return false;
	}

	public static boolean hasText(String str) {
		return hasText(((CharSequence) (str)));
	}

	public static boolean isQuoted(String string) {
		if (string == null || string.length() < 2)
			return false;
		int lastIndex = string.length() - 1;
		char firstChar = string.charAt(0);
		char lastChar = string.charAt(lastIndex);
		return firstChar == '\'' && lastChar == '\'' || firstChar == '"'
				&& lastChar == '"';
	}

	public static String normalizeSpace(String s) {
		if (s == null) {
			return null;
		}
		int len = s.length();
		if (len < 1) {
			return "";
		}
		int st = 0;
		int off = 0;      /* avoid getfield opcode */
		char[] val = s.toCharArray();    /* avoid getfield opcode */
		int count = s.length();
		
		boolean parse = true;
		char c;
		while (parse) {
			c = val[off + st];
			parse = isParse(len, st, c);
			if (parse) {
				st++;
			}
		}
		parse = true;
		while ((st < len) && (val[off + len - 1] <= ' ')) {
			c = val[off + len - 1];
			parse = isParse(len, st, c);
			if (parse) {
				len--;
			}
		}
		return ((st > 0) || (len < count)) ? s.substring(st, len) : s;
	}

	private static boolean isParse(int len, int st, char c) {
		return (st < len) && (c == ' ' || c == '\r' || c == '\n' || c == '\t');
	}
}
