package org.fnet.osbuilder.systemtools;

import java.io.File;
import java.io.IOException;

public interface Extractor extends Tool {

	/**
	 * Returns extensions that are supported by this extractor, e.g. "tar.gz", "zip"
	 *
	 * @return extensions supported by this extractor
	 */
	String[] getSupportedExtensions();

	default void extract(File source, File target, int stripLevels) throws IOException {
		extract(source, target, false, stripLevels);
	}

	default void extract(File source, File target, boolean skipExisting) throws IOException {
		extract(source, target, skipExisting, 0);
	}

	default void extract(File source, File target) throws IOException {
		extract(source, target, false, 0);
	}

	void extract(File source, File target, boolean skipExisting, int stripLevels) throws IOException;

}
