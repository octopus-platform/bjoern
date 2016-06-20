package octopus.lib.structures;

import com.tinkerpop.blueprints.Vertex;

public class OctopusNode
{
	private final Vertex node;

	public OctopusNode(Vertex vertex)
	{
		if (vertex == null)
		{
			throw new IllegalArgumentException("OctopusNode must not be null.");
		}
		node = vertex;
	}

	public OctopusNode(Vertex vertex, String nodeType)
	{
		if (!vertex.getProperty(OctopusNodeProperties.TYPE).equals(nodeType))
		{
			throw new IllegalArgumentException("Invalid node. Expected a node of type " + nodeType + " was " + vertex
					.getProperty(OctopusNodeProperties.TYPE));
		}
		node = vertex;
	}

	public Vertex getNode()
	{
		return node;
	}

	public long getId()
	{
		return Long.parseLong(getNode().getId().toString().split(":")[1]);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof OctopusNode))
		{
			return false;
		}

		OctopusNode other = (OctopusNode) o;
		return getNode().getId().equals(other.getNode().getId());
	}

	@Override
	public int hashCode()
	{
		return node != null ? node.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		String delimiter = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append(getNode().getProperty(OctopusNodeProperties.TYPE) + "(");
		for (String property : getNode().getPropertyKeys())
		{
			builder.append(property + ":" + getNode().getProperty(property));
			builder.append(delimiter);
		}
		builder.setLength(builder.length() - delimiter.length());
		builder.append(")");
		return builder.toString();
	}
}
