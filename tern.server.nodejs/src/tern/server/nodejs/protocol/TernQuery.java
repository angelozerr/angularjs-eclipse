package tern.server.nodejs.protocol;

import org.json.simple.JSONObject;

public class TernQuery extends JSONObject {

	public TernQuery(String type) {
		super.put("type", type);
	}

	public void setFile(String file) {
		super.put("file", file);
	}

	public void setEnd(Integer pos) {
		super.put("end", pos);
	}
	
	public void setLineCharPositions(boolean lineCharPositions) {
		super.put("lineCharPositions", lineCharPositions);
	}
}
