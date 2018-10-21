package org.fnet.osbuilder.toolchain;

import com.vdurmont.semver4j.Semver;

import java.util.Objects;

public class InstalledToolchainComponent {

	private ToolchainComponent component;
	private Semver version;

	public InstalledToolchainComponent(ToolchainComponent component, Semver version) {
		this.component = component;
		this.version = version;
	}

	public ToolchainComponent getComponent() {
		return component;
	}

	public Semver getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InstalledToolchainComponent)) return false;
		InstalledToolchainComponent that = (InstalledToolchainComponent) o;
		return Objects.equals(getComponent(), that.getComponent()) &&
				Objects.equals(getVersion(), that.getVersion());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getComponent(), getVersion());
	}
}
