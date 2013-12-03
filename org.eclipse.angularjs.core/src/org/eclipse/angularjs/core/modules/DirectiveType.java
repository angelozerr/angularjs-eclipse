package org.eclipse.angularjs.core.modules;

import org.eclipse.angularjs.core.utils.StringUtils;

public enum DirectiveType {

	module, controller;

	public static DirectiveType get(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		DirectiveType type = null;
		for (int i = 0; i < values().length; i++) {
			type = values()[i];
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}

}
