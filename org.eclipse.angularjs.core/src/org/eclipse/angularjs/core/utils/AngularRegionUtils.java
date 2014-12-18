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
package org.eclipse.angularjs.core.utils;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class AngularRegionUtils {

	public static AngularELRegion getAngularELRegion(
			IStructuredDocumentRegion documentRegion, int documentPosition,
			IProject project) {
		String startSymbol = AngularProject.DEFAULT_START_SYMBOL;
		String endSymbol = AngularProject.DEFAULT_END_SYMBOL;
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			startSymbol = angularProject.getStartSymbol();
			endSymbol = angularProject.getEndSymbol();
		} catch (CoreException e) {
		}
		String regionType = documentRegion.getType();
		String regionText = documentRegion.getText();
		int regionStartOffset = documentRegion.getStartOffset();
		return getAngularELRegion(regionType, regionText, regionStartOffset,
				documentPosition, startSymbol, endSymbol);
	}

	public static AngularELRegion getAngularELRegion(String regionType,
			String regionText, int regionStartOffset, int documentPosition,
			IProject project) {
		String startSymbol = AngularProject.DEFAULT_START_SYMBOL;
		String endSymbol = AngularProject.DEFAULT_END_SYMBOL;
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			startSymbol = angularProject.getStartSymbol();
			endSymbol = angularProject.getEndSymbol();
		} catch (CoreException e) {
		}
		return getAngularELRegion(regionType, regionText, regionStartOffset,
				documentPosition, startSymbol, endSymbol);
	}

	public static AngularELRegion getAngularELRegion(String regionType,
			String regionText, int regionStartOffset, int documentPosition,
			String startSymbol, String endSymbol) {
		int startOffset = documentPosition - regionStartOffset;
		if (startOffset < 0 || startOffset > regionText.length()) {
			return null;
		}
		if (regionType == DOMRegionContext.XML_CONTENT
				|| regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			String expression = null;
			String text = regionText.substring(0, startOffset);
			int startExprIndex = text.lastIndexOf(startSymbol);
			if (startExprIndex != -1) {
				int endExprIndex = regionText.indexOf(endSymbol, startOffset);
				if (startExprIndex < endExprIndex) {
					// completion (for JSP) is done inside angular
					// expression {{
					expression = regionText.substring(startExprIndex
							+ startSymbol.length(), endExprIndex);
				}
			}
			if (expression != null) {
				int expressionOffset = startOffset - startExprIndex
						- startSymbol.length();// + 1;
				return new AngularELRegion(expression, expressionOffset);
			}
		}
		return null;
	}

}
