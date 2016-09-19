package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;

public class AndCommand extends BitArithmeticCommand
{
	@Override
	public ValueSet execute(ValueSet leftOperand, ValueSet rightOperand)
	{
		return leftOperand.and(rightOperand);
	}
}
