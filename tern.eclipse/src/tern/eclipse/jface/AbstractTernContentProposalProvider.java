package tern.eclipse.jface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import tern.TernException;
import tern.doc.IJSDocument;
import tern.server.DefaultResponseHandler;
import tern.server.ITernServer;

public abstract class AbstractTernContentProposalProvider implements
		IContentProposalProvider {

	private static final IContentProposal[] EMPTY = new IContentProposal[0];

	private final IJSDocument document;

	public AbstractTernContentProposalProvider(IJSDocument document) {
		this.document = document;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		ITernServer server = document.getServer();
		DefaultResponseHandler response = new DefaultResponseHandler();
		server.requestCompletion(document, response, true);
		try {
			List<IContentProposal> proposals = new ArrayList<IContentProposal>();
			// ex:
			// {"start":{"line":1,"ch":0},"end":{"line":1,"ch":1},"completions":[{"name":"a","type":"[?]"}]}

			populateCompletions(response.getData(), proposals);
			return proposals.toArray(EMPTY);
		} catch (TernException e) {
			e.printStackTrace();
		}
		return EMPTY;
	}

	protected abstract void populateCompletions(Object data,
			List<IContentProposal> proposals);
}
