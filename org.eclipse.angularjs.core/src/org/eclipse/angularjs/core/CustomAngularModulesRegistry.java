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

import tern.angular.AngularType;
import tern.angular.modules.AbstractAngularModulesRegistry;
import tern.angular.modules.Directive;
import tern.angular.modules.IDirectiveCollector;
import tern.angular.modules.Module;
import tern.angular.modules.UseAs;
import tern.angular.protocol.completions.TernAngularCompletionsQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.core.utils.FileUtils;
import tern.server.ITernServer;
import tern.server.protocol.completions.ITernCompletionCollector;

public class CustomAngularModulesRegistry extends
		AbstractAngularModulesRegistry implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private final IProject project;
	private boolean refreshDirectives;
	private final Object lock = new Object();

	public CustomAngularModulesRegistry(IProject project) {
		this.project = project;
		this.refreshDirectives = true;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public Directive getDirective(String tagName, String name) {
		refreshIfNeeded();
		return super.getDirective(tagName, name);
	}

	@Override
	public void collectDirectives(String tagName, String directiveName,
			boolean fullMatch, List<Directive> existingDirectives,
			IDirectiveCollector collector) {
		refreshIfNeeded();
		super.collectDirectives(tagName, directiveName, fullMatch,
				existingDirectives, collector);
	}

	protected void refreshIfNeeded() {
		try {
			if (!refreshDirectives) {
				return;
			}
			synchronized (lock) {
				super.clear();
				IDETernProject ternProject = IDETernProject
						.getTernProject(project);
				TernAngularCompletionsQuery query = new TernAngularCompletionsQuery(
						AngularType.directives);
				query.setExpression("");
				ternProject.request(query, query.getFiles(),
						new ITernCompletionCollector() {

							@Override
							public void addProposal(String name, String type,
									String origin, Object doc, int pos,
									Object completion, ITernServer ternServer) {
								String moduleName = ternServer.getText(
										completion, "module");
								if (!StringUtils.isEmpty(moduleName)) {
									tern.angular.modules.Module module = CustomAngularModulesRegistry.this
											.getModule(moduleName);
									if (module == null) {
										module = new Module(moduleName);
										addModule(module);
									}

									List<String> tagsName = new ArrayList<String>();
									List<UseAs> useAs = new ArrayList<UseAs>();
									new Directive(name, AngularType.model,
											null, tagsName, useAs, true, module);
								}

							}
						});
				refreshDirectives = false;
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING,
					"Error while refrsh custom angular directives.", e);
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
			if (!(project.isAccessible())) {
				return false;
			}
			if (!AngularProject.hasAngularNature(project)) {
				return false;
			}
			return true;

		case IResource.FILE:
			if (FileUtils.isJSFile(resource) || FileUtils.isHTMLFile(resource)
					|| FileUtils.isJSPFile(resource)
					|| FileUtils.isPHPFile(resource)) {
				this.refreshDirectives = true;
			}
			return true;
		}
		return false;
	}
}
