package bjoern.plugins.radareimporter;

import bjoern.input.common.outputModules.OutputModule;
import bjoern.structures.Node;
import bjoern.structures.edges.DirectedEdge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class BlueprintsOutputModule implements OutputModule
{

	private static final Logger logger = LoggerFactory.getLogger(BlueprintsOutputModule.class);

	private final Graph graph;

	public BlueprintsOutputModule(Graph graph)
	{
		this.graph = graph;
	}

	@Override
	public void initialize(String outputDir) throws IOException
	{

	}

	@Override
	public void finish()
	{

	}

	@Override
	public void writeNode(Node node)
	{
		writeNodeNoReplace(node);
	}

	@Override
	public void writeNodeNoReplace(Node node)
	{
		Object id = node.createKey();
		Map<String, Object> properties = node.getProperties();
		try
		{
			Vertex vertex = graph.addVertex(id);
			for (Map.Entry<String, Object> entry : properties.entrySet())
			{
				if (entry.getValue() != null)
				{
					vertex.setProperty(entry.getKey(), entry.getValue());
				}
			}
		} catch (IllegalArgumentException e)
		{
			// node already added, but no replace requested
			logger.info("Node already added: " + node);
		}
	}

	@Override
	public void writeEdge(DirectedEdge edge)
	{
		try
		{
			Object id = 0;
			Vertex source = graph.getVertex(edge.getSourceKey());
			Vertex destination = graph.getVertex(edge.getDestKey());
			String label = edge.getType();
			graph.addEdge(id, source, destination, label);
		} catch (IllegalArgumentException e)
		{
			// source or destination node is missing
			logger.warn("Cannot add edge: " + edge);
		}
	}
}
