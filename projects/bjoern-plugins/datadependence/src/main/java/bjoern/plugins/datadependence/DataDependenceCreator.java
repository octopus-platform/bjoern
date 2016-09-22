package bjoern.plugins.datadependence;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataDependenceCreator
{
	private static final String LABEL = "REACHES";

	public static void createFromReachingDefinitions(
			Map<Vertex, Set<Edge>> reachingDefinitions)
	{
		for (Map.Entry<Vertex, Set<Edge>> entry1 : reachingDefinitions
				.entrySet())
		{
			for (Edge edge : entry1.getValue())
			{
				Vertex object = edge.getVertex(Direction.IN);
				Vertex destination = entry1.getKey();
				Set<Vertex> useSet = getUseSet(destination);
				if (useSet.contains(object))
				{
					Vertex source = edge.getVertex(Direction.OUT);
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
	 * @param source      the source node of the data dependence, i.e.the node
	 *                    that changes the value of {@code object}
	 * @param destination the destination node of the data dependence, ie.e
	 *                    the node that reads the value of {@code object}
	 * @param object      the data object.
	 */
	private static void addEdge(Vertex source, Vertex destination,
			Vertex object)
	{
		for (Edge edge : source.getEdges(Direction.OUT, LABEL))
		{
			if (edge.getVertex(Direction.IN).equals(destination) && edge
					.getProperty("aloc").equals(object))
			{
				// edge exists -> skip
				return;
			}
		}
		Edge edge = source.addEdge(LABEL, destination);
		edge.setProperty("aloc", object.getId());
	}

	private static Set<Vertex> getUseSet(Vertex destination)
	{
		Set<Vertex> set = new HashSet<>();
		for (Vertex vertex : destination.getVertices(Direction.OUT, "READ"))
		{
			set.add(vertex);
		}
		return set;
	}
}
