package exporters.structures.edges;

import java.io.IOException;

import exporters.radare.inputModule.Radare;
import exporters.radare.inputModule.RadareDisassemblyParser;
import exporters.structures.interpretations.DisassemblyLine;

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
