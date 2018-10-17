package org.fnet.osbuilder.os.targets;

import org.fnet.osbuilder.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

public abstract class BuildTarget {

	public static final ServiceLoader<BuildTarget> LOADED = ServiceLoader.load(BuildTarget.class);

	public static List<BuildTarget> getAll() {
		List<BuildTarget> targets = new ArrayList<>();
		for (BuildTarget t : LOADED)
			targets.add(t);
		return targets;
	}

	public abstract TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception;

	public List<Class<? extends BuildTarget>> getDependencies() {
		return Collections.emptyList();
	}

	public abstract String[] getAliases();

	public boolean isGlobal() {
		return false;
	}

	public String getHelpText() {
		return "";
	}

}
