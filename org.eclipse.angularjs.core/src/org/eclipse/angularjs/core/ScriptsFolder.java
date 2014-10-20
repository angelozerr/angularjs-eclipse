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
package org.eclipse.angularjs.core;

import tern.scriptpath.ITernScriptPath;

public class ScriptsFolder extends BaseModel {

	public ScriptsFolder(ITernScriptPath scriptPath) {
		super("scripts", Type.ScriptsFolder, scriptPath);
	}

}
