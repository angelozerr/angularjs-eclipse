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
package org.eclipse.angularjs.internal.core.documentModel.parser;

import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.parser.regions.XMLParserRegionFactory;

/**
 * 
 * This region factory is very specific to the parser output, and the specific
 * implementation classes for various regions.
 */
public class AngularParserRegionFactory extends XMLParserRegionFactory {
	public AngularParserRegionFactory() {
		super();
	}

	public ITextRegion createToken(String context, int start, int textLength,
			int length, String lang, String surroundingTag) {
		ITextRegion newRegion = null;
		if (context == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
			newRegion = new ForeignRegion(context, start, textLength, length);
		} else
			newRegion = super.createToken(context, start, textLength, length,
					lang, surroundingTag);
		return newRegion;
	}
}
