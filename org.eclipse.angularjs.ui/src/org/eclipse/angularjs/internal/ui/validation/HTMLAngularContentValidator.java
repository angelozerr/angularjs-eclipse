package org.eclipse.angularjs.internal.ui.validation;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tern.TernException;
import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.server.ITernServer;
import tern.server.protocol.TernDoc;
import tern.server.protocol.completions.TernCompletionItem;
import tern.server.protocol.type.ITernTypeCollector;

public class HTMLAngularContentValidator extends AbstractValidator {

	@Override
	protected void doValidate(
			IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model) {
		if (isStartTag(structuredDocumentRegion)) {
			IDOMNode node = DOMUtils.getNodeByOffset(model,
					structuredDocumentRegion.getStartOffset());
			if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {
				return;
			}

			IDOMElement element = (IDOMElement) node;
			NamedNodeMap map = element.getAttributes();
			for (int i = 0; i < map.getLength(); i++) {
				IDOMAttr attr = (IDOMAttr) map.item(i);
				if (attr.getValueRegionStartOffset() != 0) {
					Directive directive = DOMUtils.getAngularDirective(attr);
					if (directive != null) {
						switch (directive.getType()) {
						case module:
						case controller:

							IProject eclipseProject = file.getProject();
							try {
								IDETernProject ternProject = AngularProject
										.getTernProject(eclipseProject);

								boolean exists = find(attr, file, ternProject,
										directive.getType());
								if (!exists) {
									reporter.addMessage(this, ValidatorUtils
											.createMessage(attr,
													directive.getType()));
								}
							} catch (Exception e) {
								Trace.trace(Trace.SEVERE,
										"Error while tern hyperlink.", e);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Determines whether the IStructuredDocumentRegion is a XML "start tag"
	 * since they need to be checked for proper XML attribute region sequences
	 * 
	 * @param structuredDocumentRegion
	 * 
	 */
	private boolean isStartTag(
			IStructuredDocumentRegion structuredDocumentRegion) {
		if ((structuredDocumentRegion == null)
				|| structuredDocumentRegion.isDeleted()) {
			return false;
		}
		return structuredDocumentRegion.getFirstRegion().getType() == DOMRegionContext.XML_TAG_OPEN;
	}

	private boolean find(IDOMAttr attr, IFile file, IDETernProject ternProject,
			final AngularType angularType) throws CoreException, IOException,
			TernException {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(attr.getValue());
		HTMLTernAngularHelper.populateScope((IDOMNode) attr.getOwnerElement(),
				DOMSSEDirectiveProvider.getInstance(), query);

		ValidationTernTypeCollector collector = new ValidationTernTypeCollector();
		ternProject.request(query, query.getFiles(), attr, file, collector);
		return collector.isExists();
	}
}
