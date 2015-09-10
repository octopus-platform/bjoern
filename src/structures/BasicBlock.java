package structures;

import java.util.HashMap;

import nodeStore.Node;
import nodeStore.NodeTypes;

public class BasicBlock extends Node
{

	HashMap<Long, Instruction> instructions = new HashMap<Long, Instruction>();

	public BasicBlock()
	{
		this.setType(NodeTypes.BASIC_BLOCK);
	}

}
