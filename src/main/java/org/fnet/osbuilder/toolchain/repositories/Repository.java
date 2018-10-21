package org.fnet.osbuilder.toolchain.repositories;

import com.vdurmont.semver4j.Semver;

import java.net.URL;

public interface Repository {

	Semver[] listVersions(String id) throws ArtifactNotFoundException;

	URL getArchiveURL(String id, Semver version) throws ArtifactNotFoundException /* TODO */;

}
