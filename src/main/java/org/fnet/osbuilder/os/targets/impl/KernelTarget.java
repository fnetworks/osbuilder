package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AutoService(BuildTarget.class)
public class KernelTarget extends BuildTarget {
	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		File kernel = new File(os.getBinaryDirectory(), "kernel.bin"); // TODO change

		if (dependencyResults.stream().noneMatch(TargetResult::isRebuilt) && kernel.exists())
			return new TargetResult(false, Collections.singletonList(kernel));

		ProcessRunner runner = new ProcessRunner();
		runner.acceptExitCodes(0);

		File gcc = new File(os.getToolchain().getTargetDirectory(), "bin/" + os.getTarget() + "-gcc");
		File grub_file = new File(os.getToolchain().getTargetDirectory(), "bin/grub-file");

		List<Object> linkerArgs = new ArrayList<>(Arrays.asList("-T", new File(os.getResourceDirectory(), "linker.ld"), "-o", kernel, "-O2",
				"-nostdlib", "-ffreestanding", "-lgcc"));
		linkerArgs.addAll(dependencyResults.stream().flatMap(e -> e.getOutputFiles().stream()).collect(Collectors.toList()));
		runner.run(gcc, linkerArgs.toArray());
		runner.run(grub_file, "--is-x86-multiboot", kernel);

		return new TargetResult(true, Collections.singletonList(kernel));
	}

	@Override
	public List<Class<? extends BuildTarget>> getDependencies() {
		return List.of(AssembleTarget.class, CompileCTarget.class, CompileCPPTarget.class);
	}

	@Override
	public String[] getAliases() {
		return new String[] { "kernel" };
	}
}
