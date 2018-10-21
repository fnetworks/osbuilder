package org.fnet.osbuilder.toolchain.impl;

import com.google.auto.service.AutoService;
import com.vdurmont.semver4j.Semver;
import org.fnet.osbuilder.Main;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.systemtools.Extractor;
import org.fnet.osbuilder.systemtools.Tool;
import org.fnet.osbuilder.toolchain.Toolchain;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.fnet.osbuilder.toolchain.repositories.Repositories;
import org.fnet.osbuilder.util.Util;

import java.io.File;

@AutoService(ToolchainComponent.class)
public class BinutilsComponent extends ToolchainComponent {

	public BinutilsComponent() {
		super("binutils", "Binutils");
	}

	@Override
	public void build(Semver version, Toolchain toolchain) throws Exception {
		File binutilsTar = Main.getApplication().getCachedOrDownload(
				Repositories.getRepositories().getArchiveURL(getArtifactID(), version));
		File rootDirectory = new File(toolchain.getDirectory(), getArtifactID());
		File binutilsSourceDirectory = new File(rootDirectory, "src");
		File binutilsBuildDirectory = new File(rootDirectory, "build");

		Util.createDirectory(binutilsSourceDirectory);
		Tool.getTool(Extractor.class, e -> e.supports(binutilsTar))
				.extract(binutilsTar, binutilsSourceDirectory, true, 1);

		ProcessRunner runner = new ProcessRunner();
		Util.createDirectory(binutilsBuildDirectory);
		runner.pushDirectory(binutilsBuildDirectory);
		if (!new File(binutilsBuildDirectory, "Makefile").exists())
			runner.run(new File(binutilsSourceDirectory, "configure"), "--target", toolchain.getTarget(),
					"--prefix", toolchain.getTargetDirectory(), "--with-sysroot", "--disable-nls",
					"--disable-werror");
		runner.run("make", "--jobs", "4");
		runner.run("make", "install", "--jobs", "2");
	}
}
