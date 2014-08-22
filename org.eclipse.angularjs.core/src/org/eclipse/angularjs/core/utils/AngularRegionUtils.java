package org.eclipse.angularjs.core.utils;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class AngularRegionUtils {

	public static AngularELRegion getAngularELRegion(
			IStructuredDocumentRegion documentRegion, int documentPosition) {
		String regionType = documentRegion.getType();
		String regionText = documentRegion.getText();
		int regionStartOffset = documentRegion.getStartOffset();
		return getAngularELRegion(regionType, regionText, regionStartOffset,
				documentPosition);
	}

	public static AngularELRegion getAngularELRegion(String regionType,
			String regionText, int regionStartOffset, int documentPosition) {
		int startOffset = documentPosition - regionStartOffset;
		if (startOffset < 0) {
			return null;
		}
		String startSymbol = AngularProject.START_ANGULAR_EXPRESSION_TOKEN;
		String endSymbol = AngularProject.END_ANGULAR_EXPRESSION_TOKEN;
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
			// case for angular expression
			int expressionOffset = startOffset - startSymbol.length();
			String expression = HyperlinkUtils.getExpressionContent(regionText);
			return new AngularELRegion(expression, expressionOffset);
		} else if (regionType == DOMRegionContext.XML_CONTENT
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
