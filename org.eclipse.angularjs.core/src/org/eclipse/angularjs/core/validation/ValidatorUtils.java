/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.angularjs.core.validation;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.AngularCoreMessages;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tern.ITernFile;
import tern.angular.AngularType;
import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.Restriction;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.scriptpath.ITernScriptPath;
import tern.server.protocol.type.ValidationTernTypeCollector;

/**
 * Utilities class used to validate Angular elements/attributes.
 *
 */
public class ValidatorUtils {

	/**
	 * Return true if the given error must be ignored and false otherwise.
	 * 
	 * @param info
	 * @param project
	 * @return true if the given error must be ignored and false otherwise.
	 */
	public static boolean isIgnoreError(ErrorInfo info, IProject project) {
		int targetType = info.getTargetType();
		// org.eclipse.wst.html.core.internal.validate.ErrorState.UNDEFINED_NAME_ERROR
		// = 11 is private -(
		if ((targetType == Node.ATTRIBUTE_NODE || targetType == Node.ELEMENT_NODE)
				&& info.getState() == 11) {
			// It's an error about attribute name, check if it's an Angular
			// Attribute (ex : ng-app)
			String name = info.getHint();
			if (isDirective(project, name, targetType)) {
				// it's an angular directive, ignore the error
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given name is a directive for the current node
	 * (attribute, element) and false otherwise.
	 * 
	 * @param project
	 * @param name
	 * @param targetType
	 * @return
	 */
	private static boolean isDirective(IProject project, String name,
			int targetType) {
		try {
			if (AngularProject.hasAngularNature(project)) {
				AngularProject angularProject = AngularProject
						.getAngularProject(project);
				return AngularModulesManager.getInstance().getDirective(
						angularProject, null, name, getRestriction(targetType)) != null;
			}
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Error while getting angular project", e);
		}
		return false;
	}

	private static Restriction getRestriction(int targetType) {
		switch (targetType) {
		case Node.ATTRIBUTE_NODE:
			return Restriction.A;
		case Node.ELEMENT_NODE:
			return Restriction.E;
		}
		return null;
	}

	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived()
					|| resource.isTeamPrivateMember()
					|| !resource.isAccessible()
					|| (resource.getName().charAt(0) == '.' && resource
							.getType() == IResource.FOLDER)) {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	public static void validateFile(IReporter reporter, IFile file,
			ValidationResult result, IValidator origin) {
		if (AngularProject.hasAngularNature(file.getProject())) {
			if ((reporter != null) && (reporter.isCancelled() == true)) {
				throw new OperationCanceledException();
			}
			if (!shouldValidate(file)) {
				return;
			}
			IDOMModel model = DOMUtils.getModel(file.getProject(), file);
			if (model == null)
				return;
			IStructuredDocumentRegion[] regions = ((IStructuredDocument) model
					.getStructuredDocument()).getStructuredDocumentRegions();
			validate(reporter, file, model, regions, origin);
		}
	}

	private static void validate(IReporter reporter, IFile file,
			IDOMModel model, IStructuredDocumentRegion[] regions,
			IValidator origin) {
		for (int i = 0; i < regions.length; i++) {
			validate(regions[i], reporter, file, model, origin);
		}
	}

	public static void validate(
			IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model,
			IValidator origin) {
		IProject project = file.getProject();
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

					Directive directive = AngularDOMUtils.getAngularDirective(
							project, attr);
					if (directive != null) {
						switch (directive.getType()) {
						case module:
						case controller:
							try {
								IIDETernProject ternProject = AngularProject
										.getTernProject(project);

								boolean exists = isAngularElementExists(attr,
										file,
										structuredDocumentRegion
												.getParentDocument(),
										ternProject, directive.getType());
								if (!exists) {
									reporter.addMessage(
											origin,
											createMessage(attr,
													directive.getType(), file));
								}
							} catch (Exception e) {
								Trace.trace(Trace.SEVERE,
										"Error while tern validator.", e);
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
	private static boolean isStartTag(
			IStructuredDocumentRegion structuredDocumentRegion) {
		if ((structuredDocumentRegion == null)
				|| structuredDocumentRegion.isDeleted()) {
			return false;
		}
		return structuredDocumentRegion.getFirstRegion().getType() == DOMRegionContext.XML_TAG_OPEN;
	}

	private static boolean isAngularElementExists(IDOMAttr attr, IFile file,
			IDocument document, IIDETernProject ternProject,
			final AngularType angularType) throws CoreException, IOException,
			Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(AngularScopeHelper.getAngularValue(attr,
				angularType));

		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
				attr.getOwnerElement(), file, angularType, query);

		ValidationTernTypeCollector collector = new ValidationTernTypeCollector();

		if (scriptPath != null) {
			ternProject.request(query, query.getFiles(), scriptPath, null, null, collector);
		} else {
			ITernFile tf = new TernDocumentFile(file, document);
			ternProject.request(query, query.getFiles(), null, attr, tf, collector);
		}
		return collector.isExists();
	}

	// -------------------- Messages builder.

	/**
	 * Create a message.
	 * 
	 * @param attr
	 * @param type
	 * @param resource
	 * @return
	 */
	private static LocalizedMessage createMessage(IDOMAttr attr,
			AngularType type, IResource resource) {
		String textContent = AngularScopeHelper.getAngularValue(attr, type);
		int start = attr.getValueRegionStartOffset();
		return createMessage(attr, start, textContent, type, resource);
	}

	public static LocalizedMessage createMessage(IDOMAttr node, int start,
			String textContent, AngularType type, IResource resource) {
		int length = textContent.trim().length() + 2;
		String messageText = NLS.bind(
				AngularCoreMessages.Validation_AngularElementNotFound,
				type.name(), textContent);
		// nbElements,
		// textContent);
		int severity = IMessage.HIGH_SEVERITY; // getSeverity(reference,
												// nbElements);
		return createMessage(start, length, messageText, severity,
				node.getStructuredDocument(), resource);
	}

	public static LocalizedMessage createMessage(int start, int length,
			String messageText, int severity,
			IStructuredDocument structuredDocument, IResource resource) {
		int lineNo = getLineNumber(start, structuredDocument);
		LocalizedMessage message = new LocalizedMessage(severity, messageText,
				resource);
		message.setOffset(start);
		message.setLength(length);
		message.setLineNo(lineNo);
		return message;
	}

	private static int getLineNumber(int start, IDocument document) {
		int lineNo = -1;
		try {
			lineNo = document.getLineOfOffset(start);
		} catch (BadLocationException e) {
			Trace.trace(Trace.SEVERE, e.getMessage(), e);
		}
		return lineNo;
	}
}
