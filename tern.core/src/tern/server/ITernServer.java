package tern.server;

import java.io.IOException;

import tern.doc.IJSDocument;

public interface ITernServer {

	String getFile(String name);

	void addDef(TernDef def) throws IOException;

	void addPlugin(TernPlugin plugin) throws IOException;

	void addFile(String name, String text);

	void sendDoc(IJSDocument doc, IResponseHandler handler);

	void registerDoc(IJSDocument doc);

	void requestCompletion(IJSDocument doc, IResponseHandler handler,
			boolean dataAsJson);
}
