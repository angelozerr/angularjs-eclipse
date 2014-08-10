package org.eclipse.angularjs.core.utils;

import java.util.Collections;
import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.angularjs.internal.core.documentModel.provisional.contenttype.ContentTypeIdForAngular;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tern.angular.modules.DOMDirectiveProvider;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveParameter;

/**
 * Angular DOM utilities.
 *
 */
public class AngularDOMUtils {

	/**
	 * Returns true if if the given node is an Angular DOM and false otherwise.
	 * 
	 * @param node
	 *            DOM node
	 * @return true if if the given node is an Angular DOM and false otherwise.
	 */
	public static boolean isAngularContentType(IDOMNode node) {
		return DOMUtils.isContentTypeId(node,
				ContentTypeIdForAngular.ContentTypeID_Angular);
	}

	/**
	 * Returns true if the given DOM node is an angular directive an dfalse
	 * otherwise.
	 * 
	 * @param node
	 *            DOM node
	 * @return true if the given DOM node is an angular directive an dfalse
	 *         otherwise.
	 */
	public static boolean isAngularDirective(Node node) {
		if (node == null) {
			return false;
		}
		switch (node.getNodeType()) {
		case Node.ATTRIBUTE_NODE:
			return isAngularDirective((IDOMAttr) node);
		case Node.ELEMENT_NODE:
			return isAngularDirective((IDOMElement) node);
		}
		return false;
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
	public static boolean isAngularDirective(IDOMAttr attr) {
		if (attr == null) {
			return false;
		}
		IProject project = DOMUtils.getFile(attr).getProject();
		return getAngularDirective(project, attr) != null;
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
	public static Directive getAngularDirective(IProject project, Attr attr) {
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMSSEDirectiveProvider.getInstance().getAngularDirective(
					angularProject, attr);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return null;
	}

	/**
	 * Returns the {@link Directive} by the attribute region from the SSE DOM
	 * element {@link IDOMElement} and null otherwise.
	 * 
	 * @param element
	 *            the SSE DOM element {@link IDOMElement}.
	 * @param region
	 *            the region.
	 * 
	 * @return the {@link Directive} by the attribute region from the SSE DOM
	 *         element {@link IDOMElement} anf null otherwise.
	 */
	public static Directive getAngularDirectiveByRegion(IDOMNode element,
			ITextRegion region) {
		IDOMAttr attr = DOMUtils.getAttrByRegion(element, region);
		IProject project = DOMUtils.getFile(element).getProject();
		return getAngularDirective(project, attr);
	}

	/**
	 * Returns true if the given element is an angular directive and false
	 * otherwise.
	 * 
	 * @param attr
	 *            DOM element.
	 * @return true if the given attribute is an angular directive and false
	 *         otherwise.
	 */
	public static boolean isAngularDirective(IDOMElement element) {
		if (element == null) {
			return false;
		}
		IProject project = DOMUtils.getFile(element).getProject();
		return getAngularDirective(project, element) != null;
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
	public static Directive getAngularDirective(IProject project,
			Element element) {
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMSSEDirectiveProvider.getInstance().getAngularDirective(
					angularProject, element);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
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
	public static boolean isAngularDirectiveParameter(IDOMAttr attr) {
		if (attr == null) {
			return false;
		}
		IProject project = DOMUtils.getFile(attr).getProject();
		return getAngularDirectiveParameter(project, attr) != null;
	}

	/**
	 * Returns the angular {@link Directive} of the given attribute and null
	 * otherwise.
	 * 
	 * @param project
	 * 
	 * @param attr
	 *            DOM attribute.
	 * @return the angular {@link Directive} of the given attribute and null
	 *         otherwise.
	 */
	public static DirectiveParameter getAngularDirectiveParameter(
			IProject project, Attr attr) {
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMSSEDirectiveProvider.getInstance()
					.getAngularDirectiveParameter(angularProject, attr);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return null;
	}

	public static boolean hasAngularNature(IDOMNode element) {
		IFile file = DOMUtils.getFile(element);
		if (file == null) {
			return false;
		}
		return AngularProject.hasAngularNature(file.getProject());
	}

	public static Directive getAngularDirective(IProject project, Node node) {
		if (node == null) {
			return null;
		}
		switch (node.getNodeType()) {
		case Node.ATTRIBUTE_NODE:
			return getAngularDirective(project, (IDOMAttr) node);
		case Node.ELEMENT_NODE:
			return getAngularDirective(project, (IDOMElement) node);
		}
		return null;
	}

	public static List<Directive> getAngularDirectives(IProject project,
			Element element, Attr attr) {
		try {
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			return DOMSSEDirectiveProvider.getInstance().getAngularDirectives(
					angularProject, element, attr);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return Collections.emptyList();
	}

}
