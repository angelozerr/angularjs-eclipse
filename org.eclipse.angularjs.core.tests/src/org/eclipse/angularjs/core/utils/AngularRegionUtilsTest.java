package org.eclipse.angularjs.core.utils;

import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.junit.Test;

public class AngularRegionUtilsTest {

	@Test
	public void test() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{todo.text + to}}", 588, 604,
				"{{", "}}");
	}
}
