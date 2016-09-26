package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public abstract class BitArithmeticCommand implements ESILCommand
{
	@Override
	public final ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet leftOperand = stack.pop().execute(stack).getValue();
		ValueSet rightOperand = stack.pop().execute(stack).getValue();
		ValueSet result = execute(leftOperand, rightOperand);
		return new ValueSetContainer(result);
	}

	public abstract ValueSet execute(ValueSet leftOperand,
			ValueSet rightOperand);
}
