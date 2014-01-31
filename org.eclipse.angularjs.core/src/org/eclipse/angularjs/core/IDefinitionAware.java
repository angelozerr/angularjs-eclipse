package org.eclipse.angularjs.core;

import tern.server.protocol.definition.ITernDefinitionCollector;

public interface IDefinitionAware {

	void findDefinition(ITernDefinitionCollector collector);

}
