package structures;

import java.util.LinkedList;
import java.util.List;

public class Disassembly
{

	List<VariableOrArgument> varsAndArgs = new LinkedList<VariableOrArgument>();

	public void addVarOrArg(VariableOrArgument parsedVarOrArg)
	{
		varsAndArgs.add(parsedVarOrArg);
	}

}
