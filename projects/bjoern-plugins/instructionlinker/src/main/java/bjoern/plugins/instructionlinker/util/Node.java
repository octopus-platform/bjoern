package bjoern.plugins.instructionlinker.util;

import com.tinkerpop.blueprints.Vertex;

public class Node
{
	private final Vertex node;

	public Node(Vertex vertex)
	{
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
