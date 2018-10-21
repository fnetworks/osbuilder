package org.fnet.osbuilder.toolchain.repositories.utilities;

public class Version implements Comparable<Version> {

	public static final String VERSION_PATTERN = "\\d+(\\.\\d+)*";

	private String version;

	public Version(String version) {
		if (version == null)
			throw new NullPointerException("version");
		if (!version.matches(VERSION_PATTERN))
			throw new IllegalArgumentException("Invalid version format");
		this.version = version;
	}

	@Override
	public int compareTo(Version other) {
		if (other == null)
			throw new NullPointerException("other");
		String[] thisParts = this.getVersion().split("\\.");
		String[] thatParts = other.getVersion().split("\\.");
		int length = Math.max(thisParts.length, thatParts.length);
		for (int i = 0; i < length; i++) {
			int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
			int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
			if (thisPart < thatPart)
				return -1;
			if (thisPart > thatPart)
				return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		return this.compareTo((Version) that) == 0;
	}

	@Override
	public String toString() {
		return version;
	}

	public String getVersion() {
		return version;
	}
}
