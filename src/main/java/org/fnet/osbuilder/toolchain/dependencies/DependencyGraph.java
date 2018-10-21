package org.fnet.osbuilder.toolchain.dependencies;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyGraph<T> {

	static class Node<T> {
		public final T value;
		public final HashSet<Edge<T>> inEdges;
		public final HashSet<Edge<T>> outEdges;

		public Node(T value) {
			this.value = value;
			inEdges = new HashSet<>();
			outEdges = new HashSet<>();
		}

		public Node<T> addEdge(Node<T> node) {
			Edge<T> e = new Edge<>(this, node);
			outEdges.add(e);
			node.inEdges.add(e);
			return this;
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	static class Edge<T> {
		public final Node<T> from;
		public final Node<T> to;

		public Edge(Node<T> from, Node<T> to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Edge<?> edge = (Edge<?>) o;
			return Objects.equals(from, edge.from) &&
					Objects.equals(to, edge.to);
		}

		@Override
		public int hashCode() {
			return Objects.hash(from, to);
		}
	}

	private List<Node<T>> nodes = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public List<T> sort() {
		Node<T>[] allNodes = nodes.toArray(new Node[0]);

		ArrayList<Node<T>> L = new ArrayList<>();

		List<Node<T>> S = new ArrayList<>();
		for (Node<T> n : allNodes)
			if (n.inEdges.size() == 0)
				S.add(n);

		while (!S.isEmpty()) {
			Node<T> n = S.remove(0);
			L.add(n);

			for (Iterator<Edge<T>> it = n.outEdges.iterator(); it.hasNext(); ) {
				Edge<T> e = it.next();
				Node<T> m = e.to;
				it.remove();
				m.inEdges.remove(e);

				if (m.inEdges.isEmpty())
					S.add(m);
			}
		}

		for (Node<T> n : allNodes)
			if (!n.inEdges.isEmpty())
				throw new RuntimeException("Cycle present, topological sort not possible");
		return L.stream().map(e -> e.value).collect(Collectors.toList());
	}

	public List<Node<T>> getNodes() {
		return nodes;
	}

	public void addNode(Node<T> node) {
		if (!nodes.contains(node))
			nodes.add(node);
	}
}