package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;

public class PeekCommand implements ESILCommand
{
	@Override
	public void execute(AbstractEnvironment env, ESILStack stack)
	{
		stack.pop();
		stack.pushValueSet(ValueSet.newTop(DataWidth.R64));
	}
}
