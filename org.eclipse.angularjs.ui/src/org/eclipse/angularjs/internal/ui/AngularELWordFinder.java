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
package org.eclipse.angularjs.internal.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * Angular expression word finder.
 * 
 */
public class AngularELWordFinder {

	public static IRegion findWord(IDocument document, int offset,
			String startSymbol, String endSymbol) {

		int start = -2;
		int end = -1;
		char[] ss = startSymbol.toCharArray();
		char[] es = endSymbol.toCharArray();

		try {
			int pos = offset;
			char c;

			while (pos >= 0) {
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)
						|| isMatchStartSymbol(ss, c, pos, document)) {
					break;
				}
				--pos;
			}
			start = pos;

			pos = offset;
			int length = document.getLength();

			while (pos < length) {
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)
						|| isMatchEndSymbol(es, c, pos, document))
					break;
				++pos;
			}
			end = pos;

		} catch (BadLocationException x) {
		}

		if (start >= -1 && end > -1) {
			if (start == offset && end == offset)
				return new Region(offset, 0);
			else if (start == offset)
				return new Region(start, end - start);
			else
				return new Region(start + 1, end - start - 1);
		}

		return null;
	}

	private static boolean isMatchStartSymbol(char[] startSymbol, char c,
			int pos, IDocument document) throws BadLocationException {
		if (c != startSymbol[startSymbol.length - 1]) {
			return false;
		}
		if (pos >= startSymbol.length) {
			pos--;
			for (int i = startSymbol.length - 2; i >= 0; i--) {
				if (startSymbol[i] != document.getChar(pos--)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean isMatchEndSymbol(char[] endSymbol, char c, int pos,
			IDocument document) throws BadLocationException {
		if (c != endSymbol[0]) {
			return false;
		}
		int length = document.getLength();
		if (pos + endSymbol.length <= length) {
			pos++;
			for (int i = 1; i < endSymbol.length; i++) {
				if (endSymbol[i] != document.getChar(pos++)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}