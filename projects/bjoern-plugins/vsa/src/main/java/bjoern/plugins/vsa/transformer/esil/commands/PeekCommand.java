package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public class PeekCommand implements ESILCommand
{
	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet address = stack.pop().execute(stack).getValue();
		return new ValueSetContainer(ValueSet.newTop(DataWidth.R64));
	}
}
