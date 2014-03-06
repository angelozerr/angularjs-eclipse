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

import org.eclipse.angularjs.internal.ui.Trace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalReporter;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public abstract class AbstractValidator implements IValidator, ISourceValidator {

	private IDocument fDocument;

	private void setDocument(IDocument doc) {
		fDocument = doc;
	}

	protected IDocument getDocument() {
		return fDocument;
	}

	public void connect(IDocument document) {
		setDocument(document);
	}

	public void disconnect(IDocument document) {
		setDocument(null);
	}

	public void cleanup(IReporter reporter) {

	}

	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		if (getDocument() == null) {
			return;
		}
		if (!(reporter instanceof IncrementalReporter)) {
			return;
		}
		if (!(getDocument() instanceof IStructuredDocument)) {
			return;
		}

		// remove old messages
		reporter.removeAllMessages(this);

		IFile file = null;
		String[] delta = helper.getURIs();
		if (delta.length > 0) {
			// get the file, model and document:
			file = getFile(delta[0]);
		}

		IStructuredDocumentRegion[] regions = ((IStructuredDocument) fDocument)
				.getStructuredDocumentRegions();
		validate(reporter, file, regions);

	}

	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
		if (getDocument() == null) {
			return;
		}
		if (!(reporter instanceof IncrementalReporter)) {
			return;
		}
		if (!(getDocument() instanceof IStructuredDocument)) {
			return;
		}

		// remove old messages
		reporter.removeAllMessages(this);

		IFile file = null;
		String[] delta = helper.getURIs();
		if (delta.length > 0) {
			// get the file, model and document:
			file = getFile(delta[0]);
		}

		IStructuredDocumentRegion[] regions = ((IStructuredDocument) fDocument)
				.getStructuredDocumentRegions(dirtyRegion.getOffset(),
						dirtyRegion.getLength());

		validate(reporter, file, regions);
	}

	private void validate(IReporter reporter, IFile file,
			IStructuredDocumentRegion[] regions) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (model == null) {
				model = StructuredModelManager.getModelManager()
						.getModelForRead(file);
			}
			if (model != null) {

				for (int i = 0; i < regions.length; i++) {
					validate(regions[i], reporter, file, model);
				}
			}
		} catch (Throwable e) {
			Trace.trace(Trace.SEVERE, e.getMessage(), e);
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private void validate(IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model) {

		if (structuredDocumentRegion == null) {
			return;
		}

		doValidate(structuredDocumentRegion, reporter, file, model);
	}

	protected abstract void doValidate(
			IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter, IFile file, IStructuredModel model);

	private IFile getFile(String delta) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(delta));
		if (file != null && file.exists())
			return file;
		return null;
	}
}
