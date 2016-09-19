package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

public abstract class ArithmeticCommand implements ESILCommand
{
	@Override
	public final void execute(AbstractEnvironment env, ESILStack stack)
	{
		ValueSet result = execute(stack.popValueSet(), stack.popValueSet());
		stack.push(new ValueSetContainer(result));
	}

	protected abstract ValueSet execute(ValueSet leftOperand, ValueSet rightOperand);
}
