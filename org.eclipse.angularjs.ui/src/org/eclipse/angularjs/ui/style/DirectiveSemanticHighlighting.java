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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.internal.ui.preferences.AngularUIPreferenceNames;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Directive semantic highlighting used to highlight elements/attributes which
 * are ng-* and custom angular directives. Examples :
 * 
 * <ul>
 * <li><div ng-controller : highlight ng-controller attribute.</li>
 * <li><ng-include src=""></ng-include> : highlight ng-include element.</li>
 * </ul>
 *
 */
public class DirectiveSemanticHighlighting extends
		AbstractAngularSemanticHighlighting {

	@Override
	public String getEnabledPreferenceKey() {
		return AngularUIPreferenceNames.HIGHLIGHTING_DIRECTIVE_ENABLED;
	}

	@Override
	protected List<Position> consumes(IDOMNode node, IFile file,
			IStructuredDocumentRegion documentRegion) {
		if (isDirectiveElement(node, file.getProject())) {
			// ex : highlight ng-include
			// <ng-include src=""></ng-include>
			return consumesElement(documentRegion);
		} else {
			// ex : highlight ng-controller
			// <div ng-controller
			NamedNodeMap attributes = node.getAttributes();
			if (attributes != null) {
				List<Position> positions = null;
				IDOMAttr attr = null;
				IDOMNode currentNode = null;
				// loop for attributes of the element
				for (int i = 0; i < attributes.getLength(); i++) {
					currentNode = (IDOMNode) attributes.item(i);
					if (isDirectiveAttr(currentNode, file.getProject())) {
						// attribute is a directive.
						attr = (IDOMAttr) currentNode;
						Position pos = new Position(
								attr.getNameRegionStartOffset(),
								attr.getNameRegionEndOffset()
										- attr.getNameRegionStartOffset());
						if (positions == null) {
							positions = new ArrayList<Position>();
						}
						positions.add(pos);
					}
				}
				return positions;
			}
		}
		return null;
	}

	private boolean isDirectiveElement(IDOMNode node, IProject project) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return false;
		}
		return AngularDOMUtils.getAngularDirective(project, (IDOMElement) node) != null;
	}

	protected boolean isDirectiveAttr(IDOMNode node, IProject project) {
		if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
			return false;
		}
		return AngularDOMUtils.getAngularDirective(project, (IDOMAttr) node) != null;
	}

	private List<Position> consumesElement(IStructuredDocumentRegion region) {
		List<Position> positions = null;
		ITextRegionList regionList = region.getRegions();
		for (int i = 0; i < regionList.size(); i++) {
			ITextRegion textRegion = regionList.get(i);
			if (textRegion.getType().equals(DOMRegionContext.XML_TAG_NAME)) {
				Position position = new Position(
						region.getStartOffset(textRegion),
						textRegion.getLength());
				if (positions == null) {
					positions = new ArrayList<Position>();
				}
				positions.add(position);
			}
		}
		return positions;
	}
}
