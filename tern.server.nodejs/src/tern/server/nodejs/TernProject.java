package tern.server.nodejs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import tern.server.TernPlugin;
import tern.utils.IOUtils;

public class TernProject extends JSONObject {

	private final File projectDir;
	private final List<String> libs;
	private final JSONObject plugins;

	private List<String> patterns;

	public TernProject(File projectDir) {
		this.projectDir = projectDir;
		this.libs = new ArrayList<String>();
		super.put("libs", libs);
		this.plugins = new JSONObject();
		super.put("plugins", plugins);
	}

	public void addLib(String lib) {
		this.libs.add(lib);
	}

	public void addPlugin(TernPlugin plugin) {
		this.plugins.put(plugin.name(), "./");
	}

	public void addLoadEagerlyPattern(String pattern) {
		if (patterns == null) {
			patterns = new ArrayList<String>();
			super.put("loadEagerly", patterns);
		}
		patterns.add(pattern);
	}

	public void save() throws IOException {
		projectDir.mkdirs();
		Writer writer = null;
		try {
			writer = new FileWriter(new File(projectDir, ".tern-project"));
			super.writeJSONString(writer);
		} finally {
			if (writer != null) {
				IOUtils.closeQuietly(writer);
			}
		}
	}
}
