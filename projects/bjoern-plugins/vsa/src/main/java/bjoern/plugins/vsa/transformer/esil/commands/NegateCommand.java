package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public class NegateCommand implements ESILCommand
{
	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet result = stack.pop().execute(stack).getValue().negate();
		return new ValueSetContainer(result);
	}
}
