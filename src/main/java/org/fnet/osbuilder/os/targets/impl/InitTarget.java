package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.io.File;
import java.util.List;
import java.util.Scanner;

@AutoService(BuildTarget.class)
public class InitTarget extends BuildTarget {
	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		OperatingSystem newOs = new OperatingSystem(new File(System.getProperty("user.dir")));
		Scanner sc = new Scanner(System.in);

		System.out.print("target [i686-elf]: ");
		String target = sc.nextLine();
		if (target.trim().isEmpty())
			target = "i686-elf";
		newOs.setTarget(target);

		newOs.save();
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[]{"init", "create"};
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
