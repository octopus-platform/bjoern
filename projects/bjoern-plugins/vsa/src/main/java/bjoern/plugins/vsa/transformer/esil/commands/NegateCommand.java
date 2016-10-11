package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public class NegateCommand implements ESILCommand {
	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {
		ValueSet result = stack.pop().execute(stack, env).getValue().negate();
		return new ValueSetContainer(result);
	}
}
