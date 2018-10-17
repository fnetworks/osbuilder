package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.Util;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@AutoService(BuildTarget.class)
public class ISOTarget extends BuildTarget {
	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		File isoFile = new File("os.iso");
		File kernel = dependencyResults.get(0).getOutputFiles().get(0);

		if (!isoFile.exists() || isoFile.lastModified() < kernel.lastModified()) {
			File isoDirectory = new File(os.getBinaryDirectory(), "isodir");
			File bootDirectory = new File(isoDirectory, "boot");
			File grubDirectory = new File(bootDirectory, "grub");
			File grubConfig = new File(grubDirectory, "grub.cfg");
			Util.createDirectory(isoDirectory);
			Util.createDirectory(bootDirectory);
			Util.createDirectory(grubDirectory);

			File grub_mkrescue = new File(os.getToolchain().getTargetDirectory(), "bin/grub-mkrescue");

			File bootKernelFile = new File(bootDirectory, "kernel.bin");
			if (!bootKernelFile.exists() || bootKernelFile.lastModified() < kernel.lastModified())
				Files.copy(kernel.toPath(), bootKernelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			File grubResConfig = new File(os.getResourceDirectory(), "grub.cfg");
			if (!grubConfig.exists() || grubConfig.lastModified() < grubResConfig.lastModified()) {
				Files.copy(grubResConfig.toPath(), grubConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			ProcessRunner processRunner = new ProcessRunner();
			processRunner.acceptExitCodes(0);
			processRunner.run(grub_mkrescue, "-o", isoFile, isoDirectory);
			return new TargetResult(true, List.of(isoFile));
		}

		return new TargetResult(false, List.of(isoFile));
	}

	@Override
	public List<Class<? extends BuildTarget>> getDependencies() {
		return List.of(KernelTarget.class);
	}

	@Override
	public String[] getAliases() {
		return new String[] { "iso" };
	}
}
