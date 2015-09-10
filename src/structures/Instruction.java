package structures;

import nodeStore.Node;
import nodeStore.NodeTypes;

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
