/*******************************************************************************
 * Copyright (c) 2011 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:      
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.angularjs.core.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.documentModel.dom.IAngularDOMAttr;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;

/**
 * Utilities for SSE DOM node {@link IDOMNode}.
 * 
 */
public class DOMUtils {

	/**
	 * Returns the SSE DOM Node {@link IDOMNode} by offset from the
	 * {@link IDocument} document and null if not found.
	 * 
	 * @param document
	 *            the document.
	 * @param offset
	 *            the offset.
	 * @return
	 */
	public static final IDOMNode getNodeByOffset(IDocument document, int offset) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			return getNodeByOffset(model, offset);
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Returns the SSE DOM Node {@link IDOMNode} by offset from the
	 * {@link IStructuredModel} SSE mode and null if not found.
	 * 
	 * @param model
	 *            the SSE model.
	 * @param offset
	 *            the offset.
	 * @return
	 */
	public static final IDOMNode getNodeByOffset(IStructuredModel model,
			int offset) {
		if (model != null) {
			IndexedRegion node = model.getIndexedRegion(offset);
			if (node instanceof IDOMNode) {
				return (IDOMNode) node;
			}
		}
		return null;
	}

	/**
	 * Returns the SSE DOM Attribute {@link IDOMAttr} by region from the SSE DOM
	 * element {@link IDOMElement}.
	 * 
	 * @param element
	 *            the SSE DOM element {@link IDOMElement}.
	 * @param region
	 *            the region.
	 * @return
	 */
	public static IDOMAttr getAttrByRegion(IDOMNode element, ITextRegion region) {
		IStructuredDocumentRegion structuredDocumentRegionElement = element
				.getFirstStructuredDocumentRegion();

		// 1) Get attribute name region
		ITextRegionList elementRegions = structuredDocumentRegionElement
				.getRegions();
		int index = elementRegions.indexOf(region);
		if (index < 0) {
			return null;
		}

		ITextRegion attrNameRegion = null;
		while (index >= 0) {
			attrNameRegion = elementRegions.get(index--);
			if (attrNameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				break;
			}
		}
		if (attrNameRegion == null) {
			return null;
		}
		String attrName = structuredDocumentRegionElement
				.getText(attrNameRegion);
		return (IDOMAttr) element.getAttributes().getNamedItem(attrName);
	}

