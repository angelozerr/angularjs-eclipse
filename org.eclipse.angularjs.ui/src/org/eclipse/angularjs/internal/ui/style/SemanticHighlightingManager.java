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
package org.eclipse.angularjs.internal.ui.style;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.angularjs.ui.style.AbstractAngularSemanticHighlighting;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Angular semantic highlighting manager
 *
 */
public class SemanticHighlightingManager {

	private static SemanticHighlightingManager instance;

	private List<AbstractAngularSemanticHighlighting> highlightings = new LinkedList<AbstractAngularSemanticHighlighting>();

	public synchronized static SemanticHighlightingManager getInstance() {
		if (instance == null) {
			instance = new SemanticHighlightingManager();
		}
		return instance;
	}

	private SemanticHighlightingManager() {
		super();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						"org.eclipse.wst.sse.ui.semanticHighlighting"); //$NON-NLS-1$
		try {
			loadContributor(elements);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE,
					"Error while loading angular semantic manager", e);
		}
	}

	private SemanticHighlightingManager loadContributor(
			IConfigurationElement[] elements) throws Exception {
		for (IConfigurationElement element : elements) {
			String target = element.getAttribute("target"); //$NON-NLS-1$
			if ("org.eclipse.wst.html.core.htmlsource".equals(target)) { //$NON-NLS-1$
				final Object o = element.createExecutableExtension("class"); //$NON-NLS-1$
				if (o instanceof AbstractAngularSemanticHighlighting) {
					AbstractAngularSemanticHighlighting instance = (AbstractAngularSemanticHighlighting) o;
					highlightings.add(instance);
				}
			}
		}
		return this;
	}

	public List<AbstractAngularSemanticHighlighting> getHighlightings() {
		return highlightings;
	}

}
