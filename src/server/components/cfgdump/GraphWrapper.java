package server.components.cfgdump;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class GraphWrapper
{

	private Graph graph;

	public GraphWrapper(Graph graph)
	{
		this.graph = graph;
	}

	public void addVertex(Vertex vertex)
	{
		Vertex v = graph.addVertex(vertex.getId());
		for (String property : vertex.getPropertyKeys())
		{
			v.setProperty(property, vertex.getProperty(property));
		}
	}

	public void addEdge(Edge edge)
	{
		Vertex tail = graph.getVertex(edge.getVertex(Direction.OUT).getId());
		Vertex head = graph.getVertex(edge.getVertex(Direction.IN).getId());
		Edge e = graph.addEdge(edge.getId(), tail, head, edge.getLabel());
		for (String property : edge.getPropertyKeys())
		{
			e.setProperty(property, edge.getProperty(property));
		}
	}

	public boolean contains(Vertex vertex)
	{
		return graph.getVertex(vertex.getId()) != null;
	}

	public boolean contains(Edge edge)
	{
		return graph.getEdge(edge.getId()) != null;
	}

	public Graph getGraph()
	{
		return this.graph;
	}

}
