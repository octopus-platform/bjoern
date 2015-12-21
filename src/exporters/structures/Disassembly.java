package exporters.structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Disassembly
{

	private final long funcAddress;
	List<VariableOrArgument> varsAndArgs = new LinkedList<VariableOrArgument>();
	HashMap<Long, DisassemblyLine> addrToLine = new HashMap<Long, DisassemblyLine>();

	public Disassembly(long funcAddress)
	{
		this.funcAddress = funcAddress;
	}

	public void addVarOrArg(VariableOrArgument parsedVarOrArg)
	{
		parsedVarOrArg.setAddr(funcAddress);
		varsAndArgs.add(parsedVarOrArg);
	}

	public void addLine(DisassemblyLine disasmLine)
	{
		addrToLine.put(disasmLine.getAddr(), disasmLine);
	}

	public List<VariableOrArgument> getVariablesAndArguments()
	{
		return varsAndArgs;
	}

	public DisassemblyLine getLineForAddr(long addr)
	{
		return addrToLine.get(addr);
	}

}
