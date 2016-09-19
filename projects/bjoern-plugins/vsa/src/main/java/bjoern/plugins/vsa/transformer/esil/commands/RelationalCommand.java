package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;

public class RelationalCommand implements ESILCommand
{
	@Override
	public final void execute(AbstractEnvironment env, ESILStack stack)
	{
		stack.pushValueSet(execute(stack.popValueSet(), stack.popValueSet()));
	}

	public ValueSet execute(ValueSet leftOperand, ValueSet rightOperand)
	{
		return ValueSet.newGlobal(StridedInterval.getTop(DataWidth.R1));
	}
}
