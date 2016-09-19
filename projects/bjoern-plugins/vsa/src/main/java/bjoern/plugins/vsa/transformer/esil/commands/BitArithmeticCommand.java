package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

public abstract class BitArithmeticCommand implements ESILCommand
{
	@Override
	public final void execute(AbstractEnvironment env, ESILStack stack)
	{
		ValueSet result = execute(stack.pop().getValue(), stack.pop().getValue());
		stack.push(new ValueSetContainer(result));
	}

	public abstract ValueSet execute(ValueSet leftOperand, ValueSet rightOperand);
}
