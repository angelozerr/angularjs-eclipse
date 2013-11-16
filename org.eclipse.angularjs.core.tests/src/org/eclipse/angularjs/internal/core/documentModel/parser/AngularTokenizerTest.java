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

}
