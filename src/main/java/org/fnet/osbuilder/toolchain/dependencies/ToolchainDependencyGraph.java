package org.fnet.osbuilder.toolchain.dependencies;

import org.fnet.osbuilder.toolchain.InstalledToolchainComponent;
import org.fnet.osbuilder.toolchain.ToolchainComponent;
import org.fnet.osbuilder.toolchain.dependencies.DependencyGraph.Node;
import org.fnet.osbuilder.toolchain.repositories.ArtifactNotFoundException;

public class ToolchainDependencyGraph {

	private DependencyGraph<InstalledToolchainComponent> graph = new DependencyGraph<>();

	public ToolchainDependencyGraph() {
	}

	public Node<InstalledToolchainComponent> add(InstalledToolchainComponent component) throws ArtifactNotFoundException {
		Node<InstalledToolchainComponent> node = new Node<>(component);
		for (Class<? extends ToolchainComponent> dependency : component.getComponent().getDependencies()) {
			ToolchainComponent dependencyComponent = ToolchainComponent.getComponentByClass(dependency);
			Node<InstalledToolchainComponent> installedDependency = null;
			for (Node<InstalledToolchainComponent> gn : graph.getNodes()) {
				if (gn.value.getComponent().equals(dependencyComponent)) {
					installedDependency = gn;
					break;
				}
			}
			if (installedDependency == null) {
				InstalledToolchainComponent installedToolchainComponent = new InstalledToolchainComponent(
						dependencyComponent, dependencyComponent.getLatestVersion());
				installedDependency = add(installedToolchainComponent);
			}
			node.addEdge(installedDependency);
		}
		graph.addNode(node);
		return node;
	}

	public DependencyGraph<InstalledToolchainComponent> getGraph() {
		return graph;
	}
}
