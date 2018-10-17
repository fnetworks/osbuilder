package org.fnet.osbuilder.toolchain;

import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.Util;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolchainContext {

	private static final Pattern DOWNLOAD_INDEX_REGEX = Pattern.compile("\"(?<url>[^\"]*)\":\\w?\"(?<file>[^\"]*)\"");

	private final ProcessRunner runner;
	private Toolchain toolchain;

	private final File tempDownloadIndex;

	public ToolchainContext(Toolchain toolchain) {
		this.toolchain = toolchain;
		this.runner = new ProcessRunner();
		this.runner.acceptExitCodes(0);

		this.tempDownloadIndex = new File(toolchain.getTempDirectory(), "index.txt");
	}

	public ProcessRunner getRunner() {
		return runner;
	}

	public Toolchain getToolchain() {
		return toolchain;
	}

	public File downloadTemp(URL url) throws IOException {
		try {
			Util.createDirectory(toolchain.getTempDirectory());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			for (String line : Files.readAllLines(tempDownloadIndex.toPath())) {
				Matcher matcher = DOWNLOAD_INDEX_REGEX.matcher(line);
				if (!matcher.find())
					Logger.warn("Invalid line in downloads index");
				if (url.toExternalForm().equalsIgnoreCase(matcher.group("url")))
					return new File(toolchain.getTempDirectory(), matcher.group("file"));

			}
		} catch (IOException e) {
			Logger.warn(e, "Could not read downloads index");
		}

		Logger.info("Downloading " + url);
		String[] split = url.getPath().split("/");
		File outputFile = new File(toolchain.getTempDirectory(), System.currentTimeMillis() + "_" + split[split.length - 1]);
		try (ReadableByteChannel in = Channels.newChannel(url.openStream());
		     FileOutputStream outputStream = new FileOutputStream(outputFile)) {
			outputStream.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
		}
		try {
			Files.write(tempDownloadIndex.toPath(), Set.of(String.format("\"%s\":\"%s\"", url.toExternalForm(),
					outputFile.getName())), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			Logger.error(e, "Could not write downloads index, file will be redownloaded on next run");
		}
		return outputFile;
	}
}
