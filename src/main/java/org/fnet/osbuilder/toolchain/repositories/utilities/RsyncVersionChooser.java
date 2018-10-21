package org.fnet.osbuilder.toolchain.repositories.utilities;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RsyncVersionChooser {


	public static List<String> listFilesInDirectory(String directory) throws Exception {
		RSync rsync = new RSync()
				.source(directory)
				.destination(".")
				.listOnly(true);
		CollectingProcessOutput output = rsync.execute();
		Pattern pattern = Pattern.compile("[ladrwx\\-]+\\s+[0-9,]+\\s[0-9/]+\\s[0-9:]+\\s(?<filename>.+)");
		Matcher matcher = pattern.matcher(output.getStdOut());
		List<String> files = new ArrayList<>();
		while (matcher.find()) {
			String filename = matcher.group("filename");
			if (filename.equals("."))
				continue;
			files.add(filename);
		}
		return files;
	}

	public static class VersionMeta {
		private Version version;
		private String compression;
		private String path;

		public VersionMeta(Version version, String compression, String path) {
			this.version = version;
			this.compression = compression;
			this.path = path;
		}

		public Version getVersion() {
			return version;
		}

		public String getCompression() {
			return compression;
		}

		public String getPath() {
			return path;
		}
	}

	public static VersionMeta getLatestPreferred(String directory, Pattern versionCompressionPattern) throws Exception {
		List<VersionMeta> metas = new ArrayList<>();
		for (String file : listFilesInDirectory(directory)) {
			Matcher matcher = versionCompressionPattern.matcher(file);
			if (!matcher.matches())
				continue;
			metas.add(new VersionMeta(new Version(matcher.group("version")), matcher.group("compression"), file));
		}
		List<VersionMeta> latest = getLatestMetas(metas);
		return getPreferredArchiveMeta(latest);
	}

	public static List<Version> listVersions(String directory, Pattern versionPattern) throws Exception {
		List<Version> versions = new ArrayList<>();
		for (String file : listFilesInDirectory(directory)) {
			Matcher matcher = versionPattern.matcher(file);
			if (!matcher.matches())
				continue;
			versions.add(new Version(matcher.group("version")));
		}
		return versions;
	}

	public static Version getLatest(List<Version> versions) {
		return versions.stream().max(Comparator.comparing(e -> e)).orElse(null);
	}

	public static List<VersionMeta> getLatestMetas(List<VersionMeta> metas) {
		List<VersionMeta> copy = new ArrayList<>(metas);
		copy.sort(Comparator.comparing(VersionMeta::getVersion));
		VersionMeta first = null;
		List<VersionMeta> latest = new ArrayList<>();
		for (int i = copy.size() - 1; i >= 0; i--) {
			VersionMeta versionMeta = copy.get(i);
			if (first == null)
				first = versionMeta;
			if (!first.getVersion().equals(versionMeta.getVersion()))
				break;
			latest.add(versionMeta);
		}
		return latest;
	}

	private static final Map<String, Integer> ARCHIVE_PREFERENCE_MAP = new HashMap<>();

	static {
		ARCHIVE_PREFERENCE_MAP.put("xz", 2);
		ARCHIVE_PREFERENCE_MAP.put("gz", 1);
		ARCHIVE_PREFERENCE_MAP.put("bz2", 1);
	}

	public static String getPreferredArchive(List<String> archives) {
		int highest = 0;
		String name = null;
		for (String e : archives) {
			String ending = e.substring(e.lastIndexOf('.') + 1);
			int priority = ARCHIVE_PREFERENCE_MAP.getOrDefault(ending, 0);
			if (priority > highest) {
				name = e;
				highest = priority;
			}
		}
		return name;
	}

	public static VersionMeta getPreferredArchiveMeta(List<VersionMeta> archives) {
		int highest = 0;
		VersionMeta meta = null;
		for (VersionMeta e : archives) {
			String ending = e.getCompression();
			int priority = ARCHIVE_PREFERENCE_MAP.getOrDefault(ending, 0);
			if (priority > highest) {
				meta = e;
				highest = priority;
			}
		}
		return meta;
	}

}
