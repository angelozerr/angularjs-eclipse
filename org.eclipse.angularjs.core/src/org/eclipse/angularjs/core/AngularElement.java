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

import tern.angular.AngularType;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.server.protocol.definition.ITernDefinitionCollector;

public class AngularElement extends BaseModel implements IDefinitionAware {

	private final AngularType angularType;
	private final Module module;

	public AngularElement(String name, AngularType angularType, Module module) {
		super(name, Type.AngularElement, module.getScriptPath());
		this.angularType = angularType;
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	@Override
	public void findDefinition(ITernDefinitionCollector collector) {
		// Find definition of the angular element
		TernAngularDefinitionQuery query = new TernAngularDefinitionQuery(
				angularType);
		query.getScope().setModule(getModule().getName());
		query.setExpression(super.getName());
		super.execute(query, collector);
	}

	public AngularType getAngularType() {
		return angularType;
	}
}
