package org.eclipse.angularjs.internal.ui.hyperlink;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

import tern.TernException;
import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.server.protocol.TernDoc;

public class HTMLAngularHyperLinkDetector extends AbstractHyperlinkDetector {

	public static final IHyperlink[] EMPTY_HYPERLINK = new IHyperlink[0];

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
					IHyperlink hyperlink = new TernHyperlink(attr, file,
							ternProject, directive.getType());
					if (hyperlink != null) {
						IHyperlink[] hyperlinks = new IHyperlink[1];
						hyperlinks[0] = hyperlink;
						return hyperlinks;
					}
				}
			} catch (CoreException e) {
				Trace.trace(Trace.WARNING, "Error while tern hyperlink", e);
			}
		}
		return null;
	}

}
