package tern.server;

public enum TernPlugin {

	angular("tern/plugin/angular");

	private final String path;

	private TernPlugin(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
