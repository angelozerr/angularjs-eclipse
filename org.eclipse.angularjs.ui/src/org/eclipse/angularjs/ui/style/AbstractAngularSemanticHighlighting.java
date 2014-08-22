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
package org.eclipse.angularjs.ui.style;

import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Base class for angular semantic highlighting. This class check that the
 * region belong to a file which has Angular nature to compute tokens.
 *
 */
public abstract class AbstractAngularSemanticHighlighting implements
		ISemanticHighlightingExtension, ISemanticHighlighting {

	private static final Position[] EMPTY_POSITION = new Position[0];

	@Override
	public Position[] consumes(IStructuredDocumentRegion documentRegion,
			IndexedRegion indexedRegion) {
		if (indexedRegion != null && indexedRegion instanceof IDOMNode) {
			IDOMNode node = (IDOMNode) indexedRegion;
			IFile file = DOMUtils.getFile(node);
			IProject project = file.getProject();
			if (AngularProject.hasAngularNature(project)) {
				// project has angular nature, compute positions.
				List<Position> positions = consumes(node, file, documentRegion);
				if (positions != null) {
					return positions.toArray(EMPTY_POSITION);
				}
			}
		}
		return null;
	}

	@Override
	public String getBoldPreferenceKey() {
		return null;
	}

	@Override
	public String getUnderlinePreferenceKey() {
		return null;
	}

	@Override
	public String getStrikethroughPreferenceKey() {
		return null;
	}

	@Override
	public String getItalicPreferenceKey() {
		return null;
	}

	@Override
	public String getColorPreferenceKey() {
		return null;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return AngularUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public final Position[] consumes(IStructuredDocumentRegion region) {
		// do nothing.
		return null;
	}

	/**
	 * Returns a list of positions iff the semantic highlighting consumes any
	 * part of the structured document region.
	 * 
	 * @param node
	 *            the DOM node.
	 * @param file
	 *            the file.
	 * @param documentRegion
	 *            the region.
	 * @return a list of positions iff the semantic highlighting consumes any
	 *         part of the structured document region.
	 */
	protected abstract List<Position> consumes(IDOMNode node, IFile file,
			IStructuredDocumentRegion documentRegion);
}
