/**
AngularProject *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.internal.core.documentModel.parser;

import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;

public class AngularStructuredDocumentReParser extends
		XMLStructuredDocumentReParser {

	public AngularStructuredDocumentReParser() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser
	 * #newInstance()
	 */
	public IStructuredTextReParser newInstance() {
		return new AngularStructuredDocumentReParser();
	}

	/**
	 * This function was added in order to support asp tags in PHP (bug fix
	 * #150363)
	 */
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionSyntax() {
		StructuredDocumentEvent result = super
				.checkForCrossStructuredDocumentRegionSyntax();
		if (result == null) {
			result = checkForCriticalKey(AngularProject.START_ANGULAR_EXPRESSION_TOKEN); //$NON-NLS-1$
			if (result == null)
				result = checkForCriticalKey(AngularProject.END_ANGULAR_EXPRESSION_TOKEN); //$NON-NLS-1$

		}
		return result;
	}

	/**
	 * Change PHP Script Regions nodes...
	 */
	protected StructuredDocumentEvent regionCheck(
			IStructuredDocumentRegion oldNode, IStructuredDocumentRegion newNode) {
		final StructuredDocumentEvent event = super.regionCheck(oldNode,
				newNode);

		if (event instanceof RegionChangedEvent) {
			RegionChangedEvent changedEvent = (RegionChangedEvent) event;

			if (changedEvent.getRegion().getType() == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT) {
				oldNode.setRegions(newNode.getRegions());
			}
		}
		return event;
	}

	/**
	 * This implementation updates the php tokens model after updating WST
	 * editor model
	 */
	public StructuredDocumentEvent reparse() {
		final StructuredDocumentEvent documentEvent = super.reparse();
		return documentEvent;
	}

	@Override
	public StructuredDocumentEvent _checkBlockNodeList(List blockTagList) {
		// There are no blocktags that should be checked within Angular
		// expression
		// content
		if (dirtyStart.equals(dirtyEnd)) {
			ITextRegion region = dirtyStart.getRegionAtCharacterOffset(fStart);
			if (region != null
					&& region.getType().equals(
							AngularRegionContext.ANGULAR_EXPRESSION_CONTENT)
					&& (dirtyStart.getStart() + region.getEnd() >= (fStart + fLengthToReplace))) {
				return null;
			}
		}
		return super._checkBlockNodeList(blockTagList);
	}

}
