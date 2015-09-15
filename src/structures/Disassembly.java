package structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Disassembly
{

	List<VariableOrArgument> varsAndArgs = new LinkedList<VariableOrArgument>();
	HashMap<Long, DisassemblyLine> addrToLine = new HashMap<Long, DisassemblyLine>();

	public void addVarOrArg(VariableOrArgument parsedVarOrArg)
	{
		varsAndArgs.add(parsedVarOrArg);
	}

	public void addLine(DisassemblyLine disasmLine)
	{
		addrToLine.put(disasmLine.getAddr(), disasmLine);
	}

	public DisassemblyLine getLineForAddr(long addr)
	{
		return addrToLine.get(addr);
	}

}
