package org.fnet.osbuilder;

import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.BuildTarget;
import org.fnet.osbuilder.os.targets.TargetRunner;
import org.fnet.osbuilder.os.targets.impl.HelpTarget;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class Main {

	private static Application application;

	public static void main(String[] args) throws Exception {
		Configurator.defaultConfig()
				.writer(new ConsoleWriter())
				.formatPattern("[{class_name}] {level} - {message}")
				.level(Level.INFO)
				.activate();

		application = new Application(args);

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
			Logger.error("Unknown target " + target);
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

	public static Application getApplication() {
		return application;
	}
}
