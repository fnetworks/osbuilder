package org.fnet.osbuilder.os;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;
import org.fnet.osbuilder.toolchain.Toolchain;
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


	private File loadSource, rootDirectory;
	private Toolchain toolchain;

	private String id, name;
	private String target;

	private Map<String, String> components = new HashMap<>();

	public OperatingSystem(File file) {
		this.rootDirectory = file;
		this.loadSource = new File(rootDirectory, PROJECT_FILE_NAME);
	}

	public OperatingSystem(String id, String target) {
		this.id = id;
		this.name = id;
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		if (loadSource == null)
			throw new IllegalStateException("LoadSource can't be null");

		Map map;
		try (InputStream in = new FileInputStream(loadSource)) {
			map = new Yaml().load(in);
		}

		this.id = (String) map.get("id");
		if (id == null || id.isEmpty())
			throw new NullPointerException("Name must be set");
		this.name = (String) map.getOrDefault("name", id);
		this.target = (String) map.getOrDefault("target", "i686-elf");
		this.components = (Map<String, String>) map.get("components");
	}

	public void save() throws IOException {
		if (loadSource == null)
			throw new IllegalStateException("LoadSource can't be null");

		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		map.put("target", target);
		map.put("components", components);
		Files.write(loadSource.toPath(), new Yaml().dumpAsMap(map).getBytes());
	}

	public void setupToolchain() throws Exception {
		Toolchain toolchain = new Toolchain(target, new File(rootDirectory, "toolchain/" + target));
		this.components.forEach((id, version) -> toolchain.addComponent(id, new Semver(version, SemverType.LOOSE)));
		toolchain.build();
		this.toolchain = toolchain;
	}

	public File getBuildDirectory() {
		return new File(getRootDirectory(), "build/");
	}

	public File getTempDirectory() {
		return new File(getBuildDirectory(), "tmp/");
	}

	@NotNull
	public File getBinaryDirectory() {
		return new File(getBuildDirectory(), "bin/");
	}

	public File getIntermediateBinaryDirectory() {
		return new File(getBinaryDirectory(), "intermediate/");
	}

	@NotNull
	public File getSourceDirectory() {
		return new File(getRootDirectory(), "src/");
	}

	public File getResourceDirectory() {
		return new File(getRootDirectory(), "res/");
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
		this.rootDirectory = loadSource.getParentFile();
		this.loadSource = loadSource;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
		this.loadSource = new File(rootDirectory, PROJECT_FILE_NAME);
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null)
			throw new NullPointerException("id");
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
