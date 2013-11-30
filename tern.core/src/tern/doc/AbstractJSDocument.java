package tern.doc;

import tern.server.ITernServer;

public abstract class AbstractJSDocument implements IJSDocument {

	private final ITernServer server;
	private final String name;
	private boolean changed;

	public AbstractJSDocument(String name, ITernServer server) {
		this(name, server, false);
	}

	public AbstractJSDocument(String name, ITernServer server, boolean register) {
		this.name = name;
		this.server = server;
		this.changed = false;
		if (register) {
			server.registerDoc(this);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public ITernServer getServer() {
		return server;
	}
}
