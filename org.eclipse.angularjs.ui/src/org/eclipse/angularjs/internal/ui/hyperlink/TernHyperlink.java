package org.eclipse.angularjs.internal.ui.hyperlink;

import java.io.IOException;

import org.eclipse.angularjs.core.DOMSSEDirectiveProvider;
import org.eclipse.angularjs.core.utils.DOMUtils;
import org.eclipse.angularjs.core.utils.HyperlinkUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import tern.angular.AngularType;
import tern.angular.protocol.HTMLTernAngularHelper;
import tern.angular.protocol.TernAngularQuery;
import tern.angular.protocol.definition.TernAngularDefinitionQuery;
import tern.eclipse.ide.core.IDETernProject;
import tern.eclipse.ide.ui.hyperlink.AbstractTernHyperlink;
import tern.eclipse.ide.ui.utils.EditorUtils;
import tern.server.protocol.TernDoc;

public class TernHyperlink extends AbstractTernHyperlink {

	private final IDOMAttr attr;
	private final IFile file;
	private final AngularType angularType;

	public TernHyperlink(IDOMAttr attr, IFile file, IDETernProject ternProject,
			AngularType angularType) {
		super(HyperlinkUtils.getHyperlinkRegion(attr), ternProject);
		this.attr = attr;
		this.file = file;
		this.angularType = angularType;
	}

	@Override
	public String getHyperlinkText() {
		// TODO Auto-generated method stub
		return "todo";
	}

	@Override
	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return "todo";
	}

	@Override
	protected TernDoc createDoc() throws Exception {
		TernAngularQuery query = new TernAngularDefinitionQuery(angularType);
		query.setExpression(attr.getValue());

		TernDoc doc = HTMLTernAngularHelper.createDoc(
				(IDOMNode) attr.getOwnerElement(),
				DOMSSEDirectiveProvider.getInstance(), file,
				ternProject.getFileManager(), query);
		return doc;
	}

	/*
	 * private final IDOMAttr attr; private final String file; private final
	 * Long start; private final Long end;
	 */

	/*
	 * public TernHyperlink(IDOMAttr attr, String file, Long start, Long end) {
	 * this.attr = attr; this.file = file; this.start = start; this.end = end; }
	 */

	// @Override
	// public IRegion getHyperlinkRegion() {
	// return HyperlinkUtils.getHyperlinkRegion(attr);
	// }
	//
	// @Override
	// public String getTypeLabel() {
	// // TODO Auto-generated method stub
	// return "labellll";
	// }
	//
	// @Override
	// public String getHyperlinkText() {
	// // TODO Auto-generated method stub
	// return "texxxt";
	// }
	//
	// @Override
	// public void open() {
	// IFile file = DOMUtils.getFile(attr).getProject().getFile(this.file);
	// if (file.exists()) {
	// EditorUtils.openInEditor(file, start.intValue(), end.intValue()
	// - start.intValue(), true);
	// }
	// }

}
