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

public class NASMComponent implements ToolchainComponent {

	private String version;

	public NASMComponent(String version) {
		this.version = version;
	}

	@Override
	public void build(ToolchainContext ctx) throws IOException, InterruptedException {
		File nasmTar = ctx.downloadTemp(new URL("https://www.nasm.us/pub/nasm/releasebuilds/" + version + "/nasm-" + version + ".tar.xz"));
		File nasmSourceDirectory = new File(ctx.getToolchain().getDirectory(), "nasm/src");
		Util.createDirectory(nasmSourceDirectory);
		Tool.getTool(Extractor.class,
				e -> Arrays.asList(e.getSupportedExtensions()).contains("tar.xz"))
				.extract(nasmTar, nasmSourceDirectory, true, 1);
		ctx.getRunner().pushDirectory(nasmSourceDirectory);
		ctx.getRunner().run(new File(nasmSourceDirectory, "configure"), "--prefix", "target");
	}

	public String getVersion() {
		return version;
	}
}
