package bjoern.structures.edges;

import bjoern.nodeStore.NodeKey;
import bjoern.structures.interpretations.DisassemblyLine;

public class CallRef extends Reference
{
	private DisassemblyLine disassemblyLine;

	public CallRef(NodeKey sourceKey, NodeKey destKey, String type)
	{
		super(sourceKey, destKey, type);
	}

	public DisassemblyLine getDisassemblyLine()
	{
		return disassemblyLine;
	}

	public void setDisassemblyLine(DisassemblyLine disassemblyLine)
	{
		this.disassemblyLine = disassemblyLine;
	}

}
