package bjoern.structures.edges;

import bjoern.nodeStore.NodeKey;

public class CallRef extends Reference
{
	public CallRef(NodeKey sourceKey, NodeKey destKey)
	{
		super(sourceKey, destKey, EdgeTypes.CALL);
	}
}
