package org.fnet.osbuilder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
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

	public static List<File> listRecursive(File file, FileFilter filter) {
		List<File> fileList = new ArrayList<>();
		for (File f : Objects.requireNonNull(file.listFiles(f -> f.isDirectory() || filter.accept(f)))) {
			if (f.isDirectory())
				fileList.addAll(listRecursive(file, filter));
			else
				fileList.add(f);
		}
		return fileList;
	}

}
