package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class PokeCommand implements ESILCommand
{
	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet destinationAddress = stack.pop().execute(stack).getValue();
		ValueSet value = stack.pop().execute(stack).getValue();
		// write value to aloc at destinationAddress

		// this command returns nothing/no item is pushed on the stack
		return null;
	}
}
