package bjoern.input.common.structures;

import bjoern.input.common.nodeStore.Node;
import bjoern.input.common.nodeStore.NodeTypes;

public class RootNode extends Node {

	public RootNode()
	{
		this.setType(NodeTypes.ROOT);
	}

}
