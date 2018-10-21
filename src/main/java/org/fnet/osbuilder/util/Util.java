package org.fnet.osbuilder.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Util {

	public static void createDirectory(File directory) throws IOException {
		if (!directory.exists() && !directory.mkdirs())
			throw new IOException("Could not create directory " + directory);
	}

	public static ProcessBuilder defaultProcessBuilder(List<String> args) {
		return new ProcessBuilder(args).redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
	}

	public static List<File> listRecursive(File file) {
		List<File> fileList = new ArrayList<>();
		for (File f : Objects.requireNonNull(file.listFiles(e -> !e.equals(file)))) {
			if (f.isDirectory()) {
				if (!Files.isSymbolicLink(f.toPath()))
					fileList.addAll(listRecursive(f));
			} else {
				fileList.add(f);
			}
		}
		return fileList;
	}

	public static List<File> listRecursive(File file, FileFilter filter) {
		List<File> fileList = new ArrayList<>();
		for (File f : Objects.requireNonNull(file.listFiles(f -> f.isDirectory() || filter.accept(f)))) {
			if (f.isDirectory())
				fileList.addAll(listRecursive(f, filter));
			else
				fileList.add(f);
		}
		return fileList;
	}

	public static final File PROGRAM_DIRECTORY;

	public static final File TEMP_DIRECTORY;

	static {
		try {
			File progDir = new File(Util.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			if (progDir.getName().endsWith("lib"))
				progDir = progDir.getParentFile();
			PROGRAM_DIRECTORY = progDir;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		TEMP_DIRECTORY = new File(PROGRAM_DIRECTORY, "temp");
	}

}
