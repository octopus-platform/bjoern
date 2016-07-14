package bjoern.structures.edges;

import bjoern.nodeStore.NodeKey;

public class ControlFlowEdge extends DirectedEdge
{
	public ControlFlowEdge(NodeKey sourceKey, NodeKey destKey, String type)
	{
		super(sourceKey, destKey, type);
	}
}
