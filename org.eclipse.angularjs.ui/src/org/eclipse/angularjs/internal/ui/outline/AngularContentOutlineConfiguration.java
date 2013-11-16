package org.eclipse.angularjs.internal.ui.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;

public class AngularContentOutlineConfiguration extends
		HTMLContentOutlineConfiguration {

	private IContentProvider fContentProviderHTML;

	public IContentProvider getContentProvider(final TreeViewer viewer) {
		/*
		 * if (MODE_PHP == mode) { if (fContentProvider == null) {
		 * fContentProvider = new PHPOutlineContentProvider(viewer); }
		 * viewer.setContentProvider(fContentProvider); } else if (MODE_HTML ==
		 * mode) {
		 */
		if (fContentProviderHTML == null) {
			fContentProviderHTML = new JFaceNodeContentProvider() {
				public Object[] getElements(Object object) {
					/*
					 * if (object instanceof ISourceModule) { IEditorPart
					 * activeEditor = PHPUiPlugin .getActiveEditor(); if
					 * (activeEditor instanceof StructuredTextEditor) {
					 * StructuredTextEditor editor = (StructuredTextEditor)
					 * activeEditor; IDocument document = editor
					 * .getDocumentProvider().getDocument(
					 * editor.getEditorInput()); IStructuredModel model = null;
					 * try { model = StructuredModelManager .getModelManager()
					 * .getExistingModelForRead(document); return
					 * super.getElements(model); } finally { if (model != null)
					 * { model.releaseFromRead(); } } } }
					 */
					return super.getElements(object);
				}

				@Override
				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					/*
					 * if (newInput instanceof ISourceModule) { IEditorPart
					 * activeEditor = PHPUiPlugin .getActiveEditor(); if
					 * (activeEditor instanceof StructuredTextEditor) {
					 * StructuredTextEditor editor = (StructuredTextEditor)
					 * activeEditor; IDocument document = editor
					 * .getDocumentProvider().getDocument(
					 * editor.getEditorInput()); IStructuredModel model = null;
					 * try { model = StructuredModelManager .getModelManager()
					 * .getExistingModelForRead(document); } finally { if (model
					 * != null) { model.releaseFromRead(); } } newInput = model;
					 * } }
					 */
					super.inputChanged(viewer, oldInput, newInput);
				}
			};
		}
		viewer.setContentProvider(fContentProviderHTML);
		// }
		return viewer.getContentProvider();
	}
}
