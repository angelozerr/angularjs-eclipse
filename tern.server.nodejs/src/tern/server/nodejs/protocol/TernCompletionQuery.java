package tern.server.nodejs.protocol;

public class TernCompletionQuery extends TernQuery {

	public TernCompletionQuery() {
		super("completions");
	}

	public void setTypes(boolean types) {
		super.put("types", types);
	}

	public void setDocs(boolean docs) {
		super.put("docs", docs);
	}

	public void setUrls(boolean urls) {
		super.put("urls", urls);
	}

}
