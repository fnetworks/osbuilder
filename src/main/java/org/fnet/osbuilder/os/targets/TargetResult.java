package org.fnet.osbuilder.os.targets;

import java.io.File;
import java.util.List;

public class TargetResult {

	private boolean rebuilt;
	private List<File> outputFiles;

	public TargetResult(boolean rebuilt, List<File> outputFiles) {
		this.rebuilt = rebuilt;
		this.outputFiles = outputFiles;
	}

	public boolean isRebuilt() {
		return rebuilt;
	}

	public List<File> getOutputFiles() {
		return outputFiles;
	}
}
