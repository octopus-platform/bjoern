package bjoern.structures.interpretations;

import java.util.LinkedList;
import java.util.List;

import bjoern.structures.annotations.VariableOrArgument;

public class DisassembledFunction
{

	private long funcAddress = 0;
	List<VariableOrArgument> varsAndArgs = new LinkedList<VariableOrArgument>();
	Disassembly disassembly = new Disassembly();

	public void setFuncAddress(long funcAddress)
	{
		this.funcAddress = funcAddress;
	}

	public void addVarOrArg(VariableOrArgument parsedVarOrArg)
	{
		varsAndArgs.add(parsedVarOrArg);
	}

	public List<VariableOrArgument> getVariablesAndArguments()
	{
		return varsAndArgs;
	}

	public void addLine(DisassemblyLine disasmLine)
	{
		disassembly.addLine(disasmLine);
	}

	public DisassemblyLine getLineForAddr(long addr)
	{
		return disassembly.getLineForAddr(addr);
	}

	public long getAddress()
	{
		return this.funcAddress;
	}


}
