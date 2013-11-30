package tern.server.nodejs.process;

public interface NodejsProcessListener {

	void onStart(NodejsProcess server);

	void onData(NodejsProcess server, String line);

	void onStop(NodejsProcess server);
	
}