	/**
	 * Returns the SSE DOM Attribute {@link IDOMAttr} by offset from the SSE DOM
	 * node {@link IDOMNode}.
	 * 
	 * @param element
	 *            the SSE DOM element {@link IDOMElement}.
	 * @param region
	 *            the region.
	 * @return
	 */
	public static final IDOMAttr getAttrByOffset(Node node, int offset) {
		if ((node instanceof IndexedRegion)
				&& ((IndexedRegion) node).contains(offset)
				&& node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset))
					return (IDOMAttr) attrs.item(i);
			}

		}
		return null;
	}

	/**
	 * Returns the owner file of the SSE DOM Node {@link IDOMNode}.
	 * 
	 * @param node
	 *            the SSE DOM Node.
	 * @return
	 */
	public static final IFile getFile(IDOMNode node) {
		return getFile(node.getModel());
	}

	/**
	 * Returns the owner file of the JFace document {@link IDocument}.
	 * 
	 * @param document
	 * @return
	 */
	public static final IFile getFile(IDocument document) {
		if (document == null) {
			return null;
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			if (model != null) {
				return getFile(model);
			}
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
		return null;
	}

	/**
	 * Returns the owner file of the SSE model {@link IStructuredModel}.
	 * 
	 * @param node
	 *            the SSE model.
	 * @return
	 */
	public static final IFile getFile(IStructuredModel model) {
		String baselocation = model.getBaseLocation();
		if (baselocation != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baselocation);
			if (filePath.segmentCount() > 1) {
				return root.getFile(filePath);
			}
		}
		return null;
	}

	/**
	 * Returns the owner file name of the SSE model {@link IDOMNode}.
	 * 
	 * @param node
	 * @return
	 */
	public static final String getFileName(IDOMNode node) {
		return getFileName(node.getModel());
	}

	/**
	 * Returns the owner file name of the SSE model {@link IStructuredModel}.
	 * 
	 * @param model
	 * @return
	 */
	public static final String getFileName(IStructuredModel model) {
		String baselocation = model.getBaseLocation();
		if (baselocation != null) {
			int index = baselocation.lastIndexOf('/');
			if (index == -1) {
				index = baselocation.lastIndexOf('\\');
			}
			if (index == -1) {
				return baselocation;
			}
			return baselocation.substring(index + 1, baselocation.length());
		}
		return null;
	}

	/**
	 * Returns the node value from the DOM NOde.
	 * 
	 * @param node
	 * @return
	 */
	public static String getNodeValue(Node node) {
		if (node == null) {
			return null;
		}
		short nodeType = node.getNodeType();
		switch (nodeType) {
		case Node.ATTRIBUTE_NODE:
			return ((Attr) node).getValue();
		case Node.TEXT_NODE:
			return getTextContent((Text) node);
		}
		return node.getNodeValue();
	}

	/**
	 * Returns the normalized text content of DOM text node.
	 * 
	 * @param text
	 * @return
	 */
	public static String getTextContent(Text text) {
		return getTextContent(text, true);
	}

	/**
	 * Returns the text content of DOM text node.
	 * 
	 * @param text
	 * @return
	 */
	public static String getTextContent(Text text, boolean normalize) {
		if (normalize) {
			return StringUtils.normalizeSpace(text.getData());
		}
		return text.getData();
	}

	/**
	 * Returns the first child element retrieved by tag name from the parent
	 * node and null otherwise.
	 * 
	 * @param parentNode
	 *            parent node.
	 * @param elementName
	 *            element name to found.
	 * @return the first child element
	 */
	public static Element getFirstChildElementByTagName(Node parentNode,
			String elementName) {
		Element result = null;

		if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
			result = ((Document) parentNode).getDocumentElement();
			if (!result.getNodeName().equals(elementName)) {
				result = null;
			}
		} else {
			NodeList nodes = parentNode.getChildNodes();
			Node node;
			for (int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE
						&& node.getNodeName().equals(elementName)) {
					result = (Element) node;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns list of the first child element retrieved by tag name from the
	 * parent node and null otherwise.
	 * 
	 * @param parentNode
	 *            parent node.
	 * @param elementName
	 *            element name to found.
	 * @return list of the first child element
	 */
	public static Collection<Element> getFirstChildElementsByTagName(
			Node contextNode, String elementName) {
		Collection<Element> elements = null;
		Element result = null;

		if (contextNode.getNodeType() == Node.DOCUMENT_NODE) {
			result = ((Document) contextNode).getDocumentElement();
			if (!result.getNodeName().equals(elementName)) {
				result = null;
			}
		} else {
			NodeList nodes = contextNode.getChildNodes();
			Node node;
			for (int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE
						&& node.getNodeName().equals(elementName)) {
					if (elements == null) {
						elements = new ArrayList<Element>();
					}
					result = (Element) node;
					elements.add(result);
				}
			}
		}
		if (elements == null) {
			return Collections.emptyList();
		}
		return elements;
	}

	/**
	 * Returns the SSE DOM attribute by name from the ownner element.
	 * 
	 * @param element
	 * @param attrName
	 * @return
	 */
	public static IDOMAttr getAttr(IDOMElement element, String attrName) {
		if (StringUtils.isEmpty(attrName)) {
			return null;
		}
		String prefix = element.getPrefix();
		if (!StringUtils.isEmpty(prefix)) {
			String namespaceURI = element.getNamespaceURI();
			IDOMAttr attr = (IDOMAttr) element.getAttributeNodeNS(namespaceURI,
					attrName);
			if (attr != null) {
				return attr;
			}
		}
		return (IDOMAttr) element.getAttributeNode(attrName);
	}

	/**
	 * Returns the first child Text node from the parentNode and null otherwise.
	 * 
	 * @param parentNode
	 * @return
	 */
	public static Text getTextNode(Node parentNode) {
		if (parentNode == null)
			return null;

		Node result = null;
		parentNode.normalize();
		NodeList nodeList;
		if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
			nodeList = ((Document) parentNode).getDocumentElement()
					.getChildNodes();
		} else {
			nodeList = parentNode.getChildNodes();
		}
		int len = nodeList.getLength();
		if (len == 0) {
			return null;
		}
		for (int i = 0; i < len; i++) {
			result = nodeList.item(i);
			if (result.getNodeType() == Node.TEXT_NODE) {
				return (Text) result;
			} else if (result.getNodeType() == Node.CDATA_SECTION_NODE) {
				return /* (CDATASection) */(Text) result;
			}
		}
		return null;
	}

	/**
	 * Returns the content of the the first child Text node from the parentNode
	 * and null otherwise.
	 * 
	 * @param node
	 * @return
	 */
	public static String getTextNodeAsString(Node parentNode) {
		if (parentNode == null)
			return null;

		Node txt = getTextNode(parentNode);
		if (txt == null)
			return null;

		return txt.getNodeValue();
	}

	/**
	 * Returns the content type id of the SSE DOM Node.
	 * 
	 * @param node
	 * @return
	 */
	public static String getContentTypeId(IDOMNode node) {
		if (node == null) {
			return null;
		}
		return node.getModel().getContentTypeIdentifier();
	}

	/**
	 * Returns true if content type id of the SSE DOM Node match a
	 * contentTypeId.
	 * 
	 * @param node
	 * @param contentTypeId
	 * @return
	 */
	public static boolean isContentTypeId(IDOMNode selectedNode,
			String[] contentTypeIds) {
		for (int i = 0; i < contentTypeIds.length; i++) {
			if (DOMUtils.isContentTypeId(selectedNode, contentTypeIds[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if content type id of the SSE DOM Node match a
	 * contentTypeId.
	 * 
	 * @param node
	 * @param contentTypeId
	 * @return
	 */
	public static boolean isContentTypeId(IDOMNode node, String contentTypeId) {
		if (contentTypeId == null) {
			return false;
		}
		String nodeContentTypeId = getContentTypeId(node);
		return contentTypeId.equals(nodeContentTypeId);
	}

	/**
	 * Returns the content type id of the file by using SSE DOM Model.
	 * 
	 * @param file
	 * @return
	 */
	public static String getStructuredModelContentTypeId(IFile file) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (model == null) {
				model = StructuredModelManager.getModelManager()
						.getModelForRead(file);
			}
			if (model != null) {
				return model.getContentTypeIdentifier();
			}
		} catch (IOException e) {
			return null;
		} catch (CoreException e) {
			return null;
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return null;
	}

	/**
	 * Returns true if file match an item of the contentTypeIds list and false
	 * otherwise.
	 * 
	 * @param file
	 * @param contentTypeIds
	 * @return
	 */
	public static boolean isContentTypeId(IFile file,
			Collection<String> contentTypeIds) {
		if (contentTypeIds == null) {
			return false;
		}
		String structuredModelContentTypeId = getStructuredModelContentTypeId(file);
		if (structuredModelContentTypeId == null) {
			return false;
		}
		return contentTypeIds.contains(structuredModelContentTypeId);
	}

	/**
	 * Returns true if file match the contentTypeId and false otherwise.
	 * 
	 * @param file
	 * @param contentTypeId
	 * @return
	 */
	public static boolean isContentTypeId(IFile file, String contentTypeId) {
		if (contentTypeId == null) {
			return false;
		}
		String structuredModelContentTypeId = getStructuredModelContentTypeId(file);
		if (structuredModelContentTypeId == null) {
			return false;
		}
		return contentTypeId.equals(structuredModelContentTypeId);
	}

	/**
	 * Returns the owner element of the node and null if not found.
	 * 
	 * @param node
	 * @return
	 */
	public static Element getOwnerElement(Node node) {
		int nodeType = node.getNodeType();
		switch (nodeType) {
		case Node.ATTRIBUTE_NODE:
			return ((Attr) node).getOwnerElement();
		case Node.TEXT_NODE:
			return (Element) ((Text) node).getParentNode();
		case Node.CDATA_SECTION_NODE:
			return (Element) ((CDATASection) node).getParentNode();
		case Node.ELEMENT_NODE:
			return (Element) node;
		}
		return null;
	}

	/**
	 * Returns true if the given attribute is an angular directive (ex : ng-app)
	 * and false otherwise.
	 * 
	 * @param attr
	 *            DOM attribute.
	 * @return true if the given attribute is an angular directive (ex : ng-app)
	 *         and false otherwise.
	 */
	public static boolean isAngularDirective(Attr attr) {
		if (attr == null) {
			return false;
		}
		if ((attr instanceof IAngularDOMAttr)) {
			return ((IAngularDOMAttr) attr).isAngularDirective();
		}
		return getAngularDirective(attr) != null;
	}

	/**
	 * Returns the angular {@link Directive} of the given attribute and null
	 * otherwise.
	 * 
	 * @param attr
	 *            DOM attribute.
	 * @return the angular {@link Directive} of the given attribute and null
	 *         otherwise.
	 */
	public static Directive getAngularDirective(Attr attr) {
		if (attr == null) {
			return null;
		}
		if ((attr instanceof IAngularDOMAttr)) {
			return ((IAngularDOMAttr) attr).getAngularDirective();
		}
		return AngularModulesManager.getInstance().getDirective(
				attr.getOwnerElement().getNodeName(), attr.getName());
	}

	/**
	 * Returns the {@link Directive} by the attribute region from the SSE DOM
	 * element {@link IDOMElement} anf null otherwise.
	 * 
	 * @param element
	 *            the SSE DOM element {@link IDOMElement}.
	 * @param region
	 *            the region.
	 * 
	 * @return the {@link Directive} by the attribute region from the SSE DOM
	 *         element {@link IDOMElement} anf null otherwise.
	 */
	public static Directive getAngularDirective(IDOMNode element,
			ITextRegion region) {
		IDOMAttr attr = DOMUtils.getAttrByRegion(element, region);
		return DOMUtils.getAngularDirective(attr);
	}

	/**
	 * Returns the list of the angular attributes names of the given DOM
	 * element.
	 * 
	 * <p>
	 * <div id="xxx" ng-model="MyModel" />
	 * 
	 * will return the array ['MyModel']
	 * </p>
	 * 
	 * @param element
	 * @return
	 */
	public static List<String> getAngularDirectiveNames(Element element,
			Attr selectedAttr) {
		if (element == null) {
			return Collections.emptyList();
		}
		List<String> names = null;
		NamedNodeMap attributes = element.getAttributes();
		int length = attributes.getLength();
		Attr attr = null;
		for (int i = 0; i < length; i++) {
			attr = (Attr) attributes.item(i);
			if (selectedAttr == null || !selectedAttr.equals(attr)) {
				Directive directive = getAngularDirective(attr);
				if (directive != null) {
					if (names == null) {
						names = new ArrayList<String>();
					}
					names.add(directive.getName());
				}
			}
		}
		return (List<String>) (names != null ? names : Collections.emptyList());
	}

	public static boolean hasAngularNature(IDOMNode element) {
		IFile file = DOMUtils.getFile(element);
		if (file == null) {
			return false;
		}
		return AngularProject.hasAngularNature(file.getProject());
	}

}
