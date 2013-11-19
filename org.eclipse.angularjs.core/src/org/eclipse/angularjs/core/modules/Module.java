package org.eclipse.angularjs.core.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Module {

	private final String name;

	private final DirectivesByTagName directivesForAny;
	private final Map<String, DirectivesByTagName> directivesByTagName;

	public Module(String name) {
		this.name = name;
		this.directivesByTagName = new HashMap<String, DirectivesByTagName>();
		this.directivesForAny = new DirectivesByTagName();
	}

	public String getName() {
		return name;
	}

	void addDirective(Directive directive) {
		Collection<String> tagNames = directive.getTagNames();
		if (tagNames.size() > 0) {
			for (String tagName : tagNames) {
				addDirective(tagName, directive);
			}
		} else {
			addDirective(null, directive);
		}
	}

	private void addDirective(String tagName, Directive directive) {
		DirectivesByTagName directives = getDirectivesByTagName(tagName, true);
		directives.addDirective(directive);
	}

	private DirectivesByTagName getDirectivesByTagName(String tagName,
			boolean createIfNotExists) {
		if (tagName == null) {
			return directivesForAny;
		}
		DirectivesByTagName result = directivesByTagName.get(tagName);
		if (result == null && createIfNotExists) {
			result = new DirectivesByTagName();
			directivesByTagName.put(tagName, result);
		}
		return result;
	}

	public Directive getDirective(String tagName, String name) {
		DirectivesByTagName result = getDirectivesByTagName(tagName, false);
		if (result != null) {
			result.get(name);
		}
		return null;
	}

	public void collectDirectives(String tagName, String directiveName,
			boolean fullMatch, IDirectiveCollector collector) {
		if (fullMatch) {
			Directive directive = getDirective(tagName, directiveName);
			if (directive != null) {
				collector.add(directive, directiveName);
			}
		} else {
			DirectivesByTagName container = getDirectivesByTagName(tagName,
					false);
			if (container == null) {
				container = getDirectivesByTagName(null, false);
			}
			Set<String> names = container.keySet();
			for (String name : names) {
				if (name.startsWith(directiveName)) {
					collector.add(container.get(name), name);
				}
			}
			/*
			 * for (Directive directive : directives.values()) { String
			 * nameWhichMatch = directive.match(directiveName); if
			 * (nameWhichMatch != null) { collector.add(directive,
			 * nameWhichMatch); } }
			 */
		}
	}
}
