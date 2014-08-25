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
package org.eclipse.angularjs.ui.style;

import java.util.List;

import org.eclipse.jface.text.Position;

/**
 * Angular expression semantic highlighting used to highlight border of
 * expression. Example :
 * 
 * <p>
 * <span>{{remaining()}}
 * </p>
 * 
 * this class highlight "{{" and "}}".
 *
 */
public class AngularExpressionBorderSemanticHighlighting extends
		AbstractAngularExpressionSemanticHighlighting {

	@Override
	protected int fillPosition(List<Position> positions,
			String startExpression, String endExpression, String regionText,
			int startIndex, int startOffset) {
		int endIndex = regionText.indexOf(endExpression, startIndex
				+ startExpression.length());
		// add position of {{
		positions.add(new Position(startOffset + startIndex, startExpression
				.length()));
		if (endIndex != -1) {
			// add position of }}
			positions.add(new Position(startOffset + endIndex, endExpression
					.length()));
		}
		return endIndex;
	}
}
