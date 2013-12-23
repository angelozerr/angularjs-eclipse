package org.eclipse.angularjs.core.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.angularjs.core.modules.Directive;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
					if (populateController && scope.getController() == null) {
						String controller = ((Attr) node).getValue();
						scope.setController(controller);
					}
					break;
				case directiveRepeat:
					String expression = ((Attr) node).getValue();
					populateScope(expression, scope);
					break;
				default:
					break;
				}
			}
		}
		populateScope(scope, element.getParentNode(), populateController);
	}

	public static void populateScope(String expression, Map scope) {
		Pattern pattern = Pattern
				.compile("^\\s*(.+)\\s+in\\s+(.*?)\\s*(\\s+track\\s+by\\s+(.+)\\s*)?$");
		Matcher matcher = pattern.matcher(expression);
		while (matcher.find()) {
			String lhs = matcher.group(1);
			String rhs = matcher.group(2);

			Pattern pattern2 = Pattern
					.compile("^(?:([\\$\\w]+)|\\(([\\$\\w]+)\\s*,\\s*([\\$\\w]+)\\))$");
			Matcher matcher2 = pattern2.matcher(lhs);
			while (matcher2.find()) {
				String valueIdentifier = matcher.group(3) != null ? matcher
						.group(3) : matcher.group(1);
				String keyIdentifier = matcher.group(2);

				JSONObject repeat = new JSONObject();
				repeat.put(valueIdentifier, keyIdentifier);
				scope.put("repeat", repeat);
			}

		}
	}

	public static void main(String[] args) {
		populateScope(" a in firends ", new HashMap());
	}
}
