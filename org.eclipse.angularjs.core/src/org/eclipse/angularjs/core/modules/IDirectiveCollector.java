package org.eclipse.angularjs.core.modules;

public interface IDirectiveCollector {

	boolean add(Directive directive, String nameWhichMatch);
}
