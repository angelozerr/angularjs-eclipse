package tern.server.nodejs;

import java.io.IOException;

import org.json.simple.JSONObject;

import tern.server.nodejs.protocol.TernProtocolHelper;
import tern.server.nodejs.protocol.TernDoc;

public class SendBuffer {

	public static void main(String[] args) throws IOException {

		// {"files": [{"type": "full",
		// "name": relative_file(pfile),
		// "text": view_js_text(view)}]},

		// files
		String name = "myfile.js";
		String text = "var a = [];";

		TernDoc doc = new TernDoc();
		doc.addFile(name, text, null);
		System.out.println(doc);

		JSONObject json = TernProtocolHelper.makeRequest(
				"http://localhost:12345/", doc, false, null, null, null);
		System.err.println(json);
	}
}
