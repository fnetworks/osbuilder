package org.fnet.osbuilder;

import org.fnet.osbuilder.util.Util;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class Application {

	private String[] arguments;

	private File programDirectory, tempDirectory;
	private File tempDownloadIndex;

	public Application(String[] arguments) {
		this.arguments = arguments;
	}

	public String[] getArguments() {
		return arguments;
	}

	public File getProgramDirectory() {
		if (programDirectory == null) {
			try {
				programDirectory = new File(Util.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			if (programDirectory.getName().endsWith("lib"))
				programDirectory = programDirectory.getParentFile();
		}
		return programDirectory;
	}

	public File getTempDirectory() {
		if (tempDirectory == null)
			tempDirectory = new File(getProgramDirectory(), "temp");
		return tempDirectory;
	}

	public File getTempDownloadIndex() {
		if (tempDownloadIndex == null)
			tempDownloadIndex = new File(getTempDirectory(), "temp_index.properties");
		return tempDownloadIndex;
	}

	public File getCachedOrDownload(URL url) throws IOException {
		Util.createDirectory(getTempDirectory());

		Properties props = new Properties();
		if (getTempDownloadIndex().exists()) {
			try (FileInputStream in = new FileInputStream(getTempDownloadIndex())) {
				props.load(in);
			}
		}

		String cachedFile = props.getProperty(url.toExternalForm());
		if (cachedFile != null)
			return new File(getTempDirectory(), cachedFile);

		Logger.info("Downloading " + url);
		String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
		File outputFile = new File(getTempDirectory(), System.currentTimeMillis() + "_" + fileName);
		try (ReadableByteChannel in = Channels.newChannel(url.openStream());
			 FileOutputStream outputStream = new FileOutputStream(outputFile)) {
			outputStream.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
		}

		try (FileOutputStream out = new FileOutputStream(getTempDownloadIndex())) {
			props.store(out, null);
		} catch (IOException e) {
			Logger.error(e, "Could not write downloads index, file will be redownloaded on next run");
		}

		return outputFile;
	}

}
