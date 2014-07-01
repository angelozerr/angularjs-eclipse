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
package org.eclipse.angularjs.internal.ui.validation;

import java.io.IOException;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.AngularScopeHelper;
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

import tern.angular.AngularType;
import tern.angular.modules.Directive;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.type.TernAngularTypeQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.protocol.type.ValidationTernTypeCollector;

public class HTMLAngularContentValidator extends AbstractValidator {

	@Override
	protected void doValidate(
			IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model) {
		IProject project = file.getProject();
		if (AngularProject.hasAngularNature(project)) {
			// do angular validation only of project has angular nature
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

						Directive directive = DOMUtils.getAngularDirective(
								project, attr);
						if (directive != null) {
							switch (directive.getType()) {
							case module:
							case controller:
								try {
									IDETernProject ternProject = AngularProject
											.getTernProject(project);

									boolean exists = find(attr, file,
											ternProject, directive.getType());
									if (!exists) {
										reporter.addMessage(this,
												ValidatorUtils.createMessage(
														attr,
														directive.getType()));
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
			Exception {

		TernAngularQuery query = new TernAngularTypeQuery(angularType);
		query.setExpression(AngularScopeHelper.getAngularValue(attr,
				angularType));

		ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
				attr.getOwnerElement(), file, angularType, query);

		ValidationTernTypeCollector collector = new ValidationTernTypeCollector();

		if (scriptPath != null) {
			ternProject.request(query, query.getFiles(), scriptPath, collector);
		} else {
			ternProject.request(query, query.getFiles(), attr, file, collector);
		}
		return collector.isExists();
	}
}
