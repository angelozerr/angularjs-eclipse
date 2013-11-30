package tern.server.nodejs;

import org.json.simple.JSONObject;

import tern.server.ITernServer;
import tern.server.nodejs.protocol.TernDoc;

public class LoggingInterceptor implements IInterceptor {

	@Override
	public void handleRequest(TernDoc request, ITernServer server,
			String methodName) {
		System.out.println("-----------------------------------");
		System.out.println("Tern request#" + methodName + ": ");
		System.out.println(request.toJSONString());
	}

	@Override
	public void handleResponse(JSONObject response, ITernServer server,
			String methodName, long ellapsedTime) {
		System.out.println("");
		System.out.println("Tern response#" + methodName + " with "
				+ ellapsedTime + "ms: ");
		System.out.println(response.toJSONString());
		System.out.println("-----------------------------------");
	}

}
