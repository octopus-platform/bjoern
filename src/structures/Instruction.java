package structures;

import nodeStore.Node;
import nodeStore.NodeTypes;

public class Instruction extends Node
{
	public Instruction()
	{
		this.setType(NodeTypes.INSTRUCTION);
	}

}
