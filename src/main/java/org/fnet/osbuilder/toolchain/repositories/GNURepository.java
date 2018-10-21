package org.fnet.osbuilder.toolchain.repositories;

import com.google.auto.service.AutoService;
import com.vdurmont.semver4j.Semver;
import org.fnet.osbuilder.Main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fnet.osbuilder.util.Uncheck.uncheck;

@AutoService(Repository.class)
public class GNURepository implements Repository {

	private GNUDownloader loader;

	public GNURepository() {
		this.loader = new GNUDownloader(new File(Main.getApplication().getTempDirectory(), "gnu_index.txt"));
	}

	@Override
	public Semver[] listVersions(String id) throws ArtifactNotFoundException {
		if (loader.findStream(e -> e.equals(id)).count() == 0)
			throw new ArtifactNotFoundException(id);


		Pattern pattern = Pattern.compile(id + "/" + id + "-(?<version>\\d+(\\.\\d+)+)(.tar.(gz|xz|bz2))?$");
		return loader.findStream(pattern.asPredicate())
				.map(e -> {
					Matcher matcher = pattern.matcher(e);
					if (!matcher.find())
						throw new RuntimeException(e);
					return matcher.group("version");
				})
				.distinct()
				.map(e -> new Semver(e, Semver.SemverType.LOOSE))
				.sorted()
				.toArray(Semver[]::new);
	}

	private static final Map<String, Integer> ARCHIVE_PREFERENCE_MAP = new HashMap<>();

	static {
		ARCHIVE_PREFERENCE_MAP.put("xz", 3);
		ARCHIVE_PREFERENCE_MAP.put("gz", 2);
		ARCHIVE_PREFERENCE_MAP.put("bz2", 1);
	}

	@Override
	public URL getArchiveURL(String id, Semver version) {
		String escapedVersion = Pattern.quote(version.getOriginalValue());
		Pattern pattern = Pattern.compile(id + "/" + id + "-" + escapedVersion + "(\\.tar\\.(gz|xz|bz2))?$");
		List<String> paths = loader.find(pattern.asPredicate());

		List<String> finalCandidates = new ArrayList<>();
		for (String s : paths) {
			if (s.endsWith(".tar.gz") || s.endsWith(".tar.xz") || s.endsWith(".tar.bz2")) {
				finalCandidates.add(s);
				continue;
			}
			Pattern pattern2 = Pattern.compile(Pattern.quote(s) + "/" + id + "-" + escapedVersion + "\\.tar\\.(gz|xz|bz2)$");
			finalCandidates.addAll(loader.find(pattern2.asPredicate()));
		}

		if (finalCandidates.size() == 0)
			return null;

		if (finalCandidates.size() == 1)
			return uncheck(() -> new URL(GNUDownloader.GNU_URL, "/gnu/" + finalCandidates.get(0)));

		int highest = 0;
		String name = null;
		for (String e : finalCandidates) {
			String ending = e.substring(e.lastIndexOf('.') + 1);
			int priority = ARCHIVE_PREFERENCE_MAP.getOrDefault(ending, 0);
			if (priority > highest) {
				name = e;
				highest = priority;
			}
		}

		String name2 = name;

		return uncheck(() -> new URL(GNUDownloader.GNU_URL, "/gnu/" + name2));
	}
}
