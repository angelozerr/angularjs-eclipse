package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

public class TernHyperlink implements IHyperlink {

	private final IDOMAttr attr;
	private final String file;
	private final Long start;
	private final Long end;

	public TernHyperlink(IDOMAttr attr, String file, Long start, Long end) {
		this.attr = attr;
		this.file = file;
		this.start = start;
		this.end = end;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return HyperlinkUtils.getHyperlinkRegion(attr);
	}

	@Override
	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return "labellll";
	}

	@Override
	public String getHyperlinkText() {
		// TODO Auto-generated method stub
		return "texxxt";
	}

	@Override
	public void open() {
		IFile file = DOMUtils.getFile(attr).getProject().getFile(this.file);
		if (file.exists()) {
			EditorUtils.openInEditor(file, start.intValue(), end.intValue()
					- start.intValue(), true);
		}
	}

}
