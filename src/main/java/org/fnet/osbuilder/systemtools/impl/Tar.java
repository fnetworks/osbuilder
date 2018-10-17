package org.fnet.osbuilder.systemtools.impl;

import com.google.auto.service.AutoService;
import org.fnet.osbuilder.Util;
import org.fnet.osbuilder.systemtools.Extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoService(Extractor.class)
public class Tar implements Extractor {
	@Override
	public String[] getSupportedExtensions() {
		return new String[]{
				"tar", "tar.gz", "tar.xz", "tar.bz2"
		};
	}

	@Override
	public void extract(File source, File target, boolean skipExisting, int stripLevels) throws IOException {
		List<String> cmd = new ArrayList<>();
		cmd.add("tar");
		String compressionArgument;
		switch (source.getName().split("\\.")[source.getName().split("\\.").length - 1]) {
			case "xz":
				compressionArgument = "J";
				break;
			case "gz":
				compressionArgument = "z";
				break;
			case "bz2":
				compressionArgument = "j";
				break;
			case "tar":
				compressionArgument = null;
				break;
			default:
				throw new IOException("Invalid compression type");
		}
		cmd.add("x" + (compressionArgument != null ? compressionArgument : "") + "f");
		cmd.add(source.getAbsolutePath());
		cmd.add("-C");
		cmd.add(target.getAbsolutePath());
		if (skipExisting)
			cmd.add("--skip-old-files");
		if (stripLevels > 0) {
			cmd.add("--strip");
			cmd.add(Integer.toString(stripLevels));
		}
		System.out.println(cmd);
		try {
			if (Util.defaultProcessBuilder(cmd).start().waitFor() != 0)
				throw new IOException("Could not extract file");
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getName() {
		return "tar";
	}

	@Override
	public boolean isInstalled() {
		try {
			return Runtime.getRuntime().exec("tar --version").waitFor() == 0;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
