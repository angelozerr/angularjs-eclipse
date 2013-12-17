package org.eclipse.angularjs.internal.ui.hyperlink;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

import tern.server.protocol.definition.ITernDefinitionCollector;

public class TernHyperlinkCollector implements ITernDefinitionCollector {

	private final IDOMAttr attr;
	private IHyperlink hyperlink;

	public TernHyperlinkCollector(IDOMAttr attr) {
		this.attr = attr;
	}

	@Override
	public void setDefinition(String file, Long start, Long end) {
		this.hyperlink = new TernHyperlink(attr, file, start, end);
	}

	public IHyperlink getHyperlink() {
		return hyperlink;
	}

}
