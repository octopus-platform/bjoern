package server.components.cfgdump;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class CFGGraphWrapper extends GraphWrapper
{

	public CFGGraphWrapper(Graph graph)
	{
		super(graph);
	}

	@Override
	public void addVertex(Vertex vertex)
	{
		if (contains(vertex))
		{
			return;
		}
		super.addVertex(vertex);
	}

	@Override
	public void addEdge(Edge edge)
	{
		if (!contains(edge.getVertex(Direction.OUT)))
		{
			return;
		}
		if (!contains(edge.getVertex(Direction.IN)))
		{
			return;
		}
		super.addEdge(edge);
	}

}
