package org.fnet.osbuilder.toolchain.impl;

import org.fnet.osbuilder.Util;
import org.fnet.osbuilder.systemtools.Extractor;
import org.fnet.osbuilder.systemtools.Tool;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.fnet.osbuilder.toolchain.ToolchainContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class BinutilsComponent implements ToolchainComponent {

	private String version;

	public BinutilsComponent(String version) {
		this.version = version;
	}

	@Override
	public void build(ToolchainContext ctx) throws IOException, InterruptedException {
		File binutilsTar = ctx.downloadTemp(new URL("https://ftp.gnu.org/gnu/binutils/binutils-"
				+ version + ".tar.xz"));
		File binutilsSourceDirectory = new File(ctx.getToolchain().getDirectory(), "binutils/src");
		File binutilsBuildDirectory = new File(ctx.getToolchain().getDirectory(), "binutils/build");
		Util.createDirectory(binutilsSourceDirectory);
		Tool.getTool(Extractor.class,
				e -> Arrays.asList(e.getSupportedExtensions()).contains("tar.xz"))
				.extract(binutilsTar, binutilsSourceDirectory, true, 1);
		Util.createDirectory(binutilsBuildDirectory);
		ctx.getRunner().pushDirectory(binutilsBuildDirectory);
		if (!new File(binutilsBuildDirectory, "Makefile").exists())
			ctx.getRunner().run(new File(binutilsSourceDirectory, "configure"), "--target", ctx.getToolchain().getTarget(),
					"--prefix", ctx.getToolchain().getTargetDirectory(), "--with-sysroot", "--disable-nls",
					"--disable-werror");
		ctx.getRunner().run("make", "--jobs", "4");
		ctx.getRunner().run("make", "install", "--jobs", "2");
	}

	public String getVersion() {
		return version;
	}
}
