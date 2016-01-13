package server.components.cfgdump;

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
		//
		if (contains(vertex))
		{
			return;
		}
		super.addVertex(vertex);
	}

}
