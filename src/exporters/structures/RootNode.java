package exporters.structures;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;

public class RootNode extends Node {

	public RootNode()
	{
		this.setType(NodeTypes.ROOT);
	}

}
