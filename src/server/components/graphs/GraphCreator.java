package server.components.graphs;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public abstract class GraphCreator
{
	/**
	 * Create and return a graph containing the given vertices and edges.
	 * 
	 * @param vertices
	 *            A set of vertices.
	 * @param edges
	 *            A set of edges.
	 * @return A graph object G = (vertices, edges).
	 */
	protected Graph createGraph(Iterable<Vertex> vertices, Iterable<Edge> edges)
	{
		Graph sg = new TinkerGraph();
		for (Vertex vertex : vertices)
		{
			Vertex v = sg.addVertex(vertex.getId());
			for (String property : vertex.getPropertyKeys())
			{
				v.setProperty(property, vertex.getProperty(property));
			}
		}
		for (Edge edge : edges)
		{
			Vertex outVertex = sg
					.getVertex(edge.getVertex(Direction.OUT).getId());
			Vertex inVertex = sg
					.getVertex(edge.getVertex(Direction.IN).getId());
			Edge e = sg.addEdge(edge.getId(), outVertex, inVertex,
					edge.getLabel());
			for (String property : edge.getPropertyKeys())
			{
				e.setProperty(property, edge.getProperty(property));
			}
		}

		return sg;
	}

	/**
	 * For a set of vertices return all edges that are returned by function f
	 * and match the given predicate p.
	 * 
	 * @param vertices
	 *            A set of vertices.
	 * @param f
	 *            A function mapping vertices to edges.
	 * @param p
	 *            A predicate testing edges.
	 * @return A set of edges.
	 */
	protected Iterable<Edge> getInducedEdges(Iterable<Vertex> vertices,
			Function<Vertex, Iterable<Edge>> f, Predicate<Edge> p)
	{
		List<Edge> edges = new LinkedList<Edge>();
		for (Vertex vertex : vertices)
		{
			for (Edge edge : f.apply(vertex))
			{
				if (p.test(edge))
				{
					edges.add(edge);
				}
			}
		}
		return edges;
	}

}
