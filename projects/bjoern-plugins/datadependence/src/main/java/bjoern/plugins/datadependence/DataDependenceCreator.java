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

	private static void addEdge(Vertex source, Vertex destination,
			Vertex object)
	{
		for (Edge edge : source.getEdges(Direction.OUT, LABEL))
		{
			if (edge.getVertex(Direction.IN).equals(destination))
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
