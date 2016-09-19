package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;

public class SubCommand extends ArithmeticCommand
{
	@Override
	protected ValueSet execute(ValueSet leftOperand, ValueSet rightOperand)
	{
		return leftOperand.sub(rightOperand);
	}
}
