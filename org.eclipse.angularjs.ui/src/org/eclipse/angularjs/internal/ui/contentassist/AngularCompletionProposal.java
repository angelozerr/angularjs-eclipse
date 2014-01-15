package org.eclipse.angularjs.internal.ui.contentassist;

import org.eclipse.angularjs.core.utils.StringUtils;

import tern.angular.AngularType;
import tern.eclipse.jface.contentassist.TernCompletionProposal;
import tern.server.ITernServer;

/**
 * Extrends Tern compeltion proposal for retrieve "module" and "controller"
 * information from the tern completion.
 * 
 * @author azerr
 * 
 */
public class AngularCompletionProposal extends TernCompletionProposal {

	private final ITernServer ternServer;
	private final Object completion;

	public AngularCompletionProposal(String name, String type, String origin,
			Object doc, int pos, Object completion, ITernServer server,
			AngularType angularType, int startOffset) {
		super(name, type, origin, doc, pos, startOffset);
		this.ternServer = server;
		this.completion = completion;
	}

	@Override
	public String getAdditionalProposalInfo() {
		String module = ternServer.getText(completion, "module");
		String controller = ternServer.getText(completion, "controller");
		StringBuilder s = null;
		if (module != null) {
			s = new StringBuilder("");
			s.append("<b>Module</b>:");
			s.append(module);
		}
		if (controller != null) {
			if (s == null) {
				s = new StringBuilder("");
			} else {
				s.append("<br>");
			}
			s.append("<b>Controller</b>:");
			s.append(controller);
		}
		String origin = super.getOrigin();
		if (!StringUtils.isEmpty(origin)) {
			if (s == null) {
				s = new StringBuilder("");
			} else {
				s.append("<br>");
			}
			s.append("<b>Origin</b>:");
			s.append(origin);
		}
		String doc = super.getAdditionalProposalInfo();
		if (s != null && doc != null) {
			s.append("<br>");
			s.append("<b>Documentation</b>:");
			s.append("<br>");
			s.append(doc);
		}		
		return s != null ? s.toString() : doc;
	}

}
