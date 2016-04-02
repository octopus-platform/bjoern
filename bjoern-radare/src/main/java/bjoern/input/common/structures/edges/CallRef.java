package bjoern.input.common.structures.edges;

import java.io.IOException;

import bjoern.input.common.structures.interpretations.DisassemblyLine;
import bjoern.input.radare.inputModule.Radare;
import bjoern.input.radare.inputModule.RadareDisassemblyParser;

public class CallRef extends Xref {

	private DisassemblyLine disassemblyLine;

	public void initializeSourceInstruction()
	{
		long addr = this.getSourceKey().getAddress();

		RadareDisassemblyParser parser = new RadareDisassemblyParser();
		String line;
		try {
			line = Radare.getDisassemblyForInstructionAt(addr);
			DisassemblyLine parsedInstruction = parser.parseInstruction(line);
			setDisassemblyLine(parsedInstruction);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
