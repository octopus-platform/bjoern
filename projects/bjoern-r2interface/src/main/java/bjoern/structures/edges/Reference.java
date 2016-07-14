package bjoern.structures.edges;

import bjoern.nodeStore.NodeKey;

public abstract class Reference extends DirectedEdge
{
	public Reference(NodeKey sourceKey, NodeKey destKey, String type)
	{
		super(sourceKey, destKey, type);
	}
}
