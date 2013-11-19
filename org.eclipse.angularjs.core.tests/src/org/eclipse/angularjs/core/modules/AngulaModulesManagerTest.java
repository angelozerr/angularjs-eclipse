package org.eclipse.angularjs.core.modules;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

public class AngulaModulesManagerTest {

	@Test
	public void testName() throws Exception {
		Collection<Module> modules = AngulaModulesManager.getInstance().getModules();
		for (Module module : modules) {
			System.err.println(module);
		}
	}
}
