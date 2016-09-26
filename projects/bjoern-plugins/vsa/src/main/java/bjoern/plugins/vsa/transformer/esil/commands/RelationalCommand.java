package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public class RelationalCommand implements ESILCommand
{
	@Override
	public final ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet leftOperand = stack.pop().execute(stack).getValue();
		ValueSet rightOperand = stack.pop().execute(stack).getValue();
		ValueSet result = execute(leftOperand, rightOperand);

		return new ValueSetContainer(result);
	}

	public ValueSet execute(ValueSet leftOperand, ValueSet rightOperand)
	{
		return ValueSet.newGlobal(StridedInterval.getTop(DataWidth.R1));
	}
}
