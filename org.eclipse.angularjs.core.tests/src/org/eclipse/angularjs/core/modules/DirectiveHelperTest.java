package org.eclipse.angularjs.core.modules;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see http://docs.angularjs.org/guide/directive
 * 
 */
public class DirectiveHelperTest {

	@Test
	public void testNormalize0() throws Exception {
		String result = DirectiveHelper.normalize("ngBind");
		Assert.assertEquals("ngBind", result);
	}
	
	@Test
	public void testNormalize1() throws Exception {
		String result = DirectiveHelper.normalize("ng-bind");
		Assert.assertEquals("ngBind", result);
	}

	@Test
	public void testNormalize2() throws Exception {
		String result = DirectiveHelper.normalize("ng:bind");
		Assert.assertEquals("ngBind", result);
	}

	@Test
	public void testNormalize3() throws Exception {
		String result = DirectiveHelper.normalize("ng_bind");
		Assert.assertEquals("ngBind", result);
	}

	@Test
	public void testNormalize4() throws Exception {
		String result = DirectiveHelper.normalize("data-ng-bind");
		Assert.assertEquals("ngBind", result);
	}

	@Test
	public void testNormalize5() throws Exception {
		String result = DirectiveHelper.normalize("x-ng-bind");
		Assert.assertEquals("ngBind", result);
	}

	@Test
	public void testNormalize6() throws Exception {
		String result = DirectiveHelper.normalize("name");
		Assert.assertEquals("name", result);
	}

	@Test
	public void testNormalize7() throws Exception {
		String result = DirectiveHelper.normalize("ngname");
		Assert.assertEquals("ngname", result);
	}

	@Test
	public void testNormalize8() throws Exception {
		String result = DirectiveHelper.normalize("a");
		Assert.assertEquals("a", result);
	}
}
