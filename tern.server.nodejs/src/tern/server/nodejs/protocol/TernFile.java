package tern.server.nodejs.protocol;

import org.json.simple.JSONObject;

public class TernFile extends JSONObject {

	public TernFile(String name, String text, Integer offset) {
		if (offset != null) {
			super.put("type", "part");
			super.put("offset", offset);
		} else {
			super.put("type", "full");
		}
		super.put("name", name);
		super.put("text", text);
	}
}
