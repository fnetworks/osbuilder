package org.fnet.osbuilder.toolchain;

import com.vdurmont.semver4j.Semver;

import java.io.IOException;

public interface ComponentProvider {

	String getName();

	ToolchainComponent provideComponent(String version);

	default String getLatestVersion() throws IOException {
		return null;
	}

	default Semver[] getVersionList() {
		return new Semver[0];
	}

	default boolean isRequired() {
		return false;
	}

}
