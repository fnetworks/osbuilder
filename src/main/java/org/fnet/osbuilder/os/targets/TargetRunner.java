package org.fnet.osbuilder.os.targets;

import org.fnet.osbuilder.os.OperatingSystem;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TargetRunner {

	private ExecutorService executor;
	private OperatingSystem os;

	public TargetRunner() {
		this.os = null;
		this.executor = Executors.newFixedThreadPool(4);
	}

	public TargetRunner(OperatingSystem os) {
		this.os = os;
		this.executor = Executors.newFixedThreadPool(4);
	}

	public TargetRunner(OperatingSystem os, ExecutorService executor) {
		this.os = os;
		this.executor = executor;
	}

	private static BuildTarget getByClass(Class<? extends BuildTarget> target) {
		for (BuildTarget t : BuildTarget.getAll())
			if (t.getClass().equals(target))
				return t;
		throw new RuntimeException("Could not find BuildTarget by class");
	}

	private static void listDependencies(Class<? extends BuildTarget> target, Graph<BuildTarget, DefaultEdge> graph) {
		BuildTarget t = getByClass(target);
		if (graph.containsVertex(t))
			return;
		graph.addVertex(t);
		for (Class<? extends BuildTarget> dep : t.getDependencies()) {
			listDependencies(dep, graph);
			graph.addEdge(t, getByClass(dep));
		}

	}

//	public void runSync(Class<? extends BuildTarget> target) {
//		Graph<BuildTarget, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
//
//		listDependencies(target, graph);
//
//		CycleDetector<BuildTarget, DefaultEdge> cycleDector = new CycleDetector<>(graph);
//
//		if (cycleDector.detectCycles())
//			throw new RuntimeException("Cyclic graph");
//
//		List<BuildTarget> targets = new ArrayList<>();
//		for (TopologicalOrderIterator<BuildTarget, DefaultEdge> iter =
//			 new TopologicalOrderIterator<>(graph); iter.hasNext(); ) {
//			BuildTarget vertex = iter.next();
//			targets.add(vertex);
//		}
//
//		for (int i = targets.size() - 1; i >= 0; i--) {
//			targets.get(i).run(os, )
//		}
//	}

	public TargetResult run(BuildTarget target) throws Exception {
		System.out.println("Running target " + target.getClass().getSimpleName());
		return target.run(os, runAll(target.getDependencies()));
	}

	public TargetResult run(Class<? extends BuildTarget> target) throws Exception {
		System.out.println("Running target " + target.getSimpleName());
		BuildTarget t = getByClass(target);
		return t.run(os, runAll(t.getDependencies()));
	}

	public List<TargetResult> runAll(List<Class<? extends BuildTarget>> targets) throws Exception {
		List<TargetResult> results = new ArrayList<>();
		if (targets.isEmpty())
			return results;
//		List<Future<TargetResult>> futures = new ArrayList<>();
		for (Class<? extends BuildTarget> target : targets) {
//			BuildTarget t = getByClass(target);
//			futures.add(executor.submit(() -> t.run(os, runAll(t.getDependencies()))));
//			futures.add(executor.submit(() -> run(target)));
			results.add(run(target));
		}
//		for (Future<TargetResult> f : futures)
//			results.add(f.get());
		return results;
	}

	public OperatingSystem getOs() {
		return os;
	}

	public void setOs(OperatingSystem os) {
		this.os = os;
	}
}
