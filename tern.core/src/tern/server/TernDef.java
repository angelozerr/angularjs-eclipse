package tern.server;

public enum TernDef {

	browser("tern/defs/browser.json"), ecma5("tern/defs/ecma5.json"), jquery("tern/defs/jquery.json");

	private final String path;

	private TernDef(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
