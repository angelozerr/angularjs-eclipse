package org.eclipse.angularjs.core.utils;

import java.io.IOException;

import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.json.simple.JSONArray;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tern.eclipse.ide.core.IDETernFileManager;
import tern.server.protocol.TernDoc;
import tern.server.protocol.angular.AngularType;
import tern.server.protocol.angular.TernAngularQuery;
import tern.server.protocol.angular.TernAngularScope;

public class HTMLTernAngularHelper {

	public static TernDoc createDoc(IDOMNode element, IFile file,
			IDETernFileManager fileManager, TernAngularQuery query)
			throws CoreException, IOException {

		populateScope(element, query);

		TernDoc doc = new TernDoc(query);

		// Update TernDoc#addFile
		JSONArray files = query.getFiles();
		fileManager.updateFiles(element, file, doc, files);

		return doc;

	}

	public static void populateScope(Node element, TernAngularQuery query) {
		TernAngularScope scope = query.getScope();
		populateScope(element, scope, query.getAngularType());
	}

	public static void populateScope(Node element, TernAngularScope scope,
			AngularType angularType) {
		switch (angularType) {
		case module:
			// do nothing;
			break;
		case controller:
			// find controller
			populateScope(scope, element, false);
			break;
		case model:
			// find model
			populateScope(scope, element, true);
			break;
		}

	}

	private static void populateScope(TernAngularScope scope, Node element,
			boolean populateController) {
		if (element == null || element.getNodeType() == Node.DOCUMENT_NODE) {
			return;
		}
		NamedNodeMap attributes = element.getAttributes();
		Node node = null;
		for (int i = 0; i < attributes.getLength(); i++) {
			node = attributes.item(i);
			if (node instanceof IAngularDOMAttr) {
				Directive directive = ((IAngularDOMAttr) node)
						.getAngularDirective();
				if (directive != null) {
					switch (directive.getType()) {
					case module:
						String module = ((Attr) node).getValue();
						scope.setModule(module);
						return;
					case controller:
						if (populateController && scope.getController() == null) {
							String controller = ((Attr) node).getValue();
							scope.setController(controller);
						}
					default:
						break;
					}
				}
			}
		}
		populateScope(scope, element.getParentNode(), populateController);
	}

}
