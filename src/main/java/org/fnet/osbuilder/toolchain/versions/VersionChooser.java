package org.fnet.osbuilder.toolchain.versions;

import org.fnet.osbuilder.toolchain.versions.DirectoryListingParser.RemoteFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChooser {

	private static class Ver {
		private RemoteFile remoteFile;
		private Version version;
		private String compression;

		public Ver(RemoteFile remoteFile, Version version, String compression) {
			this.remoteFile = remoteFile;
			this.version = version;
			this.compression = compression;
		}
	}

	public static String chooseVersion(RemoteFile[] files, Pattern fileParserPattern) {
		List<Ver> versions = new ArrayList<>();
		Version latestVersion = null;

		for (RemoteFile file : files) {
			Matcher m = fileParserPattern.matcher(file.getName());
			if (!m.find())
				continue;
			Ver e = new Ver(file, new Version(m.group("version")), m.group("compression"));
			versions.add(e);
			if (latestVersion == null || latestVersion.compareTo(e.version) < 0)
				latestVersion = e.version;
		}

		List<Ver> filter = new ArrayList<>();
		for (Ver v : versions)
			if (v.version.equals(latestVersion))
				filter.add(v);

		for (Ver v : filter)
			if (v.compression != null && v.compression.equalsIgnoreCase("xz"))
				return v.version.getVersion();
		return filter.stream().findAny().map(e -> e.version.getVersion()).orElse(null);
	}

}
