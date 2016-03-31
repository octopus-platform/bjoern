package bjoern.input.common.structures.interpretations;

import java.util.HashMap;

public class Disassembly {

	HashMap<Long, DisassemblyLine> addrToLine = new HashMap<Long, DisassemblyLine>();

	public void addLine(DisassemblyLine disasmLine)
	{
		addrToLine.put(disasmLine.getAddr(), disasmLine);
	}

	public DisassemblyLine getLineForAddr(long addr)
	{
		return addrToLine.get(addr);
	}

}
