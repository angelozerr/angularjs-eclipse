package tern.server.nodejs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Start {

	public static void main(String[] args) throws IOException, InterruptedException {
		File nodejsTernFile = new File(".");
		ProcessBuilder builder = new ProcessBuilder(createCommands(
				nodejsTernFile, 12345));
		
		Process process=builder.start();
		process.waitFor();
	}

	static List<String> createCommands(File nodejsTernFile, Integer port)
			throws IOException {
		List<String> commands = new LinkedList<String>();
		commands.add("node");
		commands.add(nodejsTernFile.getCanonicalPath());
		if (port != null) {
			commands.add("--port");
			commands.add(port.toString());
		}
		return commands;
	}
}
