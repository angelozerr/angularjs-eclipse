package tern.eclipse.jface.rhino;

import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.mozilla.javascript.NativeObject;

import tern.doc.IJSDocument;
import tern.eclipse.jface.AbstractTernContentProposalProvider;

public class RhinoTernContentProposalProvider extends
		AbstractTernContentProposalProvider {

	public RhinoTernContentProposalProvider(IJSDocument document) {
		super(document);
	}

	@Override
	protected void populateCompletions(Object data,
			List<IContentProposal> proposals) {
		NativeObject rhinoObject = (NativeObject) data;
		if (rhinoObject != null) {
			Double startCh = getCh(rhinoObject, "start");
			Double endCh = getCh(rhinoObject, "end");
			int pos = endCh.intValue() - startCh.intValue();
			List completions = (List) rhinoObject.get("completions",
					rhinoObject);
			for (Object object : completions) {
				proposals.add(createProposal((NativeObject) object, pos));
			}
		}
	}

	private Double getCh(NativeObject data, String pos) {
		NativeObject loc = (NativeObject) data.get(pos, data);
		return (Double) loc.get("ch", loc);
	}

	protected IContentProposal createProposal(NativeObject completion, int pos) {
		String name = completion.get("name", completion).toString();
		String type = completion.get("type", completion).toString();
		return new ContentProposal(name.substring(pos, name.length()), name,
				name + " - " + type);
	}

}
