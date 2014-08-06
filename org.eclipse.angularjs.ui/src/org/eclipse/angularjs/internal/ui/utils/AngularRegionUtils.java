package org.eclipse.angularjs.internal.ui.utils;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class AngularRegionUtils {

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
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
			// case for angular expression
			int expressionOffset = startOffset
					- AngularProject.START_ANGULAR_EXPRESSION_TOKEN.length();
			String expression = HyperlinkUtils.getExpressionContent(regionText);
			return new AngularELRegion(expression, expressionOffset);
		} else if (regionType == DOMRegionContext.XML_CONTENT
				|| regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			String expression = null;
			String text = regionText.substring(0, startOffset);
			int startExprIndex = text
					.lastIndexOf(AngularProject.START_ANGULAR_EXPRESSION_TOKEN);
			if (startExprIndex != -1) {
				int endExprIndex = regionText.indexOf(
						AngularProject.END_ANGULAR_EXPRESSION_TOKEN,
						startOffset);
				if (startExprIndex < endExprIndex) {
					// completion (for JSP) is done inside angular
					// expression {{
					expression = regionText.substring(startExprIndex + 2,
							endExprIndex);
				}
			}
			if (expression != null) {
				int expressionOffset = startOffset
						- startExprIndex
						- AngularProject.START_ANGULAR_EXPRESSION_TOKEN
								.length() + 1;
				return new AngularELRegion(expression, expressionOffset);
			}
		}
		return null;
	}
}
