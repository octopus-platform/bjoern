package exporters.structures;

import exporters.nodeStore.Node;
import exporters.nodeStore.NodeTypes;

public class Instruction extends Node
{
	private String stringRepr;

	public Instruction()
	{
		this.setType(NodeTypes.INSTRUCTION);
	}

	public String getStringRepr()
	{
		return stringRepr;
	}

	public void setStringRepr(String stringRepr)
	{
		this.stringRepr = stringRepr;
	}

}
