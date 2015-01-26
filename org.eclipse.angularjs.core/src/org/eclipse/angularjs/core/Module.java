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
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.scriptpath.ITernScriptPath;
import tern.server.ITernServer;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class Module extends BaseModel implements ITernCompletionCollector,
		IDefinitionAware {

	private List<AngularElement> elements;

	public Module(String name, ITernScriptPath scriptPath) {
		super(name, Type.Module, scriptPath);
	}

	public Object[] getAngularElements() {
		if (elements == null) {
			this.elements = new ArrayList<AngularElement>();
			// load all controllers of the given module
			TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
					AngularType.controller);
			query.addType(AngularType.directive);
			query.addType(AngularType.filter);
			query.addType(AngularType.factory);
			query.addType(AngularType.provider);
			query.addType(AngularType.service);
			query.getScope().setModule(super.getName());
			query.setExpression("");
			super.execute(query, this);
		}
		return elements.toArray();
	}

	@Override
	public void addProposal(TernCompletionProposalRec proposal,
			Object completion, IJSONObjectHelper jsonObjectHelper) {
		AngularType angularType = AngularType.get(jsonObjectHelper.getText(
				completion, "angularType"));
		elements.add(new AngularElement(proposal.name, angularType, Module.this));
	}

	@Override
	public void findDefinition(ITernDefinitionCollector collector) {
		// load all controllers of the given module
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				AngularType.module);
		query.setExpression(super.getName());
		super.execute(query, collector);
	}
}
