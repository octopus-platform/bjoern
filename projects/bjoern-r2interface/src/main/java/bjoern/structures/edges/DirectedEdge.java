package bjoern.structures.edges;

import bjoern.structures.NodeKey;

public class DirectedEdge
{
	private NodeKey sourceKey;
	private NodeKey destKey;
	private String type;

	public DirectedEdge(NodeKey sourceKey, NodeKey destKey, String type)
	{
		setSourceKey(sourceKey);
		setDestKey(destKey);
		setType(type);
	}

	public NodeKey getSourceKey()
	{
		return sourceKey;
	}

	private void setSourceKey(NodeKey sourceKey)
	{
		this.sourceKey = sourceKey;
	}

	public NodeKey getDestKey()
	{
		return destKey;
	}

	private void setDestKey(NodeKey destKey)
	{
		this.destKey = destKey;
	}

	public String getType()
	{
		return type;
	}

	private void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "(" + sourceKey + ")--[" + type + "]-->(" + destKey + ")";
	}
}
