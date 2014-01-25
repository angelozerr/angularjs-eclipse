package org.eclipse.angularjs.internal.ui.validation;

import tern.server.protocol.type.ITernTypeCollector;

public class ValidationTernTypeCollector implements ITernTypeCollector {

	private boolean exists;

	@Override
	public void setType(String name, String type, String origin) {
		exists = name != null;
	}

	public boolean isExists() {
		return exists;
	}

}
