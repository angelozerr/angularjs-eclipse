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
package org.eclipse.angularjs.ui.contentassist;

import java.util.Collection;
import java.util.List;

import org.eclipse.angularjs.core.AngularProject;
import org.eclipse.angularjs.core.utils.AngularDOMUtils;
import org.eclipse.angularjs.core.utils.AngularELRegion;
import org.eclipse.angularjs.core.utils.AngularRegionUtils;
import org.eclipse.angularjs.core.utils.AngularScopeHelper;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.internal.ui.ImageResource;
import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.angularjs.internal.ui.contentassist.AngularMarkupCompletionProposal;
import org.eclipse.angularjs.internal.ui.contentassist.HTMLAngularCompletionProposal;
import org.eclipse.angularjs.internal.ui.contentassist.JSAngularCompletionProposal;
import org.eclipse.angularjs.internal.ui.contentassist.MarkupAngularCompletionProposal;
import org.eclipse.angularjs.internal.ui.utils.DOMUIUtils;
import org.eclipse.angularjs.internal.ui.utils.HTMLAngularPrinter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tern.ITernFile;
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
import tern.angular.protocol.completions.AngularCompletionProposalRec;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.core.resources.TernDocumentFile;
import tern.scriptpath.ITernScriptPath;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;

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
		if (AngularDOMUtils.hasAngularNature(element)) {
			IProject p = DOMUtils.getFile(element).getProject();
			Directive directive = AngularDOMUtils.getAngularDirective(p,
					element);
			if (directive != null) {
				try {
					final IIDETernProject ternProject = TernCorePlugin
							.getTernProject(p);

					// completion for directive parameters.
					String paramName = contentAssistRequest.getMatchString();
					AngularModulesManager.getInstance()
							.collectDirectiveParameters(directive, paramName,
									new IDirectiveParameterCollector() {

										@Override
										public void add(
												DirectiveParameter parameter) {
											addDirectiveParameter(
													contentAssistRequest,
													parameter, element,
													ternProject);
										}
									});
				} catch (CoreException e) {
					e.printStackTrace();
				}
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

				final List<Directive> existingDirectives = AngularDOMUtils
						.getAngularDirectives(p,
								element instanceof Element ? (Element) element
										: null, attr);

				try {
					AngularProject angularProject = AngularProject
							.getAngularProject(p);
					final IIDETernProject ternProject = TernCorePlugin
							.getTernProject(p);
					// Starts directives completion.
					angularProject.collectDirectives(tagName, directiveName,
							existingDirectives, Restriction.A,
							new IDirectiveCollector() {

								@Override
								public void add(Directive directive, String name) {

									// Add the directive in the completion.
									String displayString = name + " - "
											+ directive.getModule().getName();
									String additionalProposalInfo = HTMLAngularPrinter
											.getDirectiveInfo(directive);
									Image image = ImageResource
											.getImage(ImageResource.IMG_DIRECTIVE);
									addProposal(contentAssistRequest, name,
											directive.getDirectiveValue(),
											directive, displayString, image,
											additionalProposalInfo, element,
											ternProject);
								}

								@Override
								public void add(DirectiveParameter parameter) {
									addDirectiveParameter(contentAssistRequest,
											parameter, element, ternProject);
								}

							});
				} catch (CoreException e) {
				}
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
		if (AngularDOMUtils.hasAngularNature(element)) {
			// check if it's class attribute
			IDOMAttr attr = DOMUtils.getAttrByRegion(element,
					contentAssistRequest.getRegion());
			// is angular directive attribute?
			Directive directive = AngularDOMUtils.getAngularDirectiveByRegion(
					element, contentAssistRequest.getRegion());
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
						context.getDocument(), angularType, startIndex);
			} else {
				// is angular expression inside attribute?
				String matchingString = contentAssistRequest.getMatchString();
				String startSymbol = AngularProject.DEFAULT_START_SYMBOL;
				try {
					AngularProject angularProject = AngularProject
							.getAngularProject(DOMUtils.getFile(element)
									.getProject());
					startSymbol = angularProject.getStartSymbol();
				} catch (CoreException e) {
				}
				int index = matchingString.lastIndexOf(startSymbol);
				if (index != -1) {
					populateAngularProposals(contentAssistRequest, element,
							context.getDocument(), AngularType.model, index);
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
		try {
			IProject project = DOMUtils.getFile(attr).getProject();
			AngularProject angularProject = AngularProject
					.getAngularProject(project);
			final IIDETernProject ternProject = TernCorePlugin
					.getTernProject(project);
			// Starts directives completion.
			angularProject.collectDirectives(null, matchingString, null,
					Restriction.C, new IDirectiveCollector() {

						@Override
						public void add(Directive directive, String name) {

							// Add the directive in the completion.
							String displayString = name + " - "
									+ directive.getModule().getName();
							String additionalProposalInfo = HTMLAngularPrinter
									.getDirectiveInfo(directive);
							Image image = ImageResource
									.getImage(ImageResource.IMG_DIRECTIVE);
							addProposal(contentAssistRequest, name,
									directive.getDirectiveValue(),
									displayString, image,
									additionalProposalInfo);
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

							ICompletionProposal proposal = new HTMLAngularCompletionProposal(
									replacementString, replacementOffset,
									replacementLength, cursorPosition, image,
									displayString, contextInformation,
									additionalProposalInfo, relevance,
									ternProject);
							contentAssistRequest.addProposal(proposal);
						}

					});
		} catch (CoreException e) {
		}
	}

	private void populateAngularProposals(
			final ContentAssistRequest contentAssistRequest, IDOMNode element,
			IDocument document, final AngularType angularType,
			final Integer startIndex) {
		IFile file = DOMUtils.getFile(element);
		IProject eclipseProject = file.getProject();
		try {
			IIDETernProject ternProject = AngularProject
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
				public void addProposal(TernCompletionProposalRec proposalItem,
						Object completion, IJSONObjectHelper jsonObjectHelper) {
					ICompletionProposal proposal = null;
					if (isModuleOrController(angularType)) {

						MarkupAngularCompletionProposal markupPproposal = new MarkupAngularCompletionProposal(
								new AngularCompletionProposalRec(proposalItem,
										replacementOffset), completion,
								jsonObjectHelper, angularType);

						// in the case of "module", "controller" completion
						// the value must replace the existing value.
						String replacementString = "\"" + proposalItem.name
								+ "\"";
						int replacementLength = contentAssistRequest
								.getReplacementLength();
						int cursorPosition = getCursorPositionForProposedText(replacementString) - 2;
						markupPproposal.setReplacementString(replacementString);
						markupPproposal.setReplacementLength(replacementLength);
						markupPproposal.setCursorPosition(cursorPosition);
						markupPproposal.setReplacementOffset(replacementOffset);
						markupPproposal.setImage(HTMLAngularPrinter
								.getImage(angularType));
						proposal = markupPproposal;
					} else {
						proposal = new JSAngularCompletionProposal(
								new AngularCompletionProposalRec(
										proposalItem,
										replacementOffset
												- (proposalItem.end - proposalItem.start)),
								completion, jsonObjectHelper, angularType);
					}
					contentAssistRequest.addProposal(proposal);

				}
			};

			if (scriptPath != null) {
				ternProject.request(query, query.getFiles(), scriptPath, null,
						null, collector);
			} else {
				ITernFile tf = new TernDocumentFile(file, document);
				ternProject.request(query, query.getFiles(), null, element, tf,
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
		if (isXMLContent && AngularDOMUtils.hasAngularNature(xmlnode)) {

			// completion for Angular expression {{}} inside text node.
			int documentPosition = context.getInvocationOffset();
			IStructuredDocumentRegion documentRegion = DOMUIUtils
					.getStructuredDocumentRegion(context.getViewer(),
							documentPosition);

			String match = null;
			AngularELRegion angularRegion = AngularRegionUtils
					.getAngularELRegion(documentRegion, documentPosition,
							DOMUtils.getFile(treeNode).getProject());
			if (angularRegion != null) {
				match = angularRegion.getExpression().substring(0,
						angularRegion.getExpressionOffset());
			}
			if (match != null) {
				ContentAssistRequest contentAssistRequest = new ContentAssistRequest(
						treeNode, treeNode.getParentNode(), documentRegion,
						completionRegion, documentPosition, 0, match);

				populateAngularProposals(contentAssistRequest, treeNode,
						context.getDocument(), AngularType.model, null);

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
		if (AngularDOMUtils.hasAngularNature(node)) {
			// completion for directive with 'E' restriction.
			String directiveName = contentAssistRequest.getMatchString();

			try {
				IProject p = DOMUtils.getFile(node).getProject();
				AngularProject project = AngularProject.getAngularProject(p);
				final IIDETernProject ternProject = project.getTernProject(p);

				project.collectDirectives(DirectiveHelper.ANY_TAG,
						directiveName, null, Restriction.E,
						new IDirectiveCollector() {

							@Override
							public void add(Directive directive, String name) {

								// Add the directive in the completion.
								String displayString = name + " - "
										+ directive.getModule().getName();
								String additionalProposalInfo = HTMLAngularPrinter
										.getDirectiveInfo(directive);
								Image image = ImageResource
										.getImage(ImageResource.IMG_DIRECTIVE);

								addProposal(contentAssistRequest, directive,
										name, displayString, image,
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

								ICompletionProposal proposal = new AngularMarkupCompletionProposal(
										replacementString.toString(),
										replacementOffset, replacementLength,
										cursorPosition, image, displayString,
										contextInformation,
										additionalProposalInfo, relevance,
										ternProject);
								contentAssistRequest.addProposal(proposal);
							}

						});
			} catch (CoreException e) {
			}

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
			DirectiveParameter parameter, IDOMNode element,
			IIDETernProject ternProject) {
		// Add the directive parameter in the
		// completion.
		if (hasParameterAttribute(parameter, element)) {
			return;
		}

		Directive directive = parameter.getDirective();
		String displayString = parameter.getName() + " - "
				+ directive.getModule().getName() + "#" + directive.getName();
		String additionalProposalInfo = HTMLAngularPrinter
				.getDirectiveParameterInfo(parameter);
		Image image = ImageResource.getImage(ImageResource.IMG_DIRECTIVE_PARAM);
		addProposal(contentAssistRequest, parameter.getName(),
				DirectiveValue.required, null, displayString, image,
				additionalProposalInfo, element, ternProject);
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
			IDOMNode element, IIDETernProject ternProject) {
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

		ICompletionProposal proposal = new HTMLAngularCompletionProposal(
				replacementString.toString(), replacementOffset,
				replacementLength, cursorPosition, image, displayString,
				contextInformation, additionalProposalInfo, relevance,
				ternProject);
		contentAssistRequest.addProposal(proposal);
	}

}
