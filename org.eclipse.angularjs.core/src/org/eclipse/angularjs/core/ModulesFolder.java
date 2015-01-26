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

import java.util.ArrayList;
import java.util.List;

import tern.angular.AngularType;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.scriptpath.ITernScriptPath;
import tern.server.ITernServer;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;

public class ModulesFolder extends BaseModel implements
		ITernCompletionCollector {

	private List<Module> modules;

	public ModulesFolder(ITernScriptPath scriptPath) {
		super("modules", Type.ModulesFolder, scriptPath);
	}

	public Object[] getModules() {
		if (modules == null) {
			modules = new ArrayList<Module>();
			// load all modules
			TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
					AngularType.module);
			query.setExpression("");
			super.execute(query, this);
		}
		return modules.toArray();
	}

	@Override
	public void addProposal(TernCompletionProposalRec proposal,
			Object completion, IJSONObjectHelper jsonObjectHelper) {
		modules.add(new Module(proposal.name, getScriptPath()));
	}
}
