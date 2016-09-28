package bjoern.plugins.datadependence;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataDependenceCreator {
	private static final String LABEL = "REACHES";

	public static void createFromReachingDefinitions(
			Map<Vertex, Set<ReachingDefinitionAnalyser.Definition>>
					reachingDefinitions) {
		for (Map.Entry<Vertex, Set<ReachingDefinitionAnalyser.Definition>>
				entry : reachingDefinitions
				.entrySet()) {
			for (ReachingDefinitionAnalyser.Definition definition : entry
					.getValue()) {
				Object object = definition.getIdentifier();
				Vertex destination = entry.getKey();
				Set<String> useSet = getUseSet(destination);
				if (useSet.contains(object)) {
					Vertex source = definition.getLocation();
					addEdge(source, destination, object);
				}
			}
		}
	}

	/**
	 * Add an data dependence edge from {@code source} to {@code destination}
	 * with respect to the data object {@code object} (e.g. a variable/aloc).
	 * <p>
	 * An edge is only added if it does not exist already.
	 *
	 * @param source
	 * 		the source node of the data dependence, i.e.the node that changes the
	 * 		value of {@code object}
	 * @param destination
	 * 		the destination node of the data dependence, ie.e the node that reads
	 * 		the value of {@code object}
	 * @param object
	 * 		the data object.
	 */
	private static void addEdge(
			Vertex source, Vertex destination,
			Object object) {
		for (Edge edge : source.getEdges(Direction.OUT, LABEL)) {
			if (edge.getVertex(Direction.IN).equals(destination) && edge
					.getProperty("aloc").equals(object)) {
				// edge exists -> skip
				return;
			}
		}
		Edge edge = source.addEdge(LABEL, destination);
		edge.setProperty("aloc", object);
	}

	private static Set<String> getUseSet(Vertex destination) {
		Set<String> set = new HashSet<>();
		for (Vertex vertex : destination.getVertices(Direction.OUT, "READ")) {
			set.add(vertex.getProperty("name"));
		}
		return set;
	}
}
