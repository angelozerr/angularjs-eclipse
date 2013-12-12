package org.eclipse.angularjs.core.modules;

public interface IDirectiveCollector {

	void add(Directive directive, String nameWhichMatch);
}
