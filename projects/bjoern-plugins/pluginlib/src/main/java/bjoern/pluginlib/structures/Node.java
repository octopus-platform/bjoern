package bjoern.pluginlib.structures;

import bjoern.structures.BjoernNodeProperties;
import com.tinkerpop.blueprints.Vertex;

public class Node
{
	private final Vertex node;

	public Node(Vertex vertex)
	{
		node = vertex;
	}

	public Node(Vertex vertex, String nodeType)
	{
		if (!vertex.getProperty(BjoernNodeProperties.TYPE).equals(nodeType))
		{
			throw new IllegalArgumentException("Invalid node. Expected a node of type " + nodeType + " was " + vertex
					.getProperty(BjoernNodeProperties.TYPE));
		}
		node = vertex;
	}

	public Vertex getNode()
	{
		return node;
	}

	@Override
	public String toString()
	{
		return getNode().toString();
	}

}
