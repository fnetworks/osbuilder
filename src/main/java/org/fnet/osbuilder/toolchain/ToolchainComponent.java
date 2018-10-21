package org.fnet.osbuilder.toolchain;

import com.vdurmont.semver4j.Semver;
import org.fnet.osbuilder.toolchain.repositories.ArtifactNotFoundException;
import org.fnet.osbuilder.toolchain.repositories.Repositories;

import java.util.*;

public abstract class ToolchainComponent {

	private static final List<ToolchainComponent> COMPONENT_LIST = new ArrayList<>();

	static {
		for (ToolchainComponent component : ServiceLoader.load(ToolchainComponent.class)) {
			COMPONENT_LIST.add(component);
		}
	}

	public static List<ToolchainComponent> getComponents() {
		return COMPONENT_LIST;
	}

	public static ToolchainComponent getComponentByID(String id) {
		for (ToolchainComponent component : COMPONENT_LIST)
			if (component.getArtifactID().equals(id))
				return component;
		return null;
	}

	public static ToolchainComponent getComponentByClass(Class<? extends ToolchainComponent> cls) {
		for (ToolchainComponent component : COMPONENT_LIST)
			if (component.getClass().equals(cls))
				return component;
		return null;
	}


	private String artifactID, displayName;
	private Set<Class<? extends ToolchainComponent>> dependencies;

	public ToolchainComponent(String artifactID, String displayName) {
		this.artifactID = artifactID;
		this.displayName = displayName;
		this.dependencies = new HashSet<>();
	}

	public abstract void build(Semver version, Toolchain toolchain) throws Exception;

	public Semver[] getVersions() throws ArtifactNotFoundException {
		return Repositories.getRepositories().listVersions(getArtifactID());
	}

	public Semver getLatestVersion() throws ArtifactNotFoundException {
		return Arrays.stream(getVersions()).max(Comparator.comparing(f -> f)).orElseThrow(() -> new ArtifactNotFoundException(""));
	}

	public Set<Class<? extends ToolchainComponent>> getDependencies() {
		return dependencies;
	}

	public String getArtifactID() {
		return artifactID;
	}

	public String getDisplayName() {
		return displayName;
	}
}

