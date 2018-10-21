package org.fnet.osbuilder.toolchain.repositories;

import com.vdurmont.semver4j.Semver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class Repositories implements Repository {
	private static Repositories instance;

	public static Repositories getRepositories() {
		if (instance == null)
			instance = new Repositories();
		return instance;
	}

	private Repository[] repositories;

	private Repositories() {
		List<Repository> repositories = new ArrayList<>();
		ServiceLoader.load(Repository.class).forEach(repositories::add);
		this.repositories = repositories.toArray(new Repository[0]);
	}

	@Override
	public Semver[] listVersions(String id) throws ArtifactNotFoundException {
		for (Repository r : repositories) {
			try {
				return r.listVersions(id);
			} catch (ArtifactNotFoundException ignored) {
			}
		}
		throw new ArtifactNotFoundException(id);
	}

	@Override
	public URL getArchiveURL(String id, Semver version) throws ArtifactNotFoundException {
		for (Repository r : repositories) {
			try {
				return r.getArchiveURL(id, version);
			} catch (ArtifactNotFoundException ignored) {
			}
		}
		throw new ArtifactNotFoundException(id + '@' + version);
	}
}
