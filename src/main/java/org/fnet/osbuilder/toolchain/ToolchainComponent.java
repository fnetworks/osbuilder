package org.fnet.osbuilder.toolchain;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public interface ToolchainComponent {

	void build(ToolchainContext ctx) throws IOException, InterruptedException;

	String getVersion();

	default Set<Class<? extends ToolchainComponent>> getDependencies() {
		return Collections.emptySet();
	} // TODO

}

