package org.fnet.osbuilder;

import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetRunner;
import org.fnet.osbuilder.os.targets.impl.HelpTarget;
import org.fnet.osbuilder.os.targets.impl.ISOTarget;
import org.fnet.osbuilder.os.targets.impl.KernelTarget;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Main {

	public static void main(String[] args) throws Exception {
		Configurator.defaultConfig()
				.writer(new ConsoleWriter())
				.level(Level.TRACE)
				.activate();

		String target;
		if (args.length == 0)
			target = "help";
		else
			target = args[0];

		Optional<BuildTarget> first = BuildTarget.getAll().stream()
				.filter(e -> Arrays.asList(e.getAliases()).contains(target.toLowerCase()))
				.findFirst();

		TargetRunner runner = new TargetRunner();

		if (!first.isPresent()) {
			System.err.println("Unknown target " + target);
			runner.run(HelpTarget.class);
			System.exit(1);
			return;
		}

		// TODO pass arguments to target
		// TODO check if toolchain required

		OperatingSystem system = null;
		if (!first.get().isGlobal()) {
			system = new OperatingSystem(new File(System.getProperty("user.dir")));
			system.load();
			system.setupToolchain();

			runner.setOs(system);
		}

		runner.run(first.get());

		if (system != null)
			system.save();
	}

}
