package org.fnet.osbuilder.os.targets.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.ProcessRunner;
import org.fnet.osbuilder.Util;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetResult;
import org.fnet.osbuilder.toolchain.ComponentProvider;
import org.fnet.osbuilder.toolchain.Toolchain;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.fnet.osbuilder.util.ConsoleInterface;
import org.fnet.osbuilder.util.StringFormatter;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@AutoService(BuildTarget.class)
public class InitTarget extends BuildTarget {
	@Override
	public TargetResult run(OperatingSystem unused, List<TargetResult> dependencyResults) throws Exception {
		OperatingSystem newOs = new OperatingSystem(new File(System.getProperty("user.dir")));

		ConsoleInterface console = new ConsoleInterface();

		newOs.setId(console.ask("ID", true));
		newOs.setName(console.ask("Name", newOs.getId()));
		newOs.setTarget(console.ask("Target", "i686-elf"));

		Logger.info("Adding default build components");
		newOs.getComponents().put("binutils", Toolchain.getComponentProviderByName("binutils").getLatestVersion());
		newOs.getComponents().put("gcc", Toolchain.getComponentProviderByName("gcc").getLatestVersion());
		newOs.getComponents().put("grub", Toolchain.getComponentProviderByName("grub").getLatestVersion());

		newOs.save();

		copyTemplates(newOs);

		if (console.askYesNo("Initialize git repository", true)) {
			try {
				initializeGit();
			} catch (Exception e) {
				Logger.warn("Could not initialize git repository: " + e.getMessage());
			}
		}

		return null;
	}

	private void initializeGit() throws IOException, InterruptedException {
		ProcessRunner runner = new ProcessRunner();
		runner.acceptExitCodes(0);
		runner.run("git", "init");

		try (PrintWriter writer = new PrintWriter(new FileWriter(".gitignore"))) {
			writer.println("toolchain/");
			writer.println("bin/");
		}
	}

	private void copyTemplates(OperatingSystem newOs) throws IOException {
		Map<String, String> formatParameters = new HashMap<>();
		formatParameters.put("os.id", newOs.getId());
		formatParameters.put("os.name", newOs.getName());
		formatParameters.put("os.target", newOs.getTarget());

		StringFormatter formatter = new StringFormatter();
		File templateDirectory = new File(Util.PROGRAM_DIRECTORY, "templates/project");
		for (File sourceFile : Util.listRecursive(templateDirectory)) {
			File targetFile = new File(newOs.getLoadSourceDirectory(), sourceFile.getAbsolutePath()
					.replace(templateDirectory.getAbsolutePath(), ""));
			Util.createDirectory(targetFile.getParentFile());
			String input = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
			Files.write(targetFile.toPath(), formatter.format(input, formatParameters).getBytes(StandardCharsets.UTF_8));
		}
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
