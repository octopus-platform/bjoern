package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public abstract class ArithmeticCommand implements ESILCommand {
	@Override
	public final ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ValueSet leftOperand = stack.pop().execute(stack, env).getValue();
		ValueSet rightOperand = stack.pop().execute(stack, env).getValue();
		ValueSet result = execute(leftOperand, rightOperand);
		return new ValueSetContainer(result);
	}

	protected abstract ValueSet execute(
			ValueSet leftOperand,
			ValueSet rightOperand);
}
