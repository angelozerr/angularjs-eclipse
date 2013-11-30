package tern.server.nodejs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import tern.server.nodejs.process.NodejsProcess;
import tern.server.nodejs.process.PrintNodejsProcessListener;

// http://www.docmosis.com/docmosisfiles/samples/JavaProxyXMLExample.java.txt
// http://localhost:12345/?doc={}
// http://localhost:12345/ping
public class StartNodejsTernServer {

	public static void main(String[] args) throws MalformedURLException,
			IOException, InterruptedException {
		File nodejsTernBaseDir = new File(".");
		File projectDir = new File(".");
		NodejsProcess server = new NodejsProcess(nodejsTernBaseDir, projectDir);
		server.setPort(12345);
		server.setVerbose(true);
		
		server.addProcessListener(PrintNodejsProcessListener.getInstance());

		server.start();
		server.join();

	}
}
