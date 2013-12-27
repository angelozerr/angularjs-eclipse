package org.eclipse.angularjs.core.utils;

import java.io.IOException;

import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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

	public static TernDoc createDoc(Node element, IFile file,
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
		case directiveRepeat:
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
		if (attributes != null) {
			Attr node = null;
			for (int i = 0; i < attributes.getLength(); i++) {
				node = (Attr) attributes.item(i);
				Directive directive = DOMUtils.getAngularDirective(node);
				if (directive != null) {
					switch (directive.getType()) {
					case module:
						String module = ((Attr) node).getValue();
						scope.setModule(module);
						return;
					case controller:
						if (populateController) {
							String controller = ((Attr) node).getValue();
							scope.addController(controller);
						}
						break;
					case model:
						String model = ((Attr) node).getValue();
						scope.addModel(model);
						break;
					case directiveRepeat:
						String expression = ((Attr) node).getValue();
						scope.addRepeat(expression);
						break;
					default:
						break;
					}
				}
			}
		}
		Node parent = element.getPreviousSibling();
		if (parent == null)
			parent = element.getParentNode();
		populateScope(scope, parent, populateController);
	}

}
