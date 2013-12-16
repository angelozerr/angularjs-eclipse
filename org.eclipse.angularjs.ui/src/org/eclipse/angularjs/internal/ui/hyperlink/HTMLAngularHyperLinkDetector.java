package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Node;

import tern.server.protocol.angular.AngularType;

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
				switch (directive.getType()) {
				case module:
					findModule(attr);
					break;

				default:
					break;
				}
			}
		}
		return null;
	}

	private void findModule(IDOMAttr attr) {
		// TODO Auto-generated method stub
		
	}

}
