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

import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.junit.Assert;
import org.junit.Test;

public class AngularRegionUtilsTest {

	@Test
	public void offsetBeforeStartEL() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{todo.text + to}}", 588, 588,
				"{{", "}}");
		Assert.assertNull(region);
	}

	@Test
	public void offsetAfterStartEL() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{todo.text + to}}", 588, 590,
				"{{", "}}");
		Assert.assertNotNull(region);
		Assert.assertEquals("todo.text + to", region.getExpression());
		Assert.assertEquals(0, region.getExpressionOffset());
	}

	@Test
	public void offsetBeforeEndEL() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{todo.text + to}}", 588, 603,
				"{{", "}}");
		Assert.assertNotNull(region);
		Assert.assertEquals("todo.text + to", region.getExpression());
		Assert.assertEquals(13, region.getExpressionOffset());
	}

	@Test
	public void offsetAfterEndEL() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{todo.text + to}}", 588, 604,
				"{{", "}}");
		Assert.assertNotNull(region);
		Assert.assertEquals("todo.text + to", region.getExpression());
		Assert.assertEquals(14, region.getExpressionOffset());
	}

	@Test
	public void test2() {
		AngularELRegion region = AngularRegionUtils.getAngularELRegion(
				DOMRegionContext.XML_CONTENT, "{{", 588, 604, "{{", "}}");
		Assert.assertNull(region);
	}
}
