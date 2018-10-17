package org.fnet.osbuilder.os;

import org.fnet.osbuilder.toolchain.ComponentProvider;
import org.fnet.osbuilder.toolchain.Toolchain;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class OperatingSystem {

	private static final String PROJECT_FILE_NAME = "os.yml";
	private File loadSource, loadSourceDirectory;
	private Toolchain toolchain;
	private String target;
	private Map<String, String> components = new HashMap<>();

	public OperatingSystem(File file) {
		this.loadSourceDirectory = file;
		this.loadSource = new File(loadSourceDirectory, PROJECT_FILE_NAME);
	}

	public OperatingSystem(String target) {
		this.target = target;
		this.components = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		if (loadSource == null)
			throw new IllegalStateException("LoadSource can't be null");

		Map map;
		try (InputStream in = new FileInputStream(loadSource)) {
			map = new Yaml().load(in);
		}

		this.target = (String) map.getOrDefault("target", "i686-elf");
		this.components = (Map<String, String>) map.get("components");
	}

	public void save() throws IOException {
		if (loadSource == null)
			throw new IllegalStateException("LoadSource can't be null");

		Map<String, Object> map = new HashMap<>();
		map.put("target", target);
		map.put("components", components);
		Files.write(loadSource.toPath(), new Yaml().dumpAsMap(map).getBytes());
	}

	public void setupToolchain() throws IOException, InterruptedException {
		Map<String, ComponentProvider> cpm = new HashMap<>();
		for (ComponentProvider provider : ServiceLoader.load(ComponentProvider.class)) {
			if (provider.isRequired() && !components.containsKey(provider.getName()))
				components.put(provider.getName(), provider.getLatestVersion());
			cpm.put(provider.getName(), provider);
		}

		List<ToolchainComponent> components = new ArrayList<>(this.components.size());
		for (String component : this.components.keySet())
			components.add(cpm.get(component).provideComponent(this.components.get(component)));

		Toolchain toolchain = new Toolchain(new File(loadSourceDirectory, "toolchain/" + target), target,
				components.toArray(new ToolchainComponent[0]));
		toolchain.create();
		this.toolchain = toolchain;
	}

	@NotNull
	public File getBinaryDirectory() {
		return new File(loadSourceDirectory, "bin/");
	}

	public File getIntermediateBinaryDirectory() {
		return new File(getBinaryDirectory(), "intermediate/");
	}

	@NotNull
	public File getSourceDirectory() {
		return new File(loadSourceDirectory, "src/");
	}

	public File getResourceDirectory() {
		return new File(loadSourceDirectory, "res/");
	}

	public Toolchain getToolchain() {
		return toolchain;
	}

	public String getTarget() {
		return target;
	}

	public Map<String, String> getComponents() {
		return components;
	}

	public File getLoadSource() {
		return loadSource;
	}

	public void setLoadSource(File loadSource) {
		this.loadSourceDirectory = loadSource.getParentFile();
		this.loadSource = loadSource;
	}

	public File getLoadSourceDirectory() {
		return loadSourceDirectory;
	}

	public void setLoadSourceDirectory(File loadSourceDirectory) {
		this.loadSourceDirectory = loadSourceDirectory;
		this.loadSource = new File(loadSourceDirectory, PROJECT_FILE_NAME);
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
