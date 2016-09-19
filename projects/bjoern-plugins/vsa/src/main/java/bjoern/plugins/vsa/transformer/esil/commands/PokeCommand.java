package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;

public class PokeCommand implements ESILCommand
{
	@Override
	public void execute(AbstractEnvironment env, ESILStack stack)
	{
		stack.pop();
		stack.pop();
	}
}
