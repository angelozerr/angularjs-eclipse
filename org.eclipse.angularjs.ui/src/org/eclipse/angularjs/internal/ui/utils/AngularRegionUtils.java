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
		int startOffset = documentPosition - documentRegion.getStartOffset();
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
			// case for angular expression
			int expressionOffset = startOffset
					- AngularProject.START_ANGULAR_EXPRESSION_TOKEN.length();
			String expression = HyperlinkUtils
					.getExpressionContent(documentRegion.getText());
			return new AngularELRegion(expression, expressionOffset);
		} else if (regionType == DOMRegionContext.XML_CONTENT) {
			String expression = null;
			String text = documentRegion.getText().substring(0, startOffset);
			int startExprIndex = text
					.indexOf(AngularProject.START_ANGULAR_EXPRESSION_TOKEN);
			if (startExprIndex != -1) {
				int endExprIndex = documentRegion.getText().indexOf(
						AngularProject.END_ANGULAR_EXPRESSION_TOKEN,
						startOffset);
				if (startExprIndex < endExprIndex) {
					// completion (for JSP) is done inside angular
					// expression {{
					expression = documentRegion.getText().substring(
							startExprIndex + 2, endExprIndex);
				}
			}
			if (expression != null) {
				int expressionOffset = startOffset
						- AngularProject.START_ANGULAR_EXPRESSION_TOKEN
								.length();
				return new AngularELRegion(expression, expressionOffset);
			}
		}
		return null;
		/*
		 * return new AngularELRegion(expression, expressionOffset);
		 * 
		 * String match = null; int length = documentPosition -
		 * documentRegion.getStartOffset(); if (isXMLContent) { if
		 * (!AngularDOMUtils.isAngularContentType(treeNode)) { // case for JSP
		 * String text = documentRegion.getText().substring(0, length); int
		 * startExprIndex = text
		 * .lastIndexOf(AngularProject.START_ANGULAR_EXPRESSION_TOKEN); if
		 * (startExprIndex != -1) { int endExprIndex = text
		 * .lastIndexOf(AngularProject.END_ANGULAR_EXPRESSION_TOKEN); if
		 * (endExprIndex == -1 || endExprIndex < startExprIndex) { // completion
		 * (for JSP) is done inside angular // expression {{ match =
		 * text.substring(startExprIndex + 2, text.length()); } } } } else { //
		 * case for HTML where regionType is an angular expression //
		 * open/content. if (length > 1) { // here we have {{ match =
		 * documentRegion.getText().substring(2, length); } } return null;
		 */
	}
}
