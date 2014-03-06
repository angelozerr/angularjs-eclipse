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
package org.eclipse.angularjs.internal.ui.contentassist;

import java.util.Collection;
import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.core.documentModel.parser.AngularRegionContext;
import org.eclipse.angularjs.internal.ui.AngularScopeHelper;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.MarkupCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tern.angular.AngularType;
import tern.angular.modules.AngularModulesManager;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveHelper;
import tern.angular.modules.DirectiveParameter;
import tern.angular.modules.DirectiveValue;
import tern.angular.modules.IDirectiveCollector;
import tern.angular.modules.IDirectiveParameterCollector;
import tern.angular.modules.Restriction;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.scriptpath.ITernScriptPath;
import tern.server.ITernServer;
import tern.server.protocol.completions.ITernCompletionCollector;

/**
 * Completion in HTML editor for :
 * 
 * <ul>
 * <li>attribute name with angular directive (ex : ng-app).</li>
 * <li>attribute value with angular module, controller, model.</li>
 * <li>attribute expression in text node {{}} and directive attribute value.</li>
 * </ul>
 * 
 */
public class HTMLAngularTagsCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	private static final String CLASS_ATTR = "class";

	@Override
	protected void addAttributeNameProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// Check if project has angular nature
		final IDOMNode element = (IDOMNode) contentAssistRequest.getNode();
		if (DOMUtils.hasAngularNature(element)) {
			IProject p = DOMUtils.getFile(element).getProject();
			Directive directive = DOMUtils.getAngularDirective(p, element);
			if (directive != null) {
				// completion for directive parameters.
				String paramName = contentAssistRequest.getMatchString();
				AngularModulesManager.getInstance().collectDirectiveParameters(
						directive, paramName,
						new IDirectiveParameterCollector() {

							@Override
							public void add(DirectiveParameter parameter) {
								addDirectiveParameter(contentAssistRequest,
										parameter, element);
							}
						});
			} else {
				// completion for directive with 'A' restriction : completion
				// for
				// attribute name with angular directive (ex :
				// ng-app)
				String tagName = element.getNodeName();
				String directiveName = contentAssistRequest.getMatchString();
				IDOMAttr attr = DOMUtils.getAttrByRegion(element,
						contentAssistRequest.getRegion());
				// get angular attribute name of the element

				final List<Directive> existingDirectives = DOMUtils
						.getAngularDirectives(p,
								element instanceof Element ? (Element) element
										: null, attr);
				AngularProject project = null;
				try {
					project = AngularProject.getAngularProject(p);
				} catch (CoreException e) {
				}
				// Starts directives completion.
				project.collectDirectives(tagName, directiveName,
						existingDirectives, Restriction.A,
						new IDirectiveCollector() {

							@Override
							public void add(Directive directive, String name) {

								// Add the directive in the completion.
								String displayString = name + " - "
										+ directive.getModule().getName();
								String additionalProposalInfo = directive
										.getHTMLDescription();
								Image image = ImageResource
										.getImage(ImageResource.IMG_DIRECTIVE);
								addProposal(contentAssistRequest, name,
										directive.getDirectiveValue(),
										directive, displayString, image,
										additionalProposalInfo, element);
							}

							@Override
							public void add(DirectiveParameter parameter) {
								addDirectiveParameter(contentAssistRequest,
										parameter, element);
							}

						});
			}
		}
		super.addAttributeNameProposals(contentAssistRequest, context);
	}

	@Override
	protected void addAttributeValueProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// Check if project has angular nature
		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();
		if (DOMUtils.hasAngularNature(element)) {
			// check if it's class attribute
			IDOMAttr attr = DOMUtils.getAttrByRegion(element,
					contentAssistRequest.getRegion());
			// is angular directive attribute?
			Directive directive = DOMUtils.getAngularDirectiveByRegion(element,
					contentAssistRequest.getRegion());
			AngularType angularType = directive != null ? directive.getType()
					: null;
			if (angularType != null) {
				if (angularType.equals(AngularType.unknown)
						|| angularType.equals(AngularType.repeat_expression))
					angularType = AngularType.model;
				int startIndex = (contentAssistRequest.getMatchString()
						.startsWith("\"") || contentAssistRequest
						.getMatchString().startsWith("'")) ? 1 : 0;
				populateAngularProposals(contentAssistRequest, element,
						angularType, startIndex);
			} else {
				// is angular expression inside attribute?
				String matchingString = contentAssistRequest.getMatchString();
				int index = matchingString.lastIndexOf("{{");
				if (index != -1) {
					populateAngularProposals(contentAssistRequest, element,
							AngularType.model, index);
				} else {
					if (CLASS_ATTR.equals(attr.getName())) {
						addClassAttributeValueProposals(contentAssistRequest,
								context, attr);
					}
				}
			}
		}
		super.addAttributeValueProposals(contentAssistRequest, context);
	}

	public void addClassAttributeValueProposals(
			final ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, IDOMAttr attr) {
		// completion on "class" attribute : completion on directive
		// with 'C' restrict.

		int documentPosition = context.getInvocationOffset();
		int length = documentPosition - contentAssistRequest.getStartOffset();
		String text = attr.getValue().substring(0, length - 1);

		final int index = text.lastIndexOf(";");
		if (index != -1) {
			text = text.substring(index + 1, text.length());
		}

		String matchingString = text.trim();
		AngularProject project = null;
		try {
			project = AngularProject.getAngularProject(DOMUtils.getFile(attr)
					.getProject());
		} catch (CoreException e) {
		}
		// Starts directives completion.
		project.collectDirectives(null, matchingString, null, Restriction.C,
				new IDirectiveCollector() {

					@Override
					public void add(Directive directive, String name) {

						// Add the directive in the completion.
						String displayString = name + " - "
								+ directive.getModule().getName();
						String additionalProposalInfo = directive
								.getHTMLDescription();
						Image image = ImageResource
								.getImage(ImageResource.IMG_DIRECTIVE);
						addProposal(contentAssistRequest, name,
								directive.getDirectiveValue(), displayString,
								image, additionalProposalInfo);
					}

					@Override
					public void add(DirectiveParameter parameter) {
						// bo nothing
					}

					private void addProposal(
							final ContentAssistRequest contentAssistRequest,
							String name, DirectiveValue directiveValue,
							String displayString, Image image,
							String additionalProposalInfo) {
						String replacementString = directiveValue == DirectiveValue.none ? name
								: name + ":";
						int replacementOffset = contentAssistRequest
								.getReplacementBeginPosition();
						int replacementLength = contentAssistRequest
								.getReplacementLength();
						if (index != -1) {
							replacementOffset += index;
							replacementLength += index;
						}
						int cursorPosition = getCursorPositionForProposedText(replacementString);

						IContextInformation contextInformation = null;

						int relevance = XMLRelevanceConstants.R_NONE;

						ICompletionProposal proposal = new CustomCompletionProposal(
								replacementString, replacementOffset,
								replacementLength, cursorPosition, image,
								displayString, contextInformation,
								additionalProposalInfo, relevance);
						contentAssistRequest.addProposal(proposal);
					}

				});
	}

	private void populateAngularProposals(
			final ContentAssistRequest contentAssistRequest, IDOMNode element,
			final AngularType angularType, final Integer startIndex) {
		IFile file = DOMUtils.getFile(element);
		IProject eclipseProject = file.getProject();
		try {
			IDETernProject ternProject = AngularProject
					.getTernProject(eclipseProject);

			// get the expression to use for Tern completion
			String expression = getExpression(contentAssistRequest, startIndex);

			final int replacementOffset = getReplacementOffset(
					contentAssistRequest, angularType,
					element.getNodeType() != Node.TEXT_NODE);
			// Create Tern doc + query
			TernAngularQuery query = new TernAngularCompletionsQuery(
					angularType);
			query.setExpression(expression);
			ITernScriptPath scriptPath = AngularScopeHelper.populateScope(
					element, file, angularType, query);

			// Execute Tern completion
			// final ITernServer ternServer = ternProject.getTernServer();
			ITernCompletionCollector collector = new ITernCompletionCollector() {

				@Override
				public void addProposal(String name, String type,
						String origin, Object doc, int pos, Object completion,
						ITernServer ternServer) {

					ICompletionProposal proposal = null;
					if (isModuleOrController(angularType)) {

						MarkupAngularCompletionProposal markupPproposal = new MarkupAngularCompletionProposal(
								name, type, origin, doc, pos, completion,
								ternServer, angularType, replacementOffset);

						// in the case of "module", "controller" completion
						// the value must replace the existing value.
						String replacementString = "\"" + name + "\"";
						int replacementLength = contentAssistRequest
								.getReplacementLength();
						int cursorPosition = getCursorPositionForProposedText(replacementString) - 2;
						markupPproposal.setReplacementString(replacementString);
						markupPproposal.setReplacementLength(replacementLength);
						markupPproposal.setCursorPosition(cursorPosition);
						markupPproposal.setReplacementOffset(replacementOffset);
						markupPproposal.setImage(getImage(angularType));
						proposal = markupPproposal;
					} else {
						proposal = new JSAngularCompletionProposal(name, type,
								origin, doc, pos, completion, ternServer,
								angularType, replacementOffset);
					}
					contentAssistRequest.addProposal(proposal);

				}
			};

			if (scriptPath != null) {
				ternProject.request(query, query.getFiles(), scriptPath,
						collector);
			} else {
				ternProject.request(query, query.getFiles(), element, file,
						collector);
			}

		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error while tern completion.", e);
		}
	}

	/**
	 * Returns the expression to use for tern completion.
	 * 
	 * @param contentAssistRequest
	 * @param startIndex
	 * @return
	 */
	private String getExpression(ContentAssistRequest contentAssistRequest,
			Integer startIndex) {
		String expression = contentAssistRequest.getMatchString();
		if (startIndex != null) {
			// start index is not null , this case comes from when completion is
			// done in attribute :
			// 1) when completion is done inside an attribute <span
			// ng-app="MyModu
			// in this case the expression to use is 'MyModu' and not
			// '"MyModu'
			// 2) when completion is done inside an
			// attribute which define {{
			// ex : <span class="done-{{to
			// in this case, the expression to use is 'to' and not
			// '"done-{{to'
			expression = expression.substring(startIndex, expression.length());
		}
		return expression;
	}

	/**
	 * Returns the replacement offset.
	 * 
	 * @param contentAssistRequest
	 * @param angularType
	 * @param isAttr
	 * @return
	 */
	private int getReplacementOffset(ContentAssistRequest contentAssistRequest,
			AngularType angularType, boolean isAttr) {
		int replacementOffset = contentAssistRequest
				.getReplacementBeginPosition();
		if (isAttr) {
			// the completion is done in an attribute.
			if (!isModuleOrController(angularType)) {
				// getReplacementBeginPosition returns the position of the
				// starts of the attribute value (or quote).
				// in the case of attribute different from "module",
				// "controller", the replacement offset must
				// be the position where completion starts (ex : ng-model="todo.
				// => the position should be after todo. and before.
				replacementOffset += contentAssistRequest.getMatchString()
						.length();
			}
		}
		return replacementOffset;
	}

	@Override
	protected ContentAssistRequest computeCompletionProposals(
			String matchString, ITextRegion completionRegion,
			IDOMNode treeNode, IDOMNode xmlnode,
			CompletionProposalInvocationContext context) {
		String regionType = completionRegion.getType();
		boolean isXMLContent = (regionType == DOMRegionContext.XML_CONTENT);
		if (regionType == AngularRegionContext.ANGULAR_EXPRESSION_OPEN
				|| regionType == AngularRegionContext.ANGULAR_EXPRESSION_CONTENT
				|| (isXMLContent && DOMUtils.hasAngularNature(xmlnode))) {

			// completion for Angular expression {{}} inside text node.
			int documentPosition = context.getInvocationOffset();
			IStructuredDocumentRegion documentRegion = ContentAssistUtils
					.getStructuredDocumentRegion(context.getViewer(),
							documentPosition);

			String match = null;
			int length = documentPosition - documentRegion.getStartOffset();
			if (isXMLContent) {
				// case for JSP
				String text = documentRegion.getText().substring(0, length);
				int startExprIndex = text.lastIndexOf("{{");
				if (startExprIndex != -1) {
					int endExprIndex = text.lastIndexOf("}}");
					if (endExprIndex == -1 || endExprIndex < startExprIndex) {
						// completion (for JSP) is done inside angular
						// expression {{
						match = text.substring(startExprIndex + 2,
								text.length());
					}
				}
			} else {
				// case for HTML where regionType is an angular expression
				// open/content.
				if (length > 1) {
					// here we have {{
					match = documentRegion.getText().substring(2, length);
				}
			}
			if (match != null) {
				ContentAssistRequest contentAssistRequest = new ContentAssistRequest(
						treeNode, treeNode.getParentNode(), documentRegion,
						completionRegion, documentPosition, 0, match);

				populateAngularProposals(contentAssistRequest, treeNode,
						AngularType.model, null);

				return contentAssistRequest;
			}
		}

		return super.computeCompletionProposals(matchString, completionRegion,
				treeNode, xmlnode, context);
	}

	@Override
	protected void addTagNameProposals(
			final ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
		if (DOMUtils.hasAngularNature(node)) {
			// completion for directive with 'E' restriction.
			String directiveName = contentAssistRequest.getMatchString();

			AngularProject project = null;
			try {
				project = AngularProject.getAngularProject(DOMUtils.getFile(
						node).getProject());
			} catch (CoreException e) {
			}
			project.collectDirectives(DirectiveHelper.ANY_TAG, directiveName,
					null, Restriction.E, new IDirectiveCollector() {

						@Override
						public void add(Directive directive, String name) {

							// Add the directive in the completion.
							String displayString = name + " - "
									+ directive.getModule().getName();
							String additionalProposalInfo = directive
									.getHTMLDescription();
							Image image = ImageResource
									.getImage(ImageResource.IMG_DIRECTIVE);

							addProposal(contentAssistRequest, directive, name,
									displayString, image,
									additionalProposalInfo);

						}

						@Override
						public void add(DirectiveParameter parameter) {
							// do nothing
						}

						private void addProposal(
								final ContentAssistRequest contentAssistRequest,
								Directive directive, String name,
								String displayString, Image image,
								String additionalProposalInfo) {
							StringBuilder replacementString = new StringBuilder(
									"");
							replacementString.append(name);

							Collection<DirectiveParameter> parameters = directive
									.getParameters();
							for (DirectiveParameter parameter : parameters) {
								if (!parameter.isOptionnal()) {
									replacementString.append(" ")
											.append(parameter.getName())
											.append("=\"\"");
								}
							}

							replacementString.append(">");
							replacementString.append("</");
							replacementString.append(name);
							replacementString.append(">");

							int replacementOffset = contentAssistRequest
									.getReplacementBeginPosition();
							int replacementLength = contentAssistRequest
									.getReplacementLength();
							int cursorPosition = getCursorPositionForProposedText(replacementString
									.toString());

							IContextInformation contextInformation = null;

							int relevance = XMLRelevanceConstants.R_TAG_NAME;

							ICompletionProposal proposal = new MarkupCompletionProposal(
									replacementString.toString(),
									replacementOffset, replacementLength,
									cursorPosition, image, displayString,
									contextInformation, additionalProposalInfo,
									relevance);
							contentAssistRequest.addProposal(proposal);
						}

					});
		}
		super.addTagNameProposals(contentAssistRequest, childPosition, context);
	}

	/**
	 * Returns true if the given angular type is module or controller and false
	 * otherwise.
	 * 
	 * @param angularType
	 * @return
	 */
	private boolean isModuleOrController(final AngularType angularType) {
		return angularType == AngularType.module
				|| angularType == AngularType.controller;
	}

	public void addDirectiveParameter(
			final ContentAssistRequest contentAssistRequest,
			DirectiveParameter parameter, IDOMNode element) {
		// Add the directive parameter in the
		// completion.
		if (hasParameterAttribute(parameter, element)) {
			return;
		}

		Directive directive = parameter.getDirective();
		String displayString = parameter.getName() + " - "
				+ directive.getModule().getName() + "#" + directive.getName();
		String additionalProposalInfo = parameter.getHTMLDescription();
		Image image = ImageResource.getImage(ImageResource.IMG_DIRECTIVE_PARAM);
		addProposal(contentAssistRequest, parameter.getName(),
				DirectiveValue.required, null, displayString, image,
				additionalProposalInfo, element);
	}

	public boolean hasParameterAttribute(DirectiveParameter parameter,
			IDOMNode element) {
		if (element.getNodeType() == Node.ELEMENT_NODE
				&& element instanceof Element
				&& ((Element) element).hasAttribute(parameter.getName())) {
			// the attribute alrady exists, ignore it.
			return true;
		}
		return false;
	}

	private void addProposal(final ContentAssistRequest contentAssistRequest,
			String name, DirectiveValue directiveValue, Directive directive,
			String displayString, Image image, String additionalProposalInfo,
			IDOMNode element) {
		StringBuilder replacementString = new StringBuilder(name);
		if (directiveValue != DirectiveValue.none)
			replacementString.append("=\"\"");

		if (directive != null) {
			Collection<DirectiveParameter> parameters = directive
					.getParameters();
			for (DirectiveParameter parameter : parameters) {
				if (!parameter.isOptionnal()
						&& !hasParameterAttribute(parameter, element)) {
					replacementString.append(" ").append(parameter.getName())
							.append("=\"\"");
				}
			}
		}

		int replacementOffset = contentAssistRequest
				.getReplacementBeginPosition();
		int replacementLength = contentAssistRequest.getReplacementLength();
		int cursorPosition = getCursorPositionForProposedText(replacementString
				.toString());

		IContextInformation contextInformation = null;

		int relevance = XMLRelevanceConstants.R_XML_ATTRIBUTE_NAME;

		ICompletionProposal proposal = new CustomCompletionProposal(
				replacementString.toString(), replacementOffset,
				replacementLength, cursorPosition, image, displayString,
				contextInformation, additionalProposalInfo, relevance);
		contentAssistRequest.addProposal(proposal);
	}

	/**
	 * Returns the image to use for completion according to teh given angular
	 * type.
	 * 
	 * @param angularType
	 * @return
	 */
	private static Image getImage(AngularType angularType) {
		switch (angularType) {
		case module:
			return ImageResource.getImage(ImageResource.IMG_ANGULARJS);
		case controller:
			return ImageResource.getImage(ImageResource.IMG_CONTROLLER);
		default:
			return null;
		}
	}

}
