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
package org.eclipse.angularjs.internal.ui.utils;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class DOMUIUtils {

	/**
	 * Returns the closest IStructuredDocumentRegion for the offest and viewer.
	 * 
	 * @param viewer
	 * @param documentOffset
	 * @return the closest IStructuredDocumentRegion for the offest and viewer.
	 */
	public static IStructuredDocumentRegion getStructuredDocumentRegion(
			ITextViewer viewer, int documentOffset) {
		IStructuredDocumentRegion sdRegion = null;
		if (viewer == null || viewer.getDocument() == null)
			return null;

		int lastOffset = documentOffset;
		IStructuredDocument doc = (IStructuredDocument) viewer.getDocument();
		sdRegion = doc.getRegionAtCharacterOffset(documentOffset);
		while (sdRegion == null && lastOffset >= 0) {
			lastOffset--;
			sdRegion = doc.getRegionAtCharacterOffset(lastOffset);
		}
		return sdRegion;
	}
}
