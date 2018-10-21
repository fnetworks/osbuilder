package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.util.Collections;
import java.util.List;

@AutoService(BuildTarget.class)
public class QemuTarget extends BuildTarget {

	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		String qemuCommand;
		switch (os.getTarget()) {
			case "i686-elf":
				qemuCommand = "qemu-system-i386";
				break;
			case "x86_64-elf":
				qemuCommand = "qemu-system-x86_64";
				break;
			default:
				throw new RuntimeException("Unsupported target " + os.getTarget());
		}

		ProcessRunner runner = new ProcessRunner();
		runner.acceptExitCodes(0);

		runner.run(qemuCommand, "-cdrom", dependencyResults.get(0).getOutputFiles().get(0));

		return new TargetResult(true, Collections.emptyList());
	}

	@Override
	public List<Class<? extends BuildTarget>> getDependencies() {
		return List.of(ISOTarget.class);
	}

	@Override
	public String[] getAliases() {
		return new String[]{"run", "qemu", "emulate"};
	}
}
