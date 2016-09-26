package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;

import java.util.Deque;

public class ConditionalCommand implements ESILCommand
{

	private final String esilCode;

	public ConditionalCommand(String esilCode)
	{
		this.esilCode = esilCode;
	}

	@Override
	public ESILStackItem execute(Deque<ESILCommand> stack)
	{
		ValueSet operand = stack.pop().execute(stack).getValue();
		StridedInterval interval = operand.getValueOfGlobalRegion();
		if (interval.isZero())
		{
			return null;
		} else if (interval.isOne())
		{
			// emulate this.esilCode
			return null;
		} else
		{
			// emulate this.esilCode
			return null;
		}
	}
}
