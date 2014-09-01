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

import org.eclipse.angularjs.internal.ui.style.IStyleConstantsForAngular;
import org.eclipse.jface.text.Position;

/**
 * Angular expression semantic highlighting used to highlight content of
 * expression. Example :
 * 
 * <p>
 * <span>{{remaining()}}
 * </p>
 * 
 * this class highlight "remaining()"
 *
 */
public class AngularExpressionSemanticHighlighting extends
		AbstractAngularExpressionSemanticHighlighting {

	@Override
	public String getStyleStringKey() {
		return IStyleConstantsForAngular.ANGULAR_EXPRESSION;
	}

	@Override
	protected int fillPosition(List<Position> positions,
			String startExpression, String endExpression, String regionText,
			int startIndex, int startOffset) {
		int endIndex = regionText.indexOf(endExpression, startIndex
				+ startExpression.length());
		int length = (endIndex != -1 ? endIndex : regionText.length())
				- startIndex - endExpression.length();
		positions.add(new Position(startOffset + startIndex
				+ startExpression.length(), length));
		return endIndex;
	}

}
