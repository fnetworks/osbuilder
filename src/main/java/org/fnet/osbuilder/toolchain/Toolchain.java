package org.fnet.osbuilder.toolchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.*;

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

	private List<ToolchainComponent> components;
	private File directory, targetDirectory, tempDirectory, lockFile;
	private String target;
	private ToolchainVersionLock versionLock;

	public Toolchain(File directory, String target, ToolchainComponent... components) {
		this.directory = directory;
		this.target = target;
		this.targetDirectory = new File(directory, "binroot");
		this.tempDirectory = new File(directory, "temp/");
		this.components = Arrays.asList(components);
		this.lockFile = new File(directory, LOCK_NAME);

		if (lockFile.exists()) {
			try (FileReader reader = new FileReader(lockFile)) {
				this.versionLock = GSON.fromJson(reader, ToolchainVersionLock.class);
			} catch (IOException e) {
				throw new RuntimeException("Error while loading lock file", e);
			}
		}
	}

	public boolean exists() {
		return directory.exists() && Objects.requireNonNull(directory.listFiles()).length > 0;
	}

	public void create() throws IOException, InterruptedException {
		ToolchainContext ctx = new ToolchainContext(this);
		ctx.getRunner().exportPath(new File(getTargetDirectory(), "bin"));
//		if (versionLock != null)
//			System.out.println(versionLock.versions);
		for (ToolchainComponent c : components) {
			if (versionLock != null && versionLock.getVersions().containsKey(c.getClass().getName()) &&
					versionLock.getVersions().get(c.getClass().getName()).equalsIgnoreCase(c.getVersion()))
				continue;
			c.build(ctx);
			ctx.getRunner().popAll();
			if (versionLock == null)
				this.versionLock = new ToolchainVersionLock();
			this.versionLock.getVersions().put(c.getClass().getName(), c.getVersion());
			writeLockFile();
		}
	}

	private void writeLockFile() throws IOException {
		try (FileWriter reader = new FileWriter(lockFile)) {
			GSON.toJson(versionLock, reader);
		}
	}

	public File getTargetDirectory() {
		return targetDirectory;
	}

	public File getDirectory() {
		return directory;
	}

	public File getTempDirectory() {
		return tempDirectory;
	}

	public String getTarget() {
		return target;
	}

}
