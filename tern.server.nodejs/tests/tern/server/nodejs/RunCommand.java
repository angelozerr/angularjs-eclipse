package tern.server.nodejs;

import java.io.IOException;

import org.json.simple.JSONObject;

import tern.server.nodejs.protocol.TernCompletionQuery;
import tern.server.nodejs.protocol.TernDoc;
import tern.server.nodejs.protocol.TernProtocolHelper;

public class RunCommand {

	public static void main(String[] args) throws IOException {

		TernDoc doc = new TernDoc();

		// query
		TernCompletionQuery query = new TernCompletionQuery();
		query.setTypes(true);
		query.setFile("#0");
		query.setEnd(13);
		doc.setQuery(query);

		// files
		String name = "myfile.js";
		String text = "var a = [];a.";
		doc.addFile(name, text, null);

		System.out.println(doc);

		JSONObject json = TernProtocolHelper.makeRequest(
				"http://localhost:12345/", doc, false, null, null, null);
		System.err.println(json);
	}
}
