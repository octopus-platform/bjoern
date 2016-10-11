package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;

import java.util.Deque;

public class IncCommand implements ESILCommand {
	@Override
	public ESILStackItem execute(
			Deque<ESILCommand> stack, AbstractEnvironment env) {

		ValueSet operand = stack.pop().execute(stack, env).getValue();
		ValueSet result = operand.add(ValueSet
				.newGlobal(
						StridedInterval.getSingletonSet(1, DataWidth.R64)));
		return new ValueSetContainer(result);
	}
}

