package org.fnet.osbuilder.toolchain.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.toolchain.ComponentProvider;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.fnet.osbuilder.toolchain.versions.DirectoryListingParser;
import org.fnet.osbuilder.toolchain.versions.Version;
import org.fnet.osbuilder.toolchain.versions.VersionChooser;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

@AutoService(ComponentProvider.class)
public class GCCComponentProvider implements ComponentProvider {
	@Override
	public String getName() {
		return "gcc";
	}

	@Override
	public ToolchainComponent provideComponent(String version) {
		return new GCCComponent(version);
	}

	@Override
	public String getLatestVersion() throws IOException {
		return VersionChooser.chooseVersion(DirectoryListingParser.parse(new URL("https://ftp.gnu.org/gnu/gcc/")),
				Pattern.compile("^gcc-(?<version>" + Version.VERSION_PATTERN + ")/$"));
	}
}
