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
public class GRUBComponent extends ToolchainComponent {

	public GRUBComponent() {
		super("grub", "GRUB");
	}

	@Override
	public void build(Semver version, Toolchain toolchain) throws Exception {
		File grubTar = Main.getApplication().getCachedOrDownload(
				Repositories.getRepositories().getArchiveURL(getArtifactID(), version));
		File rootDirectory = new File(toolchain.getDirectory(), getArtifactID());
		File grubSourceDirectory = new File(rootDirectory, "src");

		Util.createDirectory(grubSourceDirectory);
		Tool.getTool(Extractor.class, e -> e.supports(grubTar))
				.extract(grubTar, grubSourceDirectory, true, 1);

		ProcessRunner runner = new ProcessRunner();
		runner.pushDirectory(grubSourceDirectory);
		runner.run(new File(grubSourceDirectory, "configure"), "--target", toolchain.getTarget(),
				"--prefix", toolchain.getTargetDirectory(), "--disable-nls", "--disable-werror");
		runner.run("make");
		runner.run("make", "install");
	}
}
