package org.fnet.osbuilder;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class ProcessRunner {

	@FunctionalInterface
	private interface ExitCodeChecker {
		boolean isValid(int exitCode);
	}

	private File currentDirectory = null;
	private List<File> directoryList = new ArrayList<>();
	private ExitCodeChecker exitCodeFunction;
	private final List<String> customPathFiles = new ArrayList<>();
	private final Map<String, String> customEnvironment = new HashMap<>();

	private Function<Object, String> stringifier = o -> {
		if (o instanceof String)
			return (String) o;
		if (o instanceof File)
			return tryCanonicalize((File) o);
		if (o == null)
			return "";
		return o.toString();
	};

	public ProcessRunner() {
		acceptExitCodes(0);
	}

	private String tryCanonicalize(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	public void setDirectory(File directory) throws IOException {
		if (!directory.exists() || !directory.isDirectory())
			throw new IOException("Directory does not exist or is no directory");
		this.currentDirectory = directory;
	}

	public void pushDirectory(File directory) throws IOException {
		setDirectory(directory);
		directoryList.add(directory);
	}

	public void popDirectory() throws IOException {
		if (directoryList.isEmpty())
			throw new RuntimeException("No directory to pop");
		setDirectory(directoryList.remove(directoryList.size() - 1));
	}

	public void popAll() throws IOException {
		setDirectory(directoryList.get(0));
		directoryList.clear();
	}

	public void acceptExitCodes(int... exitCodes) {
		this.exitCodeFunction = e -> {
			for (int i : exitCodes)
				if (i == e)
					return true;
			return false;
		};
	}

	public void acceptExitCodes(int exitCode) {
		this.exitCodeFunction = e -> e == exitCode;
	}

	public void exportPath(File directory) {
		customPathFiles.add(directory.getAbsolutePath());
	}

	public void exportEnv(String key, String value) {
		customEnvironment.put(key, value);
	}

	public void acceptAllExitCodes() {
		this.exitCodeFunction = e -> true;
	}

	public int run(String cmd, Object... args) throws IOException, InterruptedException {
		List<String> argumentList = new ArrayList<>(args.length + 1);
		argumentList.add(cmd);
		Arrays.stream(args).map(stringifier).forEach(argumentList::add);
		ProcessBuilder builder = new ProcessBuilder(argumentList);
		if (currentDirectory != null)
			builder.directory(currentDirectory);
		builder.redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
		if (!customPathFiles.isEmpty()) {
			Entry<String, String> pathEntry = null;
			for (Entry<String, String> e : builder.environment().entrySet()) {
				if (e.getKey().equalsIgnoreCase("path")) {
					pathEntry = e;
					break;
				}
			}
			StringBuilder pathBuilder = new StringBuilder(pathEntry != null ? pathEntry.getValue() : "");
			for (String path : customPathFiles) {
				if (pathBuilder.length() != 0)
					pathBuilder.append(File.pathSeparatorChar);
				pathBuilder.append(path);
			}
			builder.environment().put(pathEntry != null ? pathEntry.getKey() : "PATH", pathBuilder.toString());
		}
		if (!customEnvironment.isEmpty())
			builder.environment().putAll(customEnvironment);
		Logger.debug("EXEC: " + String.join(" ", builder.command()));
		return run(builder);
	}

	public int run(File cmd, Object... args) throws IOException, InterruptedException {
		return run(tryCanonicalize(cmd), args);
	}

	public int run(ProcessBuilder builder) throws IOException, InterruptedException {
		Process process = builder.start();
		int exitCode = process.waitFor();
		if (!exitCodeFunction.isValid(exitCode))
			throw new IOException("Process exited with unexpected exit code " + exitCode);
		return exitCode;
	}

}
