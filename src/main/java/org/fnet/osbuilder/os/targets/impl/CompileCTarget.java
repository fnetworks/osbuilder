package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.Util;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@AutoService(BuildTarget.class)
public class CompileCTarget extends BuildTarget {

	private static final Pattern SOURCE_FILE_PATTERN = Pattern.compile(".*\\.c");

	@Override
	public TargetResult run(OperatingSystem os, List<TargetResult> dependencyResults) throws Exception {
		ProcessRunner runner = new ProcessRunner();
		runner.acceptExitCodes(0);

		File gcc = new File(os.getToolchain().getTargetDirectory(), "bin/" + os.getTarget() + "-gcc");

		int rebuilt = 0;
		List<File> outputFiles = new ArrayList<>();

		for (File sourceFile : Util.listRecursive(os.getSourceDirectory(),
				f -> SOURCE_FILE_PATTERN.matcher(f.getName()).matches())) {
			File targetFile = new File(os.getIntermediateBinaryDirectory(), sourceFile.getAbsolutePath()
					.replace(os.getSourceDirectory().getAbsolutePath(), "") + ".o");
			outputFiles.add(targetFile);
			if (targetFile.exists() && targetFile.lastModified() > sourceFile.lastModified())
				continue;
			Util.createDirectory(targetFile.getParentFile());
			runner.run(gcc, "-c", sourceFile, "-o", targetFile, "-O2", "-std=gnu99", "-ffreestanding",
					"-Wall", "-Wextra");
			rebuilt++;
		}

		return new TargetResult(rebuilt > 0, outputFiles);
	}

	@Override
	public String[] getAliases() {
		return new String[]{"compile-c"};
	}
}
