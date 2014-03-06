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
package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.eclipse.ide.core.IDETernProject;

/**
 * 
 * HTML Angular HyperLink Detector.
 */
public class HTMLAngularHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}
		IDocument document = textViewer.getDocument();
		// Get the selected Node.
		IDOMNode currentNode = DOMUtils.getNodeByOffset(document,
				region.getOffset());
		if (currentNode == null) {
			return null;
		}

		IFile file = DOMUtils.getFile(currentNode);
		IProject project = file.getProject();
		if (IDETernProject.hasTernNature(project)) {
			// Get selected attribute
			IDOMAttr attr = DOMUtils.getAttrByOffset(currentNode,
					region.getOffset());

			IDOMNode node = attr != null ? attr : currentNode;
			Directive directive = DOMUtils.getAngularDirective(project, node);
			if (directive != null) {
				try {
					IHyperlink hyperlink = null;
					IDETernProject ternProject = AngularProject
							.getTernProject(project);
					if (attr != null) {
						boolean isAttrValue = region.getOffset() > attr
								.getNameRegionEndOffset();
						if (isAttrValue) {
							// Hyperlink on attr value

							// the attribute is directive, try to open the
							// angular
							// element controller, module, etc.
							hyperlink = new HTMLAngularHyperLink(attr.getOwnerElement(),
									HyperlinkUtils.getValueRegion(attr), file,
									ternProject, attr.getValue(),
									directive.getType());

						} else {
							// Hyperlink on attr name, try to open the custom
							// directive
							if (directive.isCustom()) {
								hyperlink = new HTMLAngularHyperLink(attr.getOwnerElement(),
										HyperlinkUtils.getNameRegion(attr),
										file, ternProject, directive.getName(),
										AngularType.directive);
							}
						}
					} else {
						// Hyperlink on element name, try to open the custom
						// directive
						if (directive.isCustom()) {
							IDOMElement element =(IDOMElement)node;
							hyperlink = new HTMLAngularHyperLink(element,
									HyperlinkUtils.getElementRegion(element),
									file, ternProject, directive.getName(),
									AngularType.directive);
						}
					}
					if (hyperlink != null) {
						IHyperlink[] hyperlinks = new IHyperlink[1];
						hyperlinks[0] = hyperlink;
						return hyperlinks;
					}
				} catch (CoreException e) {
					Trace.trace(Trace.WARNING, "Error while Angular hyperlink",
							e);
				}

			}
		}
		return null;
	}

}
