package bjoern.structures;

import bjoern.nodeStore.Node;
import bjoern.nodeStore.NodeTypes;

public class RootNode extends Node {

	public RootNode()
	{
		this.setType(NodeTypes.ROOT);
	}

}
