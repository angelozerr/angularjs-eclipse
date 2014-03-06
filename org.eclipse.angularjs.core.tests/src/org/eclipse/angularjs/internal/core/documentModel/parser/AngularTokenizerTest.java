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

import org.junit.Test;

public class AngularTokenizerTest {

	@Test
	public void testName() throws Exception {
		AngularTokenizer toker = new AngularTokenizer();
		// XMLTokenizer toker = new XMLTokenizer();
		//toker.setCaseSensitiveBlocking(false);
		toker.reset(new java.io.StringReader("<p>Nothing here {{'yet' + '!'}}</p>"));
		//toker.reset(new java.io.StringReader("{{ a }}"));
		// toker.beginBlockMarkerScan("script", DOMRegionContext.BLOCK_TEXT);
		System.err.println(toker.getRegions());
	}

	@Test
	public void testAttrValue() throws Exception {
		AngularTokenizer toker = new AngularTokenizer();
		toker.reset(new java.io.StringReader("<p a=\"att-{{a}}\"></p>"));
		System.err.println(toker.getRegions());
	}
}
