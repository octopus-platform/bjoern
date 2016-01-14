package exporters.structures.interpretations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exporters.structures.annotations.VariableOrArgument;

public class DisassembledFunction
{

	private long funcAddress = 0;
	List<VariableOrArgument> varsAndArgs = new LinkedList<VariableOrArgument>();
	HashMap<Long, DisassemblyLine> addrToLine = new HashMap<Long, DisassemblyLine>();

	public void setFuncAddress(long funcAddress)
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
