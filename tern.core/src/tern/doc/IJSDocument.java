package tern.doc;

import tern.server.ITernServer;

public interface IJSDocument {

	String getName();

	boolean isChanged();

	void setChanged(boolean changed);

	String getValue();

	int getCursor(String s);

	boolean somethingSelected();

	ITernServer getServer();
}
