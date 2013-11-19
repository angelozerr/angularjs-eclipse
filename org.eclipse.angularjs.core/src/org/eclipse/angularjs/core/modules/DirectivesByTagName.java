package org.eclipse.angularjs.core.modules;

import java.util.Collection;
import java.util.HashMap;

class DirectivesByTagName extends HashMap<String, Directive> {

	public void addDirective(Directive directive) {
		Collection<String> names = directive.getNames();
		for (String name : names) {
			super.put(name, directive);
		}
	}
	
	

}
