package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.internal.ui.AngularUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorUtils {

	public static IEditorPart openInEditor(IFile file, int start, int length,
			boolean activate) {
		IEditorPart editor = null;
		IWorkbenchPage page = AngularUIPlugin.getActivePage();
		try {
			if (start > 0) {
				editor = IDE.openEditor(page, file, activate);
				ITextEditor textEditor = null;
				if (editor instanceof ITextEditor)
					textEditor = (ITextEditor) editor;
				else if (editor instanceof IAdaptable)
					textEditor = (ITextEditor) editor
							.getAdapter(ITextEditor.class);
				if (textEditor != null) {
					IDocument document = textEditor.getDocumentProvider()
							.getDocument(editor.getEditorInput());
					// int start = document.getLineOffset(line - 1);
					textEditor.selectAndReveal(start, length);
					page.activate(editor);
				} else {
					IMarker marker = file
							.createMarker("org.eclipse.core.resources.textmarker");
					marker.setAttribute("lineNumber", start);
					editor = IDE.openEditor(page, marker, activate);
					marker.delete();
				}
			} else {
				editor = IDE.openEditor(page, file, activate);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return editor;
	}

}
