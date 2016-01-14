package server.components.cfgdump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class CFGGraphWrapper extends GraphWrapper
{
	private static final Logger logger = LoggerFactory
			.getLogger(CFGGraphWrapper.class);

	public CFGGraphWrapper(Graph graph)
	{
		super(graph);
	}

	@Override
	public void addVertex(Vertex vertex)
	{
		if (contains(vertex))
		{
			logger.debug("Ignoring vertex. Vertex is already contained.");
			return;
		}
		super.addVertex(vertex);
	}

	@Override
	public void addEdge(Edge edge)
	{
		if (!contains(edge.getVertex(Direction.OUT)))
		{
			logger.warn("Skipping edge. Tail is not contained.");
			return;
		}
		if (!contains(edge.getVertex(Direction.IN)))
		{
			logger.warn("Skipping edge. Head is not contained.");
			return;
		}
		super.addEdge(edge);
	}

}
