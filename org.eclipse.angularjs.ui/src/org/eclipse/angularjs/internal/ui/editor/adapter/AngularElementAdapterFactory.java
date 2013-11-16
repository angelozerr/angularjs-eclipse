/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.editor.adapter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * This adapter factory class is used to create a GenericActionFilter when
 * performing a right-click within the editor
 * 
 * @author yaronm
 */
public class AngularElementAdapterFactory implements IAdapterFactory {

	private static Map<Class<?>, Object> adapterType2Object = new HashMap<Class<?>, Object>(
			4);
	static {
		//adapterType2Object.put(IActionFilter.class, new GenericActionFilter());
		//adapterType2Object.put(ISearchPageScoreComputer.class,
		//		new DLTKSearchPageScoreComputer());
	}

	public AngularElementAdapterFactory() {
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		/*if (adaptableObject instanceof IImplForPhp) {
			if (adapterType == IModelElement.class) {
				return ((IImplForPhp) adaptableObject).getModelElement();
			}
			// commenting the next block of code fixes these bugs
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=257421
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=257681
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=271734

			// if (adapterType == IResource.class) {
			// IModelElement modelElement = ((IImplForPhp)
			// adaptableObject).getModelElement();
			// if (modelElement != null) {
			// return modelElement.getResource();
			// }
			// }
		}*/
		return adapterType2Object.get(adapterType);
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		Class[] classArray = new Class[adapterType2Object.size()];
		adapterType2Object.entrySet().toArray(classArray);
		return classArray;
	}
}
