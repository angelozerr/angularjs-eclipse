/*******************************************************************************
 * Copyright (c) 2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
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
import org.w3c.dom.Node;

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
		Node currentNode = DOMUtils.getNodeByOffset(document,
				region.getOffset());
		if (currentNode == null) {
			return null;
		}
		// Get selected attribute
		IDOMAttr attr = DOMUtils.getAttrByOffset(currentNode,
				region.getOffset());
		if (attr == null) {
			return null;
		}
		IFile file = DOMUtils.getFile(attr);
		IProject project = file.getProject();
		if (IDETernProject.hasTernNature(project)) {
			try {

				Directive directive = DOMUtils.getAngularDirective(attr);
				if (directive != null) {
					IDETernProject ternProject = AngularProject
							.getTernProject(project);
					IHyperlink hyperlink = new HTMLAngularHyperLink(attr, file,
							ternProject, directive.getType());
					if (hyperlink != null) {
						IHyperlink[] hyperlinks = new IHyperlink[1];
						hyperlinks[0] = hyperlink;
						return hyperlinks;
					}
				}
			} catch (CoreException e) {
				Trace.trace(Trace.WARNING, "Error while Angular hyperlink", e);
			}
		}
		return null;
	}

}
