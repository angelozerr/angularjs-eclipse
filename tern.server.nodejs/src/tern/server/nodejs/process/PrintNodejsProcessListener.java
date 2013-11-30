package tern.server.nodejs.process;

public class PrintNodejsProcessListener implements NodejsProcessListener {

	private static final NodejsProcessListener INSTANCE = new PrintNodejsProcessListener();
	
	public static NodejsProcessListener getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onStart(NodejsProcess server) {
		System.out.println("Server started at " + server.getPort());
	}

	@Override
	public void onData(NodejsProcess server, String line) {
		System.out.println(line);
	}

	@Override
	public void onStop(NodejsProcess server) {
		System.out.println("Server stopped");
	}
}
