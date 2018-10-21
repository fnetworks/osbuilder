package org.fnet.osbuilder.toolchain.repositories;

import org.fnet.osbuilder.Main;
import org.fnet.osbuilder.util.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static org.fnet.osbuilder.util.Uncheck.uncheck;

public class GNUDownloader {

	public static final URL GNU_URL = uncheck(() -> new URL("https://ftp.gnu.org/"));

	private List<String> entries = new ArrayList<>();

	private File localListFile;

	public void updateIndex() throws IOException {
		URL url = new URL(GNU_URL, "find.txt.gz");
		if (localListFile.exists() && localListFile.length() > 0) {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");

			ZonedDateTime remoteModified = LocalDateTime
					.parse(con.getHeaderField("Last-Modified"), DateTimeFormatter.RFC_1123_DATE_TIME)
					.atZone(ZoneId.systemDefault());
			FileTime localModified = Files.getLastModifiedTime(localListFile.toPath());
			if (localModified.toInstant().isAfter(remoteModified.toInstant())) {
				try (BufferedReader reader = new BufferedReader(new FileReader(localListFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (!line.isEmpty())
							entries.add(line);
					}
				}
				return;
			}
		}

		Util.createDirectory(localListFile.getParentFile());

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(url.openStream())));
		     BufferedWriter out = new BufferedWriter(new FileWriter(localListFile))) {
			entries.clear();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				if (!line.startsWith("./gnu/"))
					continue;

				line = line.substring("./gnu/".length());

				if (line.endsWith("README") || line.endsWith(".txt"))
					continue;

				entries.add(line);

				out.write(line);
				out.newLine();
			}
		}
		((ArrayList<String>) entries).trimToSize();
	}

	public List<String> find(Predicate<String> matcher) {
		return findStream(matcher).collect(Collectors.toList());
	}

	public Stream<String> findStream(Predicate<String> matcher) {
		if (entries.isEmpty())
			uncheck(this::updateIndex);
		return entries.parallelStream().filter(matcher);
	}

	public GNUDownloader() {
		this(new File(Main.getApplication().getTempDirectory(), "gnu_index.txt"));
	}

	public GNUDownloader(File localListFile) {
		this.localListFile = localListFile;
	}
}
