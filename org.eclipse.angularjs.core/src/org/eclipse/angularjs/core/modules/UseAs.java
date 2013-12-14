package org.eclipse.angularjs.core.modules;

import tern.utils.StringUtils;

public enum UseAs {

	attr, clazz;

	public static UseAs get(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		UseAs useAs = null;
		for (int i = 0; i < values().length; i++) {
			useAs = values()[i];
			if (useAs.name().equalsIgnoreCase(value)) {
				return useAs;
			}
		}
		return useAs;
	}
}
