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
public class GCCComponent extends ToolchainComponent {

	public GCCComponent() {
		super("gcc", "GCC");
		super.getDependencies().add(BinutilsComponent.class);
	}

	@Override
	public void build(Semver version, Toolchain toolchain) throws Exception {
		File gccTar = Main.getApplication().getCachedOrDownload(
				Repositories.getRepositories().getArchiveURL(getArtifactID(), version));
		File rootDirectory = new File(toolchain.getDirectory(), getArtifactID());
		File gccSourceDirectory = new File(rootDirectory, "src");
		File gccBuildDirectory = new File(rootDirectory, "build");

		Util.createDirectory(gccSourceDirectory);
		Tool.getTool(Extractor.class, e -> e.supports(gccTar))
				.extract(gccTar, gccSourceDirectory, true, 1);

		ProcessRunner runner = new ProcessRunner();
		runner.pushDirectory(gccSourceDirectory);
		runner.run(new File(gccSourceDirectory, "contrib/download_prerequisites"));
		runner.popDirectory();
		Util.createDirectory(gccBuildDirectory);
		runner.pushDirectory(gccBuildDirectory);
		runner.exportPath(new File(toolchain.getTargetDirectory(), "bin"));
		if (!new File(gccBuildDirectory, "Makefile").exists())
			runner.run(new File(gccSourceDirectory, "configure"), "--target", toolchain.getTarget(),
					"--prefix", toolchain.getTargetDirectory(), "--disable-nls",
					"--enable-languages=c,c++", "--without-headers");
		runner.run("make", "all-gcc", "--jobs", "4");
		runner.run("make", "all-target-libgcc", "--jobs", "2");
		runner.run("make", "install-gcc", "--jobs", "2");
		runner.run("make", "install-target-libgcc", "--jobs", "2");
	}
}
