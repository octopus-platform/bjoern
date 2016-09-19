package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

public class NegateCommand implements ESILCommand
{
	@Override
	public void execute(AbstractEnvironment env, ESILStack stack)
	{
		stack.push(new ValueSetContainer(stack.pop().getValue().negate()));
	}
}
