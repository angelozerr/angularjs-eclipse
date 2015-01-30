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
package org.eclipse.angularjs.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.angularjs.core.utils.StringUtils;
import org.eclipse.angularjs.internal.core.Trace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import tern.TernResourcesManager;
import tern.angular.AngularType;
import tern.angular.modules.AbstractAngularModulesRegistry;
import tern.angular.modules.Directive;
import tern.angular.modules.DirectiveValue;
import tern.angular.modules.IDirectiveCollector;
import tern.angular.modules.IDirectiveSyntax;
import tern.angular.modules.Module;
import tern.angular.modules.Restriction;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;

public class CustomAngularModulesRegistry extends
		AbstractAngularModulesRegistry implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private final IProject project;
	private boolean refreshDirectives;
	private final Object lock = new Object();

	public CustomAngularModulesRegistry(IProject project) {
		this.project = project;
		clear();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public Directive getDirective(String tagName, String name,
			Restriction restriction) {
		refreshIfNeeded();
		return super.getDirective(tagName, name, restriction);
	}

	@Override
	public void collectDirectives(String tagName, String directiveName,
			IDirectiveSyntax syntax, List<Directive> existingDirectives,
			Restriction restriction, IDirectiveCollector collector) {
		refreshIfNeeded();
		super.collectDirectives(tagName, directiveName, syntax,
				existingDirectives, restriction, collector);
	}

	protected void refreshIfNeeded() {
		try {
			if (!refreshDirectives) {
				return;
			}
			synchronized (lock) {
				if (!refreshDirectives) {
					return;
				}
				super.clear();
				IIDETernProject ternProject = TernCorePlugin
						.getTernProject(project);
				TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
						AngularType.directives);
				query.setExpression("");
				ternProject.request(query, query.getFiles(), null, null, null,
						new ITernCompletionCollector() {

							@Override
							public void addProposal(
									TernCompletionProposalRec proposal,
									Object completion,
									IJSONObjectHelper jsonObjectHelper) {
								String moduleName = jsonObjectHelper.getText(
										completion, "module");
								if (!StringUtils.isEmpty(moduleName)) {
									tern.angular.modules.Module module = CustomAngularModulesRegistry.this
											.getModule(moduleName);
									if (module == null) {
										module = new Module(moduleName);
										addModule(module);
									}

									List<String> tagsName = new ArrayList<String>();
									String restrict = jsonObjectHelper.getText(
											completion, "restrict");
									DirectiveValue directiveValue = DirectiveValue.none;
									new Directive(proposal.name,
											AngularType.model, null, tagsName,
											restrict, directiveValue, module);
								}

							}
						});
				refreshDirectives = false;
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING,
					"Error while refresh custom angular directives.", e);
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				delta.accept(this);
			}
		} catch (Throwable e) {
			Trace.trace(Trace.SEVERE, "", e);
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource == null) {
			return false;
		}
		switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.FOLDER:
			return true;
		case IResource.PROJECT:
			IProject project = (IProject) resource;
			return this.project.equals(project);
		case IResource.FILE:
			if (TernResourcesManager.isJSFile(resource.getName())
					|| TernResourcesManager.isHTMLFile(resource)) {
				clear();
			}
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		super.clear();
		this.refreshDirectives = true;
	}
}
