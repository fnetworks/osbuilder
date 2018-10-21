package org.fnet.osbuilder.toolchain;

import com.google.gson.Gson;
import com.vdurmont.semver4j.Semver;
import org.fnet.osbuilder.toolchain.dependencies.ToolchainDependencyGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Toolchain {

	public static class ToolchainVersionLock {
		private Map<String, String> versions;

		public Map<String, String> getVersions() {
			if (versions == null)
				versions = new HashMap<>();
			return versions;
		}
	}

	private static final String LOCK_NAME = "lock.json";
	private static final Gson GSON = new Gson();

	private String target;
	private File directory, targetDirectory;
	private File lockFile;
	private List<InstalledToolchainComponent> components = new ArrayList<>();
	private ToolchainVersionLock versionLock;

	public Toolchain(String target, File directory) {
		this.target = target;
		this.directory = directory;
		this.lockFile = new File(directory, LOCK_NAME);
		this.targetDirectory = new File(directory, "binroot");
	}

	public void addComponent(String id, Semver version) {
		addComponent(ToolchainComponent.getComponentByID(id), version);
	}

	public void addComponent(ToolchainComponent component, Semver version) {
		components.add(new InstalledToolchainComponent(component, version));
	}

	public void build() throws Exception {
		readVersionLock();

		ToolchainDependencyGraph graph = new ToolchainDependencyGraph();
		for (InstalledToolchainComponent component : components) {
			graph.add(component);
		}

		List<InstalledToolchainComponent> sorted = graph.getGraph().sort();
		for (int i = sorted.size() - 1; i >= 0; i--) {
			InstalledToolchainComponent component = sorted.get(i);
			if (getVersionLock().getVersions().containsKey(component.getComponent().getArtifactID())
				&& getVersionLock().getVersions().get(component.getComponent().getArtifactID()).equals(component.getVersion().getValue()))
				continue; // TODO uninstall previous version
			component.getComponent().build(component.getVersion(), this);
			getVersionLock().getVersions().put(component.getComponent().getArtifactID(), component.getVersion().getValue());
			writeVersionLock();
		}
	}

	private void writeVersionLock() throws IOException {
		try (FileWriter reader = new FileWriter(lockFile)) {
			GSON.toJson(versionLock, reader);
		}
	}

	private void readVersionLock() throws IOException {
		if (lockFile.exists()) {
			try (FileReader reader = new FileReader(lockFile)) {
				versionLock = GSON.fromJson(reader, ToolchainVersionLock.class);
			}
		} else {
			versionLock = new ToolchainVersionLock();
		}
	}

	public ToolchainVersionLock getVersionLock() {
		if (versionLock == null)
			versionLock = new ToolchainVersionLock();
		return versionLock;
	}

	public String getTarget() {
		return target;
	}

	public File getLockFile() {
		return lockFile;
	}

	public File getDirectory() {
		return directory;
	}

	public File getTargetDirectory() {
		return targetDirectory;
	}

	public List<InstalledToolchainComponent> getComponents() {
		return components;
	}
}
