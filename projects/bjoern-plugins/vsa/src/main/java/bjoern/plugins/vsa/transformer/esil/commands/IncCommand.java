package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;

public class IncCommand implements ESILCommand
{
	@Override
	public void execute(AbstractEnvironment env, ESILStack stack)
	{
		stack.pushValueSet(ValueSet.newGlobal(StridedInterval.getSingletonSet(1, DataWidth.R64)));
		ESILCommandFactory.getCommand(ESILKeyword.ADD).execute(env, stack);
	}
}

