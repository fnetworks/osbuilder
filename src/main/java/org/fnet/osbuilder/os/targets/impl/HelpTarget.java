package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.util.List;

@AutoService(BuildTarget.class)
public class HelpTarget extends BuildTarget {
	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		System.out.println("OSBuilder v1.0");
		System.out.println();

		for (BuildTarget target : BuildTarget.getAll()) {
			System.out.print(String.join(",", target.getAliases()));
			System.out.print(": ");
			String helpText = target.getHelpText();
			System.out.println(helpText != null && !helpText.isEmpty() ? helpText : "-");
		}
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "help", "?" };
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
