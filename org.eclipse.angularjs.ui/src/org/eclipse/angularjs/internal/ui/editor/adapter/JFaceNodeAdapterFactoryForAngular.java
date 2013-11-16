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

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;

/**
 * This class is the same as
 * org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML Its
 * perpose is to enable the change in JFaceNodeAdapterForAngular
 * 
 * @author guy.g
 * 
 */
public class JFaceNodeAdapterFactoryForAngular extends JFaceNodeAdapterFactory {

	public JFaceNodeAdapterFactoryForAngular() {
		this(IJFaceNodeAdapter.class, true);
	}

	public JFaceNodeAdapterFactoryForAngular(Object adapterKey,
			boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			singletonAdapter = new JFaceNodeAdapterForAngular(this);
			initAdapter(singletonAdapter, node);
		}
		return singletonAdapter;
	}

	public INodeAdapterFactory copy() {
		return new JFaceNodeAdapterFactoryForAngular(getAdapterKey(),
				isShouldRegisterAdapter());
	}
}
