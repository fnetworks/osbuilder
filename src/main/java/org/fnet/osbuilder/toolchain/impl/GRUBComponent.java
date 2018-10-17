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

public class GRUBComponent implements ToolchainComponent {

	private String version;

	public GRUBComponent(String version) {
		this.version = version;
	}

	@Override
	public void build(ToolchainContext ctx) throws IOException, InterruptedException {
		File grubTar = ctx.downloadTemp(new URL("https://ftp.gnu.org/gnu/grub/grub-" + version + ".tar.gz"));
		File grubSourceDirectory = new File(ctx.getToolchain().getDirectory(), "grub/src");
		Util.createDirectory(grubSourceDirectory);
		Tool.getTool(Extractor.class,
				e -> Arrays.asList(e.getSupportedExtensions()).contains("tar.xz"))
				.extract(grubTar, grubSourceDirectory, true, 1);
		ctx.getRunner().pushDirectory(grubSourceDirectory);
		ctx.getRunner().run(new File(grubSourceDirectory, "configure"), "--target", ctx.getToolchain().getTarget(),
				"--prefix", ctx.getToolchain().getTargetDirectory(), "--disable-nls", "--disable-werror");
		ctx.getRunner().run("make");
		ctx.getRunner().run("make", "install");

	}

	@Override
	public String getVersion() {
		return version;
	}
}
