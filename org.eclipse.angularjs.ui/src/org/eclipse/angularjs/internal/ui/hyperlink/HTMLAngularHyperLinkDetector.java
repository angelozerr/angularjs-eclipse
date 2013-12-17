package org.eclipse.angularjs.internal.ui.hyperlink;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HTMLTernAngularHelper;
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
import tern.eclipse.ide.core.IDETernProject;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.angular.AngularType;
import tern.server.protocol.angular.TernAngularQuery;
import tern.server.protocol.angular.definitions.TernAngularDefinitionQuery;

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

		if (attr instanceof IAngularDOMAttr) {
			Directive directive = ((IAngularDOMAttr) attr)
					.getAngularDirective();
			if (directive != null) {

				IFile file = DOMUtils.getFile(attr);
				IProject eclipseProject = file.getProject();
				try {
					IDETernProject ternProject = AngularProject
							.getTernProject(eclipseProject);

					IHyperlink hyperlink = find(attr, file, ternProject,
							directive.getType());
					if (hyperlink != null) {
						IHyperlink[] hyperlinks = new IHyperlink[1];
						hyperlinks[0] = hyperlink;
						return hyperlinks;
					}

				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error while tern hyperlink.", e);
				}
			}
		}
		return null;
	}

	private IHyperlink find(IDOMAttr attr, IFile file,
			IDETernProject ternProject, AngularType angularType)
			throws CoreException, IOException, TernException {

		TernAngularQuery query = new TernAngularDefinitionQuery(angularType);
		query.setExpression(attr.getValue());

		TernDoc doc = HTMLTernAngularHelper.createDoc(
				(IDOMNode) attr.getOwnerElement(), file,
				ternProject.getFileManager(), query);

		ITernServer server = ternProject.getTernServer();
		TernHyperlinkCollector collector = new TernHyperlinkCollector(attr);
		server.request(doc, collector);
		return collector.getHyperlink();
	}

}
