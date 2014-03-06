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
