package bjoern.structures.edges;

import bjoern.structures.NodeKey;

public abstract class Reference extends DirectedEdge
{
	public Reference(NodeKey sourceKey, NodeKey destKey, String type)
	{
		super(sourceKey, destKey, type);
	}
}
