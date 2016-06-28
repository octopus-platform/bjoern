package bjoern.structures.edges;

import bjoern.structures.interpretations.DisassemblyLine;

public class CallRef extends Xref {

	private DisassemblyLine disassemblyLine;


	public DisassemblyLine getDisassemblyLine()
	{
		return disassemblyLine;
	}

	public void setDisassemblyLine(DisassemblyLine disassemblyLine)
	{
		this.disassemblyLine = disassemblyLine;
	}

}
