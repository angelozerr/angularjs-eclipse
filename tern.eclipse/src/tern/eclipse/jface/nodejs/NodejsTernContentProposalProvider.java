package tern.eclipse.jface.nodejs;

import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.json.simple.JSONObject;

import tern.doc.IJSDocument;
import tern.eclipse.jface.AbstractTernContentProposalProvider;

public class NodejsTernContentProposalProvider extends
		AbstractTernContentProposalProvider {

	public NodejsTernContentProposalProvider(IJSDocument document) {
		super(document);
	}

	@Override
	protected void populateCompletions(Object data,
			List<IContentProposal> proposals) {
		JSONObject jsonObject = (JSONObject) data;
		if (jsonObject != null) {
			Long startCh = getCh(jsonObject, "start");
			Long endCh = getCh(jsonObject, "end");
			int pos = endCh.intValue() - startCh.intValue();
			List completions = (List) jsonObject.get("completions");
			for (Object object : completions) {
				proposals.add(createProposal((JSONObject) object, pos));
			}
		}
	}

	private long getCh(JSONObject data, String pos) {
		JSONObject loc = (JSONObject) data.get(pos);
		return (Long) loc.get("ch");
	}

	protected IContentProposal createProposal(JSONObject completion, int pos) {
		String name = completion.get("name").toString();
		String type = completion.get("type").toString();
		Object doc = completion.get("doc");
		return new ContentProposal(name.substring(pos, name.length()), name,
				name + " - " + type + "\n" + doc);
	}

}
