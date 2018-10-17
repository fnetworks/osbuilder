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
import java.util.Set;

public class GCCComponent implements ToolchainComponent {

	private String version;

	public GCCComponent(String version) {
		this.version = version;
	}

	@Override
	public void build(ToolchainContext ctx) throws IOException, InterruptedException {
		File gccTar = ctx.downloadTemp(new URL("https://ftp.gnu.org/gnu/gcc/gcc-" + version + "/gcc-"
				+ version + ".tar.xz"));
		File gccSourceDirectory = new File(ctx.getToolchain().getDirectory(), "gcc/src");
		File gccBuildDirectory = new File(ctx.getToolchain().getDirectory(), "gcc/build");
		Util.createDirectory(gccSourceDirectory);
		Tool.getTool(Extractor.class,
				e -> Arrays.asList(e.getSupportedExtensions()).contains("tar.xz"))
				.extract(gccTar, gccSourceDirectory, true, 1);
		ctx.getRunner().pushDirectory(gccSourceDirectory);
		ctx.getRunner().run(new File(gccSourceDirectory, "contrib/download_prerequisites"));
		ctx.getRunner().popDirectory();
		Util.createDirectory(gccBuildDirectory);
		ctx.getRunner().pushDirectory(gccBuildDirectory);
		ctx.getRunner().exportPath(new File(ctx.getToolchain().getTargetDirectory(), "bin"));
		if (!new File(gccBuildDirectory, "Makefile").exists())
			ctx.getRunner().run(new File(gccSourceDirectory, "configure"), "--target", ctx.getToolchain().getTarget(),
					"--prefix", ctx.getToolchain().getTargetDirectory(), "--disable-nls",
					"--enable-languages=c,c++", "--without-headers");
		ctx.getRunner().run("make", "all-gcc", "--jobs", "4");
		ctx.getRunner().run("make", "all-target-libgcc", "--jobs", "2");
		ctx.getRunner().run("make", "install-gcc", "--jobs", "2");
		ctx.getRunner().run("make", "install-target-libgcc", "--jobs", "2");
	}

	public String getVersion() {
		return version;
	}

	@Override
	public Set<Class<? extends ToolchainComponent>> getDependencies() {
		return Set.of(BinutilsComponent.class);
	}
}
