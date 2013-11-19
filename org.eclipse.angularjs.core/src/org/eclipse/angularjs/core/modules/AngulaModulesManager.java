package org.eclipse.angularjs.core.modules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

public class AngulaModulesManager {

	private static final AngulaModulesManager INSTANCE = new AngulaModulesManager();

	public static AngulaModulesManager getInstance() {
		return INSTANCE;
	}

	private final Map<String, Module> modules;

	private AngulaModulesManager() {
		this.modules = new HashMap<String, Module>();
		try {
			loadModule(AngulaModulesManager.class.getResourceAsStream("ng.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadModule(InputStream in) throws IOException, SAXException {
		addModule(new SAXModuleHandler().load(in));
	}

	public void addModule(Module module) {
		modules.put(module.getName(), module);
	}

	public Collection<Module> getModules() {
		return modules.values();
	}

	public void collectDirectives(String tagName, String directiveName,
			boolean fullMatch, IDirectiveCollector collector) {
		Collection<Module> modules = getModules();
		for (Module module : modules) {
			module.collectDirectives(tagName, directiveName, fullMatch,
					collector);
		}
	}
}
